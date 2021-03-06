/*--
 * Copyright 2015 Ren� M. de Bloois
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

package solidstack.script;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.testng.Assert;

import solidstack.io.SourceException;
import solidstack.io.SourceReader;
import solidstack.script.scopes.Scope;

public class Util
{
	static public Object eval( SourceReader source, Object scope )
	{
		return load( source ).eval( scope );
	}

	static public Object eval( String expression )
	{
		return eval( expression, null );
	}

	static public Object eval( String expression, Object scope )
	{
		Script script = Script.compile( expression );
//		StringBuilder buffer = new StringBuilder();
//		script.writeTo( buffer );
//		System.out.println( buffer );
		return script.eval( scope );
	}

	static public void fail( Script script, Class<? extends Exception> exception, String message )
	{
		fail( script, null, exception, message );
	}

	static public void fail( Script script, Scope scope, Class<? extends Exception> exception, String message )
	{
		try
		{
			script.eval( scope );
			failBecauseExceptionWasNotThrown( exception );
		}
		catch( Exception t )
		{
			assertThat( t ).isExactlyInstanceOf( exception );
			assertThat( t ).hasMessageContaining( message );
		}
	}

	static public void fail( String expression, Class<? extends Exception> exception, String message )
	{
		fail( Script.compile( expression ), null, exception, message );
	}

	static public void fail( String expression, Scope scope, Class<? extends Exception> exception, String message )
	{
		fail( Script.compile( expression ), scope, exception, message );
	}

	static public void failParse( String expression, String message )
	{
		try
		{
			Script.compile( expression );
			Assert.fail( "Expected a SourceException" );
		}
		catch( SourceException e )
		{
			assertThat( e ).hasMessageContaining( message );
		}
	}

	static public Script load( SourceReader source )
	{
		return Script.compile( source );
	}

	static public String readFile( String file ) throws IOException
	{
		InputStream in = ScriptTests.class.getResourceAsStream( file );
		Reader reader = new InputStreamReader( in );
		char[] buffer = new char[ 1024 ];
		StringBuilder result = new StringBuilder();
		int len;
		while( ( len = reader.read( buffer ) ) >= 0 )
			result.append( buffer, 0, len );
		return result.toString();
	}

	static public void test( Script script, int expected )
	{
		Assert.assertEquals( script.eval(), expected );
	}

	static public void test( SourceReader source, Object expected )
	{
		test( source, null, expected );
	}

	static public void test( SourceReader source, Object scope, Object expected )
	{
		Assert.assertEquals( eval( source, scope ), expected );
	}

	static public void test( String expression, Object expected )
	{
		test( expression, null, expected );
	}

	static public void test( String expression, Object scope, Object expected )
	{
		Object result = eval( expression, scope );
		Assert.assertEquals( result, expected );
	}
}
