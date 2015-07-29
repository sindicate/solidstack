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

		Field field = cls.addField( "function", FunctionObject.class );
		field.setPrivate( true );

		Method method = cls.addConstructor( FunctionObject.class );
		ExpressionBuilder e = new ExpressionBuilder( method );
		e.statement( e.callSuper( e.var( 0 ), "<init>" ) );
		e.statement( e.setField( e.var( 0 ), "function", "Lsolidstack/script/objects/FunctionObject;", e.var( 1 ) ) );
		method.return_();

		Method compare = cls.addMethod( "compare", int.class, Object.class, Object.class );
//		return ( (java.lang.Integer)this.function.call( new java.lang.Object[] { o1, o2 } ) ).intValue()
		// TODO Make more things implicit
		compare.return_(
			e.call(
				e.cast(
					e.call(
						e.field( e.var( 0 ), "function", "Lsolidstack/script/objects/FunctionObject;" ),
						"call",
						"([Ljava/lang/Object;)Ljava/lang/Object;",
						e.initArray( "java.lang.Object", e.var( 1 ), e.var( 2 ) )
					),
					"java.lang.Integer"
				),
				"intValue",
				"()I"
			)
		);

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
