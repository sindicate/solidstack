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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
	static private final Logger log = LoggerFactory.getLogger( CompilerClassLoader.class );

	static private JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
	static private JavaFileManager standardFileManager = compiler.getStandardFileManager( null, null, null );

	private CustomFileManager fileManager = new CustomFileManager( standardFileManager );

	public CompilerClassLoader( ClassLoader parent )
	{
		super( parent );
		System.out.println( standardFileManager.getClass() );
	}

	public Class<?> compile( String className, CharSequence source )
	{
		SourceFile javaFile = new SourceFile( className, source );

		this.fileManager.start();

		CompilationTask task = compiler.getTask( null, this.fileManager, null, null, null, Arrays.asList( javaFile ) );
		Assert.isTrue( task.call() );
		// FIXME Need to capture the compile errors

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
		public JavaFileObject getJavaFileForOutput( Location location, String className, Kind kind, FileObject sibling )
		{
			ClassFile result = new ClassFile( className );
			this.classFiles.put( className, result );
			this.newClasses.add( result );
			log.debug( "getJavaFileForOutput {} {} {} {} -> {}", location, className, kind, sibling, result );
			return result;
		}

		@Override
		public Iterable<JavaFileObject> list( Location location, String packageName, Set<Kind> kinds, boolean recurse )
				throws IOException
		{
			log.debug( "list {} {} {} {}", location, packageName, kinds, recurse );
			if( location == StandardLocation.CLASS_PATH && kinds.contains( Kind.CLASS ) )
			{
				Assert.isFalse( recurse );

				List<JavaFileObject> classes = new ArrayList<JavaFileObject>();
				packageName = packageName + ".";
				int len = packageName.length();
				for( Entry<String, JavaFileObject> entry : this.classFiles.entrySet() )
				{
					String name = entry.getKey();
					if( name.substring( 0, len ).equals( packageName ) && name.substring( len ).indexOf( '.' ) < 0)
						classes.add( entry.getValue() );
				}

				if( !classes.isEmpty() )
					return new ClassIterable( super.list( location, packageName, kinds, recurse ).iterator(), classes.iterator() );
			}

			return new ClassIterable( super.list( location, packageName, kinds, recurse ).iterator(), null );
		}

		@Override
		public String inferBinaryName( Location location, JavaFileObject file )
		{
			String result;
			if( file instanceof ClassFile )
				result = ( (ClassFile)file ).getName();
			else
				result = super.inferBinaryName( location, file );
			log.debug( "inferBinaryName {} {} -> {}", location, file, result );
			return result;
		}

		@Override
		public ClassLoader getClassLoader( Location location )
		{
			ClassLoader result = super.getClassLoader( location );
			log.debug( "getClassLoader {} -> {}", location, result );
			return result;
		}

		@Override
		public boolean isSameFile( FileObject a, FileObject b )
		{
			boolean result = super.isSameFile( a, b );
			log.debug( "isSameFile {} {} -> {}", a, b, result );
			return result;
		}

		@Override
		public boolean handleOption( String current, Iterator<String> remaining )
		{
			boolean result = super.handleOption( current, remaining );
			log.debug( "handleOption {} {} -> {}", current, remaining, result );
			return result;
		}

		@Override
		public boolean hasLocation( Location location )
		{
			boolean result = super.hasLocation( location );
			log.debug( "hasLocation {} -> {}", location, result );
			return result;
		}

		@Override
		public int isSupportedOption( String option )
		{
			int result = super.isSupportedOption( option );
			log.debug( "isSupportedOption {} -> {}", option, result );
			return result;
		}

		@Override
		public JavaFileObject getJavaFileForInput( Location location, String className, Kind kind ) throws IOException
		{
			JavaFileObject result = super.getJavaFileForInput( location, className, kind );
			log.debug( "getJavaFileForInput {} {} {} -> {}", location, className, kind, result );
			return result;
		}

		@Override
		public FileObject getFileForInput( Location location, String packageName, String relativeName )
				throws IOException
		{
			FileObject result = super.getFileForInput( location, packageName, relativeName );
			log.debug( "getFileForInput {} {} {} -> {}", location, packageName, relativeName, result );
			return result;
		}

		@Override
		public FileObject getFileForOutput( Location location, String packageName, String relativeName,
				FileObject sibling ) throws IOException
		{
			FileObject result = super.getFileForOutput( location, packageName, relativeName, sibling );
			log.debug( "getFileForOutput {} {} {} {} -> {}", location, packageName, relativeName, sibling, result );
			return result;
		}

		@Override
		public void flush() throws IOException
		{
			log.debug( "flush" );
			super.flush();
		}

		@Override
		public void close() throws IOException
		{
			log.debug( "close" );
			super.close();
		}
	}

	static class SourceFile extends SimpleJavaFileObject
	{
		private CharSequence contents = null;

		public SourceFile( String className, CharSequence contents )
		{
			super( URI.create( "string:///" + className.replace( '.', '/' ) + Kind.SOURCE.extension ), Kind.SOURCE );
			this.contents = contents;
		}

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

	static class ClassIterable implements Iterable<JavaFileObject>
	{
		Iterator<JavaFileObject> parent;
		Iterator<JavaFileObject> myclasses;

		ClassIterable( Iterator<JavaFileObject> parent, Iterator<JavaFileObject> myclasses )
		{
			this.parent = parent;
			this.myclasses = myclasses;
		}

		@Override
		public Iterator<JavaFileObject> iterator()
		{
			return new Iterator<JavaFileObject>()
			{
				@Override
				public boolean hasNext()
				{
					return ClassIterable.this.parent.hasNext() || ClassIterable.this.myclasses != null && ClassIterable.this.myclasses.hasNext();
				}

				@Override
				public JavaFileObject next()
				{
					JavaFileObject result;
					if( ClassIterable.this.parent.hasNext() )
						result = ClassIterable.this.parent.next();
					else
						result = ClassIterable.this.myclasses.next();
					log.debug( "next -> {}", result );
					return result;
				}

				@Override
				public void remove()
				{
					throw new UnsupportedOperationException();
				}
			};
		}
	}
}
