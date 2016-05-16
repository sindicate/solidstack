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
		ClassBuilder cb = new ClassBuilder();

		ConstantClass cTest = cb.addConstantClass( "Test" );
		ConstantClass cObject = cb.addConstantClass( "java/lang/Object" );
		ConstantClass cComparator = cb.addConstantClass( "java/util/Comparator" );
		ConstantClass cFunctionObject = cb.addConstantClass( "solidstack/script/objects/FunctionObject" );
		ConstantClass cInteger = cb.addConstantClass( "java/lang/Integer" );

		Class test = cb.addClass( cTest );
		test.extend( cObject );
		test.implement( cComparator );

		ConstantFieldref fieldFunction = cb.addConstantFieldref( cTest, "function", cb.addFieldType( FunctionObject.class ) );

		// private FunctionObject function
		Field field = test.addField( fieldFunction );
		field.setPrivate( true );

		ConstantMethodref methodInit = cb.addConstantMethodref( cTest, "<init>", cb.addMethodType( void.class, FunctionObject.class ) );
		ConstantMethodref methodSuper = cb.addConstantMethodref( cObject, "<init>", cb.addMethodType( void.class ) );
		ConstantMethodref methodIntValue = cb.addConstantMethodref( cInteger, "intValue", cb.addMethodType( int.class ) );

		ExpressionBuilder e = new ExpressionBuilder();

		// public <init>( FunctionObject f )
		//     super()
		//     this.function = f
		//     return
		Method method = test.addMethod( cb, methodInit );
		method.addStatement( e.callSuper( e.local( 0, TYPE.REF ), methodSuper ) );
		method.addStatement( e.setField( e.local( 0, TYPE.REF ), fieldFunction, e.local( 1, TYPE.REF ) ) );
		method.return_();

		ConstantUtf8 typeCompare = cb.addMethodType( int.class, Object.class, Object.class );
		ConstantMethodref methodCompare = cb.addConstantMethodref( cTest, "compare", typeCompare );
		ConstantUtf8 typeCall = cb.addMethodType( Object.class, Object[].class );
		ConstantMethodref methodCall = cb.addConstantMethodref( cFunctionObject, "call", typeCall );

		// public int compare( Object o1, Object o2 )
		//      return ( (Integer)this.function.call( new Object[] { o1, o2 } ) ).intValue()
		Method compare = test.addMethod( cb, methodCompare );
		compare.return_(
			e.call(
				e.cast(
					e.call(
						e.field( e.local( 0, TYPE.REF ), fieldFunction ),
						methodCall,
						e.initArray( cObject, e.local( 1, TYPE.REF ), e.local( 2, TYPE.REF ) )
					),
					cInteger
				),
				methodIntValue
			)
		);

		Bytes compiled = cb.compile();

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
		java.lang.Class<?> loadedClass = loader.load( test.name(), compiled.toByteArray() );
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
		ClassBuilder file = new ClassBuilder();

		ConstantClass cTest = file.addConstantClass( "Test" );
		ConstantClass cObject = file.addConstantClass( "java/lang/Object" );

		Class test = file.addClass( cTest );
		test.extend( cObject );

		ConstantMethodref methodInit = file.addConstantMethodref( cTest, "<init>", file.addMethodType( void.class ) );
		ConstantMethodref methodSuperInit = file.addConstantMethodref( cObject, "<init>", file.addMethodType( void.class ) );

		ExpressionBuilder e = new ExpressionBuilder();

		// public <init>()
		//     super()
		//     return
		Method method = test.addMethod( file, methodInit );
		method.addStatement( e.callSuper( e.local( 0, TYPE.REF ), methodSuperInit ) );
		method.return_();

		ConstantUtf8 typeTest = file.addMethodType( int.class );
		ConstantMethodref methodTest = file.addConstantMethodref( cTest, "test", typeTest );

		// public int test()
		//     i2 = 0
		//     for( i1 = 0; i1 < 10; i1 = i1 + 1 )
		//         i2 = i2 + i1
		//     return i2;
		method = test.addMethod( file, methodTest );
		method.addStatement( e.setLocal( 2, e.literal( 0 ) ) );
		method.for_(
			e.setLocal( 1, e.literal( 0 ) ),
			e.lessThan( e.local( 1, TYPE.INT ), e.literal( 10 ) ),
			e.setLocal( 1, e.plus( e.local( 1, TYPE.INT ), e.literal( 1 ) ) ),
			e.setLocal( 2, e.plus( e.local( 2, TYPE.INT ), e.local( 1, TYPE.INT ) ) )
		);
		method.return_( e.local( 2, TYPE.INT ) );

		Bytes compiled = file.compile();

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
		java.lang.Class<?> loadedClass = loader.load( test.name(), compiled.toByteArray() );
		Assert.assertNotNull( loadedClass );

		Object object = loadedClass.newInstance();
		java.lang.reflect.Method m = loadedClass.getMethod( "test" );
		Object result = m.invoke( object );
		assertThat( result ).isInstanceOf( Integer.class );
		int i = (Integer)result;
		assertThat( i ).isEqualTo( 1 + 2 + 3 + 4 + 5 + 6 + 7 + 8 + 9 );
	}
}
