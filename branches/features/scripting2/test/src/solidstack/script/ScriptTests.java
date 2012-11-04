/*--
 * Copyright 2012 Ren� M. de Bloois
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

import java.math.BigDecimal;
import java.util.ArrayList;

import org.testng.Assert;
import org.testng.annotations.Test;

import solidstack.io.SourceException;


@SuppressWarnings( "javadoc" )
public class ScriptTests
{
	@Test
	static public void test1()
	{
		Context context = new Context();
		context.set( "var1", "Value" );
		test( "var1", context, "Value" );
		test( "", null );
	}

	@Test
	static public void test2()
	{
		Context context = new Context();
		context.set( "var1", new BigDecimal( 1 ) );
		test( "var1 + 1", context, new BigDecimal( 2 ) );
	}

	@Test
	static public void test3()
	{
		test( "1 + 1 * 2", new BigDecimal( 3 ) );
		test( "2 * 1 + 1", new BigDecimal( 3 ) );
		test( "1 + 1 + 1", new BigDecimal( 3 ) );
		test( "1 + 2 * 2 + 1", new BigDecimal( 6 ) );
		test( "( 1 + 2 ) * 2 + 1", new BigDecimal( 7 ) );
		test( "1 + 2 * ( 2 + 1 )", new BigDecimal( 7 ) ); // TODO These () translate to Tuple instead of Parenthesis
		test( "( 1 + 2 ) * ( 2 + 1 )", new BigDecimal( 9 ) );
	}

	static public void test( String expression, Object expected )
	{
		test( expression, null, expected );
	}

	static public void test( String expression, Context context, Object expected )
	{
		Object result = eval( expression, context );
		Assert.assertEquals( result, expected );
	}

	static public Object eval( String expression )
	{
		return eval( expression, null );
	}

	static public Object eval( String expression, Context context )
	{
		Script script = Script.compile( expression );
//		String dump = new Dumper().dump( script );
		return script.execute( context );
	}

	static public void testParseFail( String expression )
	{
		try
		{
			Script.compile( expression );
			Assert.fail( "Expected a SourceException" );
		}
		catch( SourceException e )
		{
			// Expected
		}
	}

//	@Test
//	static public void test4()
//	{
//		int val = 1;
//		Assert.assertEquals( val > 0 ? 2 : 3 + 1, 2 );
//		Assert.assertEquals( val + 1 > 0 ? 2 : 3 + 1, 2 );
//		Assert.assertEquals( val > 0 ? 2 : val > 0 ? 3 : 4, 2 );
//
//		testParseFail( "1 ? 2 ; 3 : 4" );
//		test( "1 ? 2 : 3 + 1", new BigDecimal( 2 ) );
//		test( "( 1 ? 2 : 3 ) + 1", new BigDecimal( 3 ) );
//		test( "1 + 1 ? 2 : 3 + 1", new BigDecimal( 2 ) );
//		test( "1 + ( 1 ? 2 : 3 ) + 1", new BigDecimal( 4 ) );
//		test( "1 + ( 1 ? 2 : 3 + 1 )", new BigDecimal( 3 ) );
//		test( "( 1 + 1 ? 2 : 3 ) + 1", new BigDecimal( 3 ) );
//		test( "0 ? 2 : 3 + 4 * 5", new BigDecimal( 23 ) );
//		test( "0 ? 2 : ( 3 + 4 ) * 5", new BigDecimal( 35 ) );
//		test( "( 0 ? 2 : 3 ) + 4 * 5", new BigDecimal( 23 ) );
//		test( "( ( 0 ? 2 : 3 ) + 4 ) * 5", new BigDecimal( 35 ) );
//		test( "1 ? 1 ? 2 : 3 : 4", new BigDecimal( 2 ) );
//		test( "1 ? 0 ? 2 : 3 : 4", new BigDecimal( 3 ) );
//		test( "0 ? 0 ? 2 : 3 : 4", new BigDecimal( 4 ) );
//		test( "1 ? 2 : 1 ? 3 : 4", new BigDecimal( 2 ) );
//		test( "( 1 ? 2 : 1 ) ? 3 : 4", new BigDecimal( 3 ) );
//		test( "0 ? 2 : 1 ? 3 : 4", new BigDecimal( 3 ) );
//		test( "0 ? 2 : 0 ? 3 : 4", new BigDecimal( 4 ) );
//		test( "1 ? 2 : 3 + 4 ? 5 : 6", new BigDecimal( 2 ) );
//		test( "( 1 ? 2 : 3 ) + 4 ? 5 : 6", new BigDecimal( 5 ) );
//		test( "1 ? 2 : 3 + ( 4 ? 5 : 6 )", new BigDecimal( 2 ) );
//		test( "( 1 ? 2 : 3 ) + ( 4 ? 5 : 6 )", new BigDecimal( 7 ) );
//		test( "0 ? 2 : 3 + 4 ? 5 : 6", new BigDecimal( 5 ) );
//		test( "0 ? 2 : 3 + ( 4 ? 5 : 6 )", new BigDecimal( 8 ) );
//
//		test( "1 + 1 == 2 ? 2 : 3", new BigDecimal( 2 ) );
//		test( "!0 ? 2 : 3", new BigDecimal( 2 ) );
//	}

	@Test
	static public void test4()
	{
		test( "if( 1; 2; 3 + 1 )", new BigDecimal( 2 ) );
		test( "if( !\"\"; 2; 3 )", new BigDecimal( 2 ) );
		test( "if( !\"x\"; 2; 3 )", new BigDecimal( 3 ) );
		test( "if( 1; 2 )", new BigDecimal( 2 ) );
		test( "if( 1;; 2 )", null );
		test( "if( null; 2 )", null );
		test( "if( null;; 2 )", new BigDecimal( 2 ) );
		test( "1 || 2", new BigDecimal( 1 ) );
		test( "0 || 2", new BigDecimal( 0 ) );
		test( "null || 2", new BigDecimal( 2 ) );
		test( "1 && 2", new BigDecimal( 2 ) );
		test( "0 && 2", new BigDecimal( 2 ) );
		test( "null && 2", null );
	}

	@Test
	static public void test5()
	{
		Context context = new Context();

		test( "a = 1", context, new BigDecimal( 1 ) );
		Assert.assertEquals( context.get( "a" ), new BigDecimal( 1 ) );

		test( "a = b = 1", context, new BigDecimal( 1 ) );
		Assert.assertEquals( context.get( "a" ), new BigDecimal( 1 ) );
		Assert.assertEquals( context.get( "b" ), new BigDecimal( 1 ) );

		test( "1 + ( a = 1 )", context, new BigDecimal( 2 ) );
		Assert.assertEquals( context.get( "a" ), new BigDecimal( 1 ) );

		test( "1 + ( a = 1 ) + a", context, new BigDecimal( 3 ) );
		Assert.assertEquals( context.get( "a" ), new BigDecimal( 1 ) );
	}

	@Test
	static public void test6()
	{
		test( "\"test\"", "test" );
		test( "\"test\" + \"test\"", "testtest" );
	}

	@Test
	static public void test7()
	{
		test( "abs( +1 )", new BigDecimal( 1 ) );
		test( "abs( -1 )", new BigDecimal( 1 ) );
		test( "abs( 1 + -2 )", new BigDecimal( 1 ) );

		test( "substr( \"sinterklaas\", 1 + 2 * 1, 9 - 1 - 1 )", "terk" );
		test( "substr( \"sinterklaas\", 6 )", "klaas" );
		test( "upper( \"sinterklaas\" )", "SINTERKLAAS" );
		test( "println( \"Hello World!\" )", "Hello World!" );
		test( "println( upper( \"Hello World!\" ) )", "HELLO WORLD!" );
		test( "length( \"sinterklaas\" )", 11 );
	}

	@Test
	static public void test8()
	{
		test( "1 == 1", true );
		test( "1 == 0", false );

		test( "true", true );
		test( "false", false );
		test( "1 == 1 == true", true );
		test( "true == 1 == 1", false );
		test( "true == ( 1 == 1 )", true );

		test( "!true", false );
		test( "!false", true );
		test( "!( 1 == 1 )", false );
		test( "!( 1 == 0 )", true );
		test( "!1 == false", true );

		test( "false && false", new Boolean( false ) );
		test( "false && true", new Boolean( false ) );
		test( "true && false", new Boolean( false ) );
		test( "true && true", new Boolean( true ) );

		test( "false || false", new Boolean( false ) );
		test( "false || true", new Boolean( true ) );
		test( "true || false", new Boolean( true ) );
		test( "true || true", new Boolean( true ) );

		test( "println( true ) && println( true )", new Boolean( true ) );
	}

	@Test
	static public void test9()
	{
		test( "null", null );
		test( "null == null", true );
		test( "1 == null", false );
		test( "\"test\" == null", false );
	}

	@Test
	static public void test10()
	{
		test( "2; 3", new BigDecimal( 3 ) );
		test( "a = 0; b = 1", new BigDecimal( 1 ) );
		test( "a = 0;", new BigDecimal( 0 ) );
		test( ";", null );
		test( "", null );
		test( ";;; a = 0;;;; b = 1;;;", new BigDecimal( 1 ) );
		test( ";;;;", null );
	}

	@Test
	static public void test11()
	{
		test( "( 2; 3 )", new BigDecimal( 3 ) );
		test( "a = 1; a + a + a++", new BigDecimal( 3 ) );
		test( "a = 1; a + a + ++a", new BigDecimal( 4 ) );
		test( "a = 0; if( true; a++, a++ )", new BigDecimal( 1 ) );
		test( "if( a = 1, b = a, b; 3; 4 )", new BigDecimal( 3 ) );
		test( "a = 0; if( false; a++, a++; ++a, ++a )", new BigDecimal( 2 ) );
		test( "i = 0; while( i < 10; println( i++ ) )", new BigDecimal( 9 ) );
		test( "i = 0; while( i++ < 10; println( i ) )", new BigDecimal( 10 ) );
	}

	@Test
	static public void test12()
	{
		testParseFail( "println( 1 " );
		test( "f = fun( a; a * a ); f( 3 )", new BigDecimal( 9 ) );
		test( "fun( a; a * a ) ( 5 )", new BigDecimal( 25 ) );
		test( "b = 8; fun( a; a ) ( b )", new BigDecimal( 8 ) );
		test( "fun( a; a( 3 ) ) ( fun( b; 5 * b ) )", new BigDecimal( 15 ) );
		test( "fun( a, b; a( 1, 2 ) * b( 3, 4 ) ) ( fun( c, d; c * d ), fun( e, f; e * f ) )", new BigDecimal( 24 ) );
		test( "fun( a, b; a( 1, 2 ) * b( 3, 4 ) ) ( fun( a, b; a * b ), fun( a, b; a * b ) )", new BigDecimal( 24 ) );
		test( "f = fun( ; 1 ); f()", new BigDecimal( 1 ) );
		test( "a = 0; fun( ; a = 1 ) (); a", new BigDecimal( 1 ) );
		test( "fun( a; a ) ( null )", null );
		test( "f = fun( ; fun( ; 2 ) ); f()()", new BigDecimal( 2 ) );
		test( "a = 1; f = fun( ; a ); a = 2; f()", new BigDecimal( 2 ) );
		test( "fun(a;fun(;a))(1)()", new BigDecimal( 1 ) );
	}

	@Test
	static public void test13()
	{
		Context context = new Context();
		context.set( "s", "sinterklaas" );
		test( "s.length()", context, 11 );
		test( "s.substring( 6 )", context, "klaas" );
		test( "s.substring( 1, 6 )", context, "inter" );
		test( "s.contains( \"kl\" )", context, true );

		TestObject1 o1 = new TestObject1();
		context.set( "o1", o1 );
		test( "o1.test()", context, 0 );
		test( "o1.test( 1 )", context, 2 );
		test( "o1.test( \"string\" )", context, 3 );
		test( "o1.test( \"string\", \"string\" )", context, 4 );
		assert o1.test( new BigDecimal( 1 ), new BigDecimal( 1 ) ) == 6;
		test( "o1.test( 1, 1 )", context, 6 );
		test( "1.getClass()", BigDecimal.class );
		test( "1.getClass().valueOf( 1.1 )", new BigDecimal( "1.1" ) );
		test( "o1.test( 1 == 1 )", context, 7 );

		TestObject2 o2 = new TestObject2();
		context.set( "o2", o2 );
		test( "o2.test( 1, 1 )", context, 1 );
	}

	@Test
	static public void test13_2()
	{
		Context context = new Context();
		test( "c = class( \"solidstack.script.ScriptTests$TestObject1\" );", context, TestObject1.class );
		test( "c().value", context, 0 );
		test( "c( 1 ).value", context, 2 );
		test( "c( \"string\" ).value", context, 3 );
		test( "c( \"string\", \"string\" ).value", context, 4 );
		test( "c( 1, 1 ).value", context, 6 );
		test( "c( 1 == 1 ).value", context, 7 );

		test( "c2 = class( \"solidstack.script.ScriptTests$TestObject2\" );", context, TestObject2.class );
		test( "c2( 1, 1 ).value", context, 1 );
	}

	@SuppressWarnings( "unused" )
	static public class TestObject1
	{
		public int value;

		public TestObject1() { this.value = 0; }
		public TestObject1( int i ) { this.value = 1; }
		public TestObject1( BigDecimal i ) { this.value = 2; }
		public TestObject1( String s ) { this.value = 3; }
		public TestObject1( String... s ) { this.value = 4; }
		public TestObject1( BigDecimal... b ) { this.value = 5; }
		public TestObject1( BigDecimal b1, Number b2 ) { this.value = 6; }
		public TestObject1( boolean b ) { this.value = 7; }

		public int test() { return 0; }
		public int test( int i ) { return 1; }
		public int test( BigDecimal i ) { return 2; }
		public int test( String s ) { return 3; }
		public int test( String... s ) { return 4; }
		public int test( BigDecimal... b ) { return 5; }
		public int test( BigDecimal b1, Number b2 ) { return 6; }
		public int test( boolean b ) { return 7; }
	}

	@SuppressWarnings( "unused" )
	static public class TestObject2
	{
		public int value;

		public TestObject2() { this.value = 0; }
		public TestObject2( int i1, int i2 ) { this.value = 1; }

		public int test( int i1, int i2 ) { return 1; }
	}

//	static private void test( String file ) throws IOException
//	{
//		InputStream in = ScriptTests.class.getResourceAsStream( file );
//		Reader reader = new InputStreamReader( in );
//		char[] buffer = new char[ 1024 ];
//		StringBuilder contents = new StringBuilder();
//		int len;
//		while( ( len = reader.read( buffer ) ) >= 0 )
//			contents.append( buffer, 0, len );
//		Script.compile( contents.toString() ).execute( null );
//		// TODO Validate result
//	}

	@Test
	static public void test14()
	{
		test( "( a, b ) = ( 1, 2 ); a + b", new BigDecimal( 3 ) );
		test( "( a, b ) = ( 1, 2 ); a + b", new BigDecimal( 3 ) );
		test( "( a, b ) = fun( ; 1, 2 )(); a + b", new BigDecimal( 3 ) );
		test( "( a, b ) = ( fun( ; 1 ), fun( ; 2 ) ) ; a() + b()", new BigDecimal( 3 ) );
	}

	@Test
	static public void test15()
	{
		test( "a = 1; \"a = ${a}\"", "a = 1" );
		test( "a = 1; s = \"a = ${a}\"; a = 2; s", "a = 1" );
		test( "a = 1; \"a = \\${a}\"", "a = ${a}" );
		test( "\"${1}\"", "1" );
//		test( "\"${}\"", "1" ); TODO
		test( "\"\".getClass()", String.class );
		test( "\"x\".getClass()", String.class );
		test( "\"${1}\".getClass()", SuperString.class );
		test( "\"x${1}x\".getClass()", SuperString.class );
	}

	@Test
	static public void test16()
	{
		test( "def( a ) = 1;", new BigDecimal( 1 ) );

		test( "fun( ; a = 1 )(); a", new BigDecimal( 1 ) ); // The function has no context of its own
		test( "a = 1; fun( a; a++ )( a ); a;", new BigDecimal( 1 ) );
		test( "a = 1; fun( ; def( a ) = 2 )(); a", new BigDecimal( 2 ) ); // The function has no context of its own
//		test( "a = 1; fun( ; val( a ) = 2 )(); a", new BigDecimal( 2 ) ); // The function has no context of its own
		test( "a = 1; fun{ ; def( a ) = 2 }(); a", new BigDecimal( 1 ) ); // The function has its own context

		test( "( a = 1 ); a", new BigDecimal( 1 ) ); // The block has no context of its own
		test( "a = 1; ( def( a ) = 2 ); a", new BigDecimal( 2 ) ); // The block has no context of its own
//		test( "a = 1; fun( ; val( a ) = 2 )(); a", new BigDecimal( 2 ) ); // The function has no context of its own
		test( "a = 1; { def( a ) = 2 }; a", new BigDecimal( 1 ) ); // The block has its own context
	}

	@Test
	static public void test17()
	{
		test( "class( \"java.util.ArrayList\" );", ArrayList.class );
		test( "c = class( \"java.util.ArrayList\" ); c();", new ArrayList() );
		test( "l = class( \"java.util.ArrayList\" )(); l.add( \"sinterklaas\" ); l.toArray();", new Object[] { "sinterklaas" } );
		test( "ArrayList = class( \"java.util.ArrayList\" ); l = ArrayList(); l.toArray();", new Object[ 0 ] );
	}

	// TODO Exceptions, catch & finally
	// TODO MethodMissing
	// TODO Default parameter values
	// TODO Calls with named parameters
	// TODO def & val
	// TODO { } blocks for new scopes
	// TODO Store tuples in variables?
	// TODO Binary and hexadecimal literals
	// TODO Add methods to the datatypes and/or objects
	// TODO DSLs
	// TODO Underscores in number literals
	// TODO Parameter spreading
	// TODO Arrays and maps with literals
	// TODO Ranges
	// TODO Synchronization
	// TODO Return, switch, break, continue
	// TODO Threads & sleep, etc
	// TODO Assert with lazy evaluation of its arguments
	// TODO Optional? Lazy evaluation of all arguments
	// TODO // Comments, /* comments, /** comments which can contain /* comments
	// TODO Compile time (post processing) transformation functions, for example: removeMargins()
	// TODO Token interceptors that work on the token stream, or custom script parsers for eval
	// TODO Symbols :red
	// TODO Mixins
	// TODO Lazy evaluation
}