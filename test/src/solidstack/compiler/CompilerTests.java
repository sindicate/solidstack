package solidstack.compiler;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Comparator;

import org.testng.Assert;
import org.testng.annotations.Test;

import solidstack.script.objects.FunctionObject;


public class CompilerTests
{
	@Test
	static public void test1() throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException
	{
		ClassBuilder cls = new ClassBuilder( "Test", Comparator.class );

		Field function = cls.addField( "function", FunctionObject.class );
		function.setPrivate( true );

		Method init = cls.addConstructor( FunctionObject.class );
		init.callSuper( "<init>" );
		ExpressionBuilder e = new ExpressionBuilder( init );
		e.statement( e.setField( e.var( 0 ), "function", e.var( 1 ) ) );
		init.return_();
//		out.writeByte( 0xB1 ); // return

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

		FunctionObject functionObject = new FunctionObject();
		Comparator<Object> comparator = (Comparator<Object>)loadedClass.getConstructor( FunctionObject.class ).newInstance( functionObject );

		Object[] array = new Object[] { new Object(), new Object(), new Object(), new Object() };
		Arrays.sort( array, comparator );
	}
}
