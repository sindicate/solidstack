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
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import solidstack.lang.DynamicClassLoader;
import solidstack.lang.SystemException;


public class CompilerClassLoader extends DynamicClassLoader
{
	static final Logger log = LoggerFactory.getLogger( CompilerClassLoader.class );

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
		try
		{
			Writer out = new CharArrayWriter();
			DiagnosticListener<JavaFileObject> l = new DiagnosticListener<JavaFileObject>()
			{
				@Override
				public void report( Diagnostic<? extends JavaFileObject> diagnostic )
				{
					log.error( "diagnostic: {}", diagnostic.toString() );
				}
			};
			CompilationTask task = compiler.getTask( out, this.fileManager, l, null, null, Arrays.asList( javaFile ) );

			boolean result = task.call();
			String output = out.toString();
			if( output.length() > 0 )
				log.error( "out: {}", output );
			if( !result )
				throw new SystemException( "Compilation failed" );
		}
		finally
		{
			try
			{
				this.fileManager.close();
			}
			catch( IOException e )
			{
				throw new SystemException( e );
			}
		}

		List<ClassFile> classes = this.fileManager.getCompiledClasses();
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
}
