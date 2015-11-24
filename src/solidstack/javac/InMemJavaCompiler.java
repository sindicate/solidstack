package solidstack.javac;

import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileManager;
import javax.tools.ToolProvider;

import solidstack.lang.SystemException;


public class InMemJavaCompiler
{
	static private JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
	static private JavaFileManager standardFileManager = compiler.getStandardFileManager( null, null, null );

	static public Class<?> compile( String className, String source )
	{
		JavaFile javaFile = new JavaFile( className, source );
		CustomFileManager fileManager = new CustomFileManager( standardFileManager );

		CompilationTask task = compiler.getTask( null, fileManager, null, null, null, Arrays.asList( javaFile ) );
		boolean result = task.call();

		JavacClassLoader loader = new JavacClassLoader( Thread.currentThread().getContextClassLoader() ) ;

		Map<String, ClassFile> classFiles = fileManager.getClassFiles();
		for( Entry<String, ClassFile> entry : classFiles.entrySet() )
			loader.defineClass( entry.getKey(), entry.getValue().getBytes() );

		try
		{
			return loader.loadClass( className );
		}
		catch( ClassNotFoundException e )
		{
			throw new SystemException( e );
		}
	}
}
