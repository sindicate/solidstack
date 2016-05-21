package solidstack.classgen;

import static org.fest.assertions.api.Assertions.assertThat;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Comparator;

import org.testng.Assert;
import org.testng.annotations.Test;

import solidstack.classgen.Types.TYPE;
import solidstack.classgen.bytecode.ExpressionBuilder;
import solidstack.classgen.constants.CClass;
import solidstack.classgen.constants.CFieldref;
import solidstack.classgen.constants.CMethodref;
import solidstack.classgen.constants.CUtf8;
import solidstack.script.Script;
import solidstack.script.objects.FunctionObject;


public class CompilerTests
{
	@Test
	static public void testScriptInComparator() throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException
	{
		ClassFile cb = new ClassFile();

		CClass cTest = cb.addClass( "Test" );
		CClass cObject = cb.addClass( "java/lang/Object" );
		CClass cComparator = cb.addClass( "java/util/Comparator" );
		CClass cFunctionObject = cb.addClass( "solidstack/script/objects/FunctionObject" );
		CClass cInteger = cb.addClass( "java/lang/Integer" );

		Class test = cb.createClass( cTest );
		test.extend( cObject );
		test.implement( cComparator );

		CFieldref fieldFunction = cb.addFieldref( cTest, "function", cb.addFieldType( FunctionObject.class ) );

		// private FunctionObject function
		Field field = test.addField( fieldFunction );
		field.setPrivate( true );

		CMethodref methodInit = cb.addMethodref( cTest, "<init>", cb.addMethodType( void.class, FunctionObject.class ) );
		CMethodref methodSuper = cb.addMethodref( cObject, "<init>", cb.addMethodType( void.class ) );
		CMethodref methodIntValue = cb.addMethodref( cInteger, "intValue", cb.addMethodType( int.class ) );

		ExpressionBuilder e = new ExpressionBuilder();

		// public <init>( FunctionObject f )
		//     super()
		//     this.function = f
		//     return
		Method method = test.addMethod( cb, methodInit );
		method.statement( e.callSuper( e.local( 0, TYPE.REF ), methodSuper ) );
		method.statement( e.setField( e.local( 0, TYPE.REF ), fieldFunction, e.local( 1, TYPE.REF ) ) );
		method.return_();

		CUtf8 typeCompare = cb.addMethodType( int.class, Object.class, Object.class );
		CMethodref methodCompare = cb.addMethodref( cTest, "compare", typeCompare );
		CUtf8 typeCall = cb.addMethodType( Object.class, Object[].class );
		CMethodref methodCall = cb.addMethodref( cFunctionObject, "call", typeCall );

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
			),
			TYPE.INT
		);

		Bytes compiled = cb.generate();

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
		ClassFile file = new ClassFile();

		CClass cTest = file.addClass( "Test" );
		CClass cObject = file.addClass( "java/lang/Object" );

		Class test = file.createClass( cTest );
		test.extend( cObject );

		CMethodref methodInit = file.addMethodref( cTest, "<init>", file.addMethodType( void.class ) );
		CMethodref methodSuperInit = file.addMethodref( cObject, "<init>", file.addMethodType( void.class ) );

		ExpressionBuilder e = new ExpressionBuilder();

		// public <init>()
		//     super()
		//     return
		Method method = test.addMethod( file, methodInit );
		method.statement( e.callSuper( e.local( 0, TYPE.REF ), methodSuperInit ) );
		method.return_();

		CUtf8 typeTest = file.addMethodType( int.class );
		CMethodref methodTest = file.addMethodref( cTest, "test", typeTest );

		// public int test()
		//     i2 = 0
		//     for( i1 = 0; i1 < 10; i1 = i1 + 1 )
		//         i2 = i2 + i1
		//     return i2;
		// TODO Maybe give locals a type
		method = test.addMethod( file, methodTest );
		method.statement( e.setLocal( 2, e.literal( 0 ) ) );
		method.for_(
			e.setLocal( 1, e.literal( 0 ) ),
			e.lessThan( e.local( 1, TYPE.INT ), e.literal( 10 ) ),
			e.setLocal( 1, e.plus( e.local( 1, TYPE.INT ), e.literal( 1 ) ) ),
			e.setLocal( 2, e.plus( e.local( 2, TYPE.INT ), e.local( 1, TYPE.INT ) ) )
		);
		method.return_( e.local( 2, TYPE.INT ), TYPE.INT );

		Bytes compiled = file.generate();

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
