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
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;

import org.assertj.core.api.Assertions;
import org.testng.annotations.Test;

import solidstack.script.java.Java;
import solidstack.script.java.MissingMethodException;


public class JavacTests
{
	private CompilerClassLoader compiler = new CompilerClassLoader( Thread.currentThread().getContextClassLoader() );

	@Test
	public void basicTest() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, IOException, MissingMethodException
	{
		String source = readFile( "HelloWorld.avaj" );

		Class<?> cls = this.compiler.compile( "solidstack.javac.HelloWorld", source );

		Assertions.assertThat( cls ).isNotNull();
		Assertions.assertThat( cls.getDeclaredMethods() ).hasSize( 1 );

		Object result = Java.invoke( Java.construct( cls ), "sayHello" );

		Assertions.assertThat( result ).isExactlyInstanceOf( String.class );
		Assertions.assertThat( (String)result ).isEqualTo( "Hello World!" );
	}

	@Test(dependsOnMethods="basicTest")
	public void dependentTest() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, IOException, MissingMethodException
	{
		String source = readFile( "HelloWorld2.avaj" );

		Class<?> cls = this.compiler.compile( "solidstack.javac.HelloWorld2", source );

		Assertions.assertThat( cls ).isNotNull();
		Assertions.assertThat( cls.getDeclaredMethods() ).hasSize( 1 );

		Object result = Java.invoke( Java.construct( cls ), "sayHello" );

		Assertions.assertThat( result ).isExactlyInstanceOf( String.class );
		Assertions.assertThat( (String)result ).isEqualTo( "Hello World!" );
	}

	private String readFile( String filename ) throws IOException
	{
		String result;
		InputStream is = JavacTests.class.getResourceAsStream( filename );
		Assertions.assertThat( is ).isNotNull();
		try
		{
			InputStreamReader in = new InputStreamReader( is );
			char[] buffer = new char[ 4096 ];
			int len = in.read( buffer );
			return new String( buffer, 0, len );
		}
		finally
		{
			is.close();
		}
	}
}
