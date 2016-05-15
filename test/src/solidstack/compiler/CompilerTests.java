package solidstack.compiler;

import static org.fest.assertions.api.Assertions.assertThat;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Comparator;

import org.testng.Assert;
import org.testng.annotations.Test;

import solidstack.compiler.AccessLocal.TYPE;
import solidstack.script.Script;
import solidstack.script.objects.FunctionObject;


public class CompilerTests
{
	@Test
	static public void testScriptInComparator() throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException
	{
		ClassBuilder cls = new ClassBuilder( "Test", Comparator.class );

		Field field = cls.addField( "function", FunctionObject.class );
		field.setPrivate( true );

		Method method = cls.addConstructor( FunctionObject.class );
		ExpressionBuilder e = new ExpressionBuilder( method );
		e.statement( e.callSuper( e.local( 0, TYPE.REF ), "<init>" ) );
		e.statement( e.setField( e.local( 0, TYPE.REF ), "function", "Lsolidstack/script/objects/FunctionObject;", e.local( 1, TYPE.REF ) ) );
		method.return_();

		Method compare = cls.addMethod( "compare", int.class, Object.class, Object.class );
		e = new ExpressionBuilder( compare );
//		return ( (java.lang.Integer)this.function.call( new java.lang.Object[] { o1, o2 } ) ).intValue()
		// TODO Make more things implicit
		compare.return_(
			e.call(
				e.cast(
					e.call(
						e.field( e.local( 0, TYPE.REF ), "function", "Lsolidstack/script/objects/FunctionObject;" ),
						"call",
						"([Ljava/lang/Object;)Ljava/lang/Object;",
						e.initArray( "java.lang.Object", e.local( 1, TYPE.REF ), e.local( 2, TYPE.REF ) )
					),
					"java.lang.Integer"
				),
				"intValue",
				"()I"
			)
		);

		Bytes compiled = cls.compile();

		FileOutputStream out = new FileOutputStream( "compiled.class" );
		try
		{
			out.write( compiled.toByteArray() );
		}
		finally
		{
			out.close();
		}

		Loader loader = new Loader();
		java.lang.Class<?> loadedClass = loader.load( cls.name(), compiled.toByteArray() );
		Assert.assertNotNull( loadedClass );

		FunctionObject function = (FunctionObject)Script.compile( "(a,b)=>a-b" ).eval();
		Comparator<Object> comparator = (Comparator<Object>)loadedClass.getConstructor( FunctionObject.class ).newInstance( function );

		Integer[] array = new Integer[] { 5, 2, 8, 4, 9, 1, 4, 6, 10, 3, 7, 5, 3 };
		Arrays.sort( array, comparator );

		assertThat( array ).isEqualTo( new Integer[] { 1, 2, 3, 3, 4, 4, 5, 5, 6, 7, 8, 9, 10 } );
	}

	@Test
	static public void testForLoop() throws IOException, InstantiationException, IllegalAccessException, SecurityException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException
	{
		ClassBuilder cls = new ClassBuilder( "Test" );

		Method init = cls.addConstructor();
		ExpressionBuilder e = new ExpressionBuilder( init );
		e.statement( e.callSuper( e.local( 0, TYPE.REF ), "<init>" ) );
		init.return_();

		Method test = cls.addMethod( "test", int.class );
		e = new ExpressionBuilder( test );
		test.addStatement( e.setLocal( 2, e.literal( 0 ) ) );
		test.for_(
			e.setLocal( 1, e.literal( 0 ) ),
			e.lessThan( e.local( 1, TYPE.INT ), e.literal( 10 ) ),
			e.setLocal( 1, e.plus( e.local( 1, TYPE.INT ), e.literal( 1 ) ) ),
			e.setLocal( 2, e.plus( e.local( 2, TYPE.INT ), e.local( 1, TYPE.INT ) ) )
		);
		test.return_( e.local( 2, TYPE.INT ) );

		Bytes compiled = cls.compile();

		FileOutputStream out = new FileOutputStream( "compiled.class" );
		try
		{
			out.write( compiled.toByteArray() );
		}
		finally
		{
			out.close();
		}

		Loader loader = new Loader();
		java.lang.Class<?> loadedClass = loader.load( cls.name(), compiled.toByteArray() );
		Assert.assertNotNull( loadedClass );

		Object object = loadedClass.newInstance();
		java.lang.reflect.Method method = loadedClass.getMethod( "test" );
		Object result = method.invoke( object );
		assertThat( result ).isInstanceOf( Integer.class );
		int i = (Integer)result;
		assertThat( i ).isEqualTo( 1 + 2 + 3 + 4 + 5 + 6 + 7 + 8 + 9 );
	}
}
