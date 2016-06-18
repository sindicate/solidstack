package solidstack.script;

import static org.assertj.core.api.Assertions.assertThat;

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
		// The ComparatorEqualsCall class will call the equals method instead of the compare method
		exec( "var Test= loadClass(\"solidstack.script.ComparatorEqualsCall\"); var test=new Test(); test.sort( (i1,i2) => i1-i2 );" );
	}

	@Test
	static public void customInterfaceNonReturning() throws FileNotFoundException
	{
		// TODO Capture the output
		Object result = exec( "var c=loadClass(\"solidstack.script.CustomInterfaceConsumer\"); var i=new c(); i.run1( ()=>println(\"I'm here!\") );" );
		assertThat( result ).isNull();

		result = exec( "var c=loadClass(\"solidstack.script.CustomInterfaceConsumer\"); var i=new c(); i.run2( (name)=>println(s\"Hello ${name}!\"), \"René\" );" );
		assertThat( result ).isNull();
	}

	@Test
	static public void customInterfaceReturning() throws FileNotFoundException
	{
		// This test conversion too (from Boolean to String)
		Object result = exec( "var c=loadClass(\"solidstack.script.CustomInterfaceConsumer\"); var i=new c(); i.run3( ()=>true );" );
		assertThat( result ).isInstanceOf( String.class );
		assertThat( (String)result ).isEqualTo( "true" );

		// This returns a Long
		result = exec( "var c=loadClass(\"solidstack.script.CustomInterfaceConsumer\"); var i=new c(); i.run4( ()=>123 );" );
		assertThat( result ).isInstanceOf( Long.class );
		assertThat( (Long)result ).isEqualTo( 123 );

		// This returns a long
		result = exec( "var c=loadClass(\"solidstack.script.CustomInterfaceConsumer\"); var i=new c(); i.run5( ()=>456 );" );
		assertThat( result ).isInstanceOf( Long.class );
		assertThat( (Long)result ).isEqualTo( 456 );
	}

	static public Object exec( String script )
	{
		return Exec.exec( SourceReaders.forString( script ) );
	}
}
