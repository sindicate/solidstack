/*--
 * Copyright 2015 René M. de Bloois
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package solidstack.javac;

import java.io.CharArrayWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import solidstack.io.ByteMatrixInputStream;
import solidstack.io.ByteMatrixOutputStream;
import solidstack.lang.Assert;
import solidstack.lang.DynamicClassLoader;
import solidstack.lang.SystemException;


// Inspired by: https://github.com/OpenHFT/Java-Runtime-Compiler
public class CompilerClassLoader extends DynamicClassLoader
{
	static final Logger logger = LoggerFactory.getLogger( CompilerClassLoader.class );

	static private JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
	static private JavaFileManager standardFileManager = compiler.getStandardFileManager( null, null, null );

	private CustomFileManager fileManager = new CustomFileManager( standardFileManager );

	public CompilerClassLoader( ClassLoader parent )
	{
		super( parent );
	}

	public Class<?> compile( String className, CharSequence source )
	{
		SourceFile javaFile = new SourceFile( className, source );

		this.fileManager.start();

		Writer out = new CharArrayWriter();
		CompilationTask task = compiler.getTask( out, this.fileManager, null, null, null, Arrays.asList( javaFile ) );
		if( !task.call() )
			throw new CompilationFailedException( out.toString() );

		List<ClassFile> classes = this.fileManager.end();
		for( ClassFile classFile : classes )
			defineClass( classFile.getName(), classFile.getBytes() );

		try
		{
			return loadClass( className );
		}
		catch( ClassNotFoundException e )
		{
			throw new SystemException( e );
		}
	}

	static class CustomFileManager extends ForwardingJavaFileManager<JavaFileManager>
	{
		private final Map<String, JavaFileObject> classFiles = new HashMap<String, JavaFileObject>();
		private final List<ClassFile> newClasses = new ArrayList<ClassFile>();

		protected CustomFileManager( JavaFileManager fileManager )
		{
			super( fileManager );
			logger.debug( "JavaFileManager: {}", fileManager.getClass() );
		}

		public void start()
		{
			this.newClasses.clear();
		}

		public List<ClassFile> end()
		{
			return this.newClasses;
		}

		@Override
		public ClassLoader getClassLoader( Location location )
		{
			ClassLoader result = super.getClassLoader( location );
			logger.debug( "getClassLoader({}) -> {}", location, result );
			return result;
		}

		@Override
		public JavaFileObject getJavaFileForOutput( Location location, String className, Kind kind, FileObject sibling )
		{
			ClassFile result = new ClassFile( className );
			this.classFiles.put( className, result );
			this.newClasses.add( result );
			logger.debug( "getJavaFileForOutput {} {} {} {} -> {}", location, className, kind, sibling, result );
			return result;
		}

		@Override
		public Iterable<JavaFileObject> list( Location location, String packageName, Set<Kind> kinds, boolean recurse )
				throws IOException
		{
			Iterable<JavaFileObject> result;

			if( location == StandardLocation.CLASS_PATH && kinds.contains( Kind.CLASS ) )
			{
				Assert.isFalse( recurse );

				List<JavaFileObject> classes = new ArrayList<JavaFileObject>();
				packageName = packageName + ".";
				for( Entry<String, JavaFileObject> entry : this.classFiles.entrySet() )
					if( entry.getKey().startsWith( packageName ) ) // TODO What about child packages?
						classes.add( entry.getValue() );

				if( !classes.isEmpty() )
					result = new ClassIterable( super.list( location, packageName, kinds, recurse ).iterator(), classes );
				else
					try
					{
						ClassLoader loader = Thread.currentThread().getContextClassLoader();
						String pn = packageName;
						if( pn.endsWith( "." ) )
							pn = pn.substring( 0, pn.length() - 1 );
						pn = pn.replace( '.', '/' );
						logger.debug( pn );
						Enumeration<URL> urls = loader.getResources( pn );
						while( urls.hasMoreElements() ) {
							URL url = urls.nextElement();
							logger.debug( "Resource: {}", url.toString() );
							if( "file".equals( url.getProtocol() ) )
							{
								File folder = new File( url.toURI() );
								File[] files = folder.listFiles();
								for( File file : files )
									if( file.isFile() && file.getName().endsWith( ".class" ) )
										classes.add( new ClassPathFile( url.toURI() ) );
							}
							else if( "jar".equals( url.getProtocol() ) || "zip".equals( url.getProtocol() ) )
							{
								logger.debug( "Protocol: {}", url.getProtocol() );
								String path = url.toString();
								logger.debug( "Path: {}", path );
								int i = path.indexOf( '!' );
								logger.debug( "i: {}", i );
								if( i < 0 )
									throw new IllegalArgumentException( "Expecting a !" );
								path = path.substring( 4, i );
								logger.debug( "path: {}", path );
								URI uri = new URI( path );
								if( uri.getScheme() == null || uri.getScheme().length() == 1 )
									uri = new URI( "file:///" + path ); // TODO Hostname?
								logger.debug( "Nested resource: {}", uri.toString() );
								if( "file".equals( uri.getScheme() ) )
								{
									ZipFile zip = new ZipFile( new File( uri ) );
									try
									{
										Enumeration<? extends ZipEntry> entries = zip.entries();
										pn = pn + "/";
										while( entries.hasMoreElements() )
										{
											ZipEntry entry = entries.nextElement();
											if( !entry.isDirectory() )
												//												logger.debug( "ZipEntry: {}", entry.getName() );
												if( entry.getName().startsWith( pn ) )
												{
													String uri3 = url.getProtocol() + ":" + uri.toString() + "!" + entry.getName();
													if( uri3.startsWith( "zip:file:///" ) )
														uri3 = "jar:file:/" + uri3.substring( 12 );
//													classes.add( new InputStreamFile( uri3, zip.getInputStream( entry ) ) );
												}
										}
									}
									finally
									{
										zip.close();
									}
								}
								else
									throw new UnsupportedOperationException( "Unsupported scheme: " + uri.getScheme() );
							}
							else
								throw new UnsupportedOperationException( "Unsupported protocol: " + url.getProtocol() );
						}
//						if( !classes.isEmpty() )
							result = new ClassIterable( super.list( location, packageName, kinds, recurse ).iterator(), classes );
//						else
//							result = super.list( location, packageName, kinds, recurse );
					}
					catch( URISyntaxException e )
					{
						throw new IOException( e );
					}
			}
			else
				result = super.list( location, packageName, kinds, recurse );

			logger.debug( "list({}, {}, {}, {}) -> {}", location, packageName, kinds, recurse, result );

			return result;
		}

		@Override
		public String inferBinaryName( Location location, JavaFileObject file )
		{
			if( file instanceof ClassFile )
				return ( (ClassFile)file ).getName();
			if( file instanceof ClassPathFile )
				return ( (ClassPathFile)file ).getName();
			if( file instanceof InputStreamFile )
				return ( (InputStreamFile)file ).getName();
			return super.inferBinaryName( location, file );
		}

//		@Override
//		public ClassLoader getClassLoader( Location location )
//		{
//			ClassLoader result = super.getClassLoader( location );
//			log.debug( "getClassLoader {} -> {}", location, result );
//			return result;
//		}
//
//		@Override
//		public boolean isSameFile( FileObject a, FileObject b )
//		{
//			boolean result = super.isSameFile( a, b );
//			log.debug( "isSameFile {} {} -> {}", a, b, result );
//			return result;
//		}
//
//		@Override
//		public boolean handleOption( String current, Iterator<String> remaining )
//		{
//			boolean result = super.handleOption( current, remaining );
//			log.debug( "handleOption {} {} -> {}", current, remaining, result );
//			return result;
//		}
//
//		@Override
//		public boolean hasLocation( Location location )
//		{
//			boolean result = super.hasLocation( location );
//			log.debug( "hasLocation {} -> {}", location, result );
//			return result;
//		}
//
//		@Override
//		public int isSupportedOption( String option )
//		{
//			int result = super.isSupportedOption( option );
//			log.debug( "isSupportedOption {} -> {}", option, result );
//			return result;
//		}
//
//		@Override
//		public JavaFileObject getJavaFileForInput( Location location, String className, Kind kind ) throws IOException
//		{
//			JavaFileObject result = super.getJavaFileForInput( location, className, kind );
//			log.debug( "getJavaFileForInput {} {} {} -> {}", location, className, kind, result );
//			return result;
//		}
//
//		@Override
//		public FileObject getFileForInput( Location location, String packageName, String relativeName )
//				throws IOException
//		{
//			FileObject result = super.getFileForInput( location, packageName, relativeName );
//			log.debug( "getFileForInput {} {} {} -> {}", location, packageName, relativeName, result );
//			return result;
//		}
//
//		@Override
//		public FileObject getFileForOutput( Location location, String packageName, String relativeName,
//				FileObject sibling ) throws IOException
//		{
//			FileObject result = super.getFileForOutput( location, packageName, relativeName, sibling );
//			log.debug( "getFileForOutput {} {} {} {} -> {}", location, packageName, relativeName, sibling, result );
//			return result;
//		}
//
//		@Override
//		public void flush() throws IOException
//		{
//			log.debug( "flush" );
//			super.flush();
//		}
//
//		@Override
//		public void close() throws IOException
//		{
//			log.debug( "close" );
//			super.close();
//		}
	}

	static class SourceFile extends SimpleJavaFileObject
	{
		private CharSequence contents = null;

		public SourceFile( String className, CharSequence contents )
		{
			super( URI.create( "string:///" + className.replace( '.', '/' ) + Kind.SOURCE.extension ), Kind.SOURCE );
			this.contents = contents;
		}

//		@Override
//		public String toString()
//		{
//			return "SourceFile[" + getName() + "]";
//		}

		@Override
		public CharSequence getCharContent( boolean ignoreEncodingErrors )
		{
			return this.contents;
		}
	}

	static class ClassFile extends SimpleJavaFileObject
	{
		private ByteMatrixOutputStream bytes = new ByteMatrixOutputStream( 1024 );

		public ClassFile( String className )
		{
			super( URI.create( className ), Kind.CLASS );
		}

		@Override
		public OutputStream openOutputStream()
		{
			return this.bytes;
		}

//		@Override
//		public String toString()
//		{
//			return "ClassFile[" + getName() + "]";
//		}

		@Override
		public InputStream openInputStream() throws IOException
		{
			return new ByteMatrixInputStream( this.bytes.toByteMatrix() );
		}

		public byte[] getBytes()
		{
			return this.bytes.toByteArray();
		}
	}

	static class ClassPathFile extends SimpleJavaFileObject
	{
		private URI uri;

		public ClassPathFile( URI uri )
		{
			super( uri, Kind.CLASS );
			this.uri = uri;
		}

//		@Override
//		public String toString()
//		{
//			return "ClassPathFile[" + getName() + "]";
//		}

		@Override
		public OutputStream openOutputStream()
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public InputStream openInputStream() throws IOException
		{
			throw new UnsupportedOperationException();
			//return this.uri.toURL().openStream();
		}

		public byte[] getBytes()
		{
			throw new UnsupportedOperationException();
		}
	}

	static class InputStreamFile implements JavaFileObject
	{
		private InputStream in;
		private String name;

		public InputStreamFile( String name, InputStream in )
		{
			this.in = in;
			this.name = name;
		}

		@Override
		public String getName()
		{
			return this.name;
		}

		@Override
		public String toString()
		{
			return "InputStreamFile[" + getName() + "]";
		}

		@Override
		public OutputStream openOutputStream()
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public InputStream openInputStream() throws IOException
		{
			throw new UnsupportedOperationException();
//			return this.in;
		}

		@Override
		public URI toUri()
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public Reader openReader( boolean ignoreEncodingErrors ) throws IOException
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public CharSequence getCharContent( boolean ignoreEncodingErrors ) throws IOException
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public Writer openWriter() throws IOException
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public long getLastModified()
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean delete()
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public Kind getKind()
		{
			return Kind.CLASS;
		}

		@Override
		public boolean isNameCompatible( String simpleName, Kind kind )
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public NestingKind getNestingKind()
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public Modifier getAccessLevel()
		{
			throw new UnsupportedOperationException();
		}
	}

	static class ClassIterable implements Iterable<JavaFileObject>
	{
		Iterator<JavaFileObject> parent;
		List<JavaFileObject> myclasses;

		ClassIterable( Iterator<JavaFileObject> parent, List<JavaFileObject> myclasses )
		{
			this.parent = parent;
			this.myclasses = myclasses;
		}

		@Override
		public Iterator<JavaFileObject> iterator()
		{
			return new Iterator<JavaFileObject>()
			{
				private Iterator<JavaFileObject> iterator = ClassIterable.this.myclasses.iterator();

				@Override
				public boolean hasNext()
				{
					return ClassIterable.this.parent.hasNext() || this.iterator.hasNext();
				}

				@Override
				public JavaFileObject next()
				{
					if( ClassIterable.this.parent.hasNext() )
						return ClassIterable.this.parent.next();
					return this.iterator.next();
				}

				@Override
				public void remove()
				{
					throw new UnsupportedOperationException();
				}
			};
		}

		@Override
		public String toString()
		{
			StringBuilder result = new StringBuilder();
			boolean first = true;
			for( JavaFileObject javaFileObject : this.myclasses )
			{
				if( first )
					first = false;
				else
					result.append( ',' );
				result.append( javaFileObject.toString() );
			}

			return result.toString();
		}
	}
}
