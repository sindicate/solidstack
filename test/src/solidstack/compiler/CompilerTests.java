package solidstack.compiler;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

import org.testng.Assert;
import org.testng.annotations.Test;


public class CompilerTests
{
	@Test
	static public void test1() throws IOException, InstantiationException, IllegalAccessException
	{
		ClassBuilder cls = new ClassBuilder( "Test", Comparator.class );

		Method init = cls.addConstructor();
		init.callSuper( "<init>" );

		Method compare = cls.addMethod( "compare", int.class, Object.class, Object.class );
		compare.return_( 0 );

		byte[] compiled = cls.compile();

		FileOutputStream out = new FileOutputStream( "compiled.class" );
		try
		{
			out.write( compiled );
		}
		finally
		{
			out.close();
		}

		Loader loader = new Loader();
		java.lang.Class<?> loadedClass = loader.load( cls.name(), compiled );
		Assert.assertNotNull( loadedClass );

		Comparator<Object> comparator = (Comparator<Object>)loadedClass.newInstance();

		Object object = new Object();
		Object[] array = new Object[] { object, object, object, object };
		Arrays.sort( array, comparator );
	}
}
