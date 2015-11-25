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
import java.lang.reflect.Method;

import org.fest.assertions.api.Assertions;
import org.testng.annotations.Test;


public class JavacTests
{
	@Test
	public void basicTest() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, IOException
	{
		String source;
		InputStream is = JavacTests.class.getResourceAsStream( "HelloWorld.avaj" );
		Assertions.assertThat( is ).isNotNull();
		try
		{
			InputStreamReader in = new InputStreamReader( is );
			char[] buffer = new char[ 4096 ];
			int len = in.read( buffer );
			source = new String( buffer, 0, len );
		}
		finally
		{
			is.close();
		}

		CompilerClassLoader compiler = new CompilerClassLoader( Thread.currentThread().getContextClassLoader() );
		Class<?> cls = compiler.compile( "solidstack.javac.HelloWorld", source );

		Assertions.assertThat( cls ).isNotNull();
		Assertions.assertThat( cls.getDeclaredMethods() ).hasSize( 1 );

		Object instance = cls.newInstance();
		Method sayHello = cls.getMethod( "sayHello" );
		Object result = sayHello.invoke( instance );

		Assertions.assertThat( result ).isExactlyInstanceOf( String.class );
		Assertions.assertThat( (String)result ).isEqualTo( "Hello World!" );
	}
}
