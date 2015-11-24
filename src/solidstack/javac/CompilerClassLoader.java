package solidstack.javac;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;

import solidstack.lang.DynamicClassLoader;
import solidstack.lang.SystemException;


// Inspired by: https://github.com/OpenHFT/Java-Runtime-Compiler
public class CompilerClassLoader extends DynamicClassLoader
{
	static private JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
	static private JavaFileManager standardFileManager = compiler.getStandardFileManager( null, null, null );

	public CompilerClassLoader( ClassLoader parent )
	{
		super( parent );
	}

	public Class<?> compile( String className, CharSequence source )
	{
		JavaFile javaFile = new JavaFile( className, source );
		CustomFileManager fileManager = new CustomFileManager( standardFileManager );

		CompilationTask task = compiler.getTask( null, fileManager, null, null, null, Arrays.asList( javaFile ) );
		boolean result = task.call();

		Map<String, ClassFile> classFiles = fileManager.getClassFiles();
		for( Entry<String, ClassFile> entry : classFiles.entrySet() )
			defineClass( entry.getKey(), entry.getValue().getBytes() );

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
		private final Map<String, ClassFile> classFiles = new HashMap<String, ClassFile>();

		protected CustomFileManager( JavaFileManager fileManager )
		{
			super( fileManager );
		}

//		@Override
//		public JavaFileObject getJavaFileForInput( Location location, String className, Kind kind ) throws IOException
//		{
//			if( location == StandardLocation.CLASS_OUTPUT && this.classFiles.containsKey( className ) && kind == Kind.CLASS )
//				return this.classFiles.get( className );
//			return this.fileManager.getJavaFileForInput( location, className, kind );
//		}

		@Override
		public JavaFileObject getJavaFileForOutput( Location location, String className, Kind kind, FileObject sibling )
		{
			ClassFile result = new ClassFile( className );
			this.classFiles.put( className, result );
			return result;
		}

		public Map<String, ClassFile> getClassFiles()
		{
			return this.classFiles;
		}
	}

	static class JavaFile extends SimpleJavaFileObject
	{
		private CharSequence contents = null;

		public JavaFile( String className, CharSequence contents )
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
		private ByteArrayOutputStream baos = new ByteArrayOutputStream();

		public ClassFile( String className )
		{
			super( URI.create( className ), Kind.CLASS );
		}

		@Override
		public OutputStream openOutputStream()
		{
			return this.baos;
		}

		public byte[] getBytes()
		{
			return this.baos.toByteArray();
		}
	}
}
