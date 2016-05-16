package solidstack.script;

import static org.fest.assertions.api.Assertions.assertThat;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;

import solidstack.io.SourceReaders;

public class InterfaceTests
{
	@Test
	static public void comparator() throws FileNotFoundException
	{
		Object result = exec( "var list = List(10,9,8,7,6,5,4,3,2,1); list.sort( (i1,i2) => i1-i2 ); list" );
		assertThat( result ).isInstanceOf( List.class );
		assertThat( (List)result ).isEqualTo( Arrays.asList( 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 ) );
	}

	@Test
	static public void comparatorEqualsMethod() throws FileNotFoundException
	{
//		try
//		{
			exec( "var Test= loadClass(\"solidstack.script.ComparatorEqualsCall\"); var test=new Test(); test.sort( (i1,i2) => i1-i2 );" );
//			failBecauseExceptionWasNotThrown( ScriptException.class );
//		}
//		catch( ScriptException e )
//		{
//			assertThat( e.getMessage() ).contains( "Method not supported" );
//		}
	}

	static public Object exec( String script )
	{
		return Exec.exec( SourceReaders.forString( script ) );
	}
}
