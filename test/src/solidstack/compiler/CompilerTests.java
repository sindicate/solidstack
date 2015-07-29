package solidstack.compiler;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Comparator;

import org.testng.Assert;
import org.testng.annotations.Test;


public class CompilerTests
{
	@Test
	static public void test1() throws IOException
	{
		ClassBuilder cls = new ClassBuilder( "Test", Comparator.class );
		Method compare = cls.addMethod( "compare", int.class, Object.class, Object.class );
		compare.return_( 0 );

		FileOutputStream out = new FileOutputStream( "compiled.class" );
		try
		{
			out.write( cls.compile() );
		}
		finally
		{
			out.close();
		}

		Loader loader = new Loader();
		java.lang.Class<?> loadedClass = loader.load( cls.name(), cls.compile() );
		Assert.assertNotNull( loadedClass );
	}
}
