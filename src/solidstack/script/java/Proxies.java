package solidstack.script.java;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import solidstack.classgen.Bytes;
import solidstack.classgen.ClassFile;
import solidstack.classgen.Field;
import solidstack.classgen.Loader;
import solidstack.classgen.bytecode.AccessLocal.TYPE;
import solidstack.classgen.bytecode.ExpressionBuilder;
import solidstack.classgen.constants.CClass;
import solidstack.classgen.constants.CFieldref;
import solidstack.classgen.constants.CMethodref;
import solidstack.lang.SystemException;
import solidstack.script.objects.FunctionObject;

public class Proxies
{
	static public <T> T createProxy( Class<T> interfase, Method method, FunctionObject function )
	{
		InvocationHandler handler = new FunctionObjectInvocationHandler( function, method );
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		return (T)Proxy.newProxyInstance( loader, new Class[] { interfase }, handler ); // Proxy classes get cached by the JVM
	}

	static public <T> T createProxy2( Class<T> interfase, Method method, FunctionObject function )
	{
		// TODO Caching of the proxy classes

		ClassFile file = new ClassFile();

		CClass cProxy = file.addClass( "solidstack/proxy/Proxy" ); // TODO Give $name with counter
		CClass cObject = file.addClass( "java/lang/Object" );
		CClass cFunctionObject = file.addClass( "solidstack/script/objects/FunctionObject" ); // TODO Add addClass(class)
		CClass cInteger = file.addClass( "java/lang/Integer" );
		CClass cComparator = file.addClass( "java/util/Comparator" );

		// The proxy class

		solidstack.classgen.Class proxyClass = file.createClass( cProxy );
		proxyClass.extend( cObject );
		proxyClass.implement( cComparator );

		// Fields

		CFieldref fieldFunction = file.addFieldref( cProxy, "function", file.addFieldType( FunctionObject.class ) );

		// private FunctionObject function
		Field field = proxyClass.addField( fieldFunction );
		field.setPrivate( true );

		// Constructor

		CMethodref methodInit = file.addMethodref( cProxy, "<init>", file.addMethodType( void.class, FunctionObject.class ) );
		CMethodref methodSuper = file.addMethodref( cObject, "<init>", file.addMethodType( void.class ) );

		ExpressionBuilder eb = new ExpressionBuilder();

		// public <init>( FunctionObject f )
		//     super()
		//     this.function = f
		//     return
		solidstack.classgen.Method init = proxyClass.addMethod( file, methodInit );
		init.statement( eb.callSuper( eb.local( 0, TYPE.REF ), methodSuper ) );
		init.statement( eb.setField( eb.local( 0, TYPE.REF ), fieldFunction, eb.local( 1, TYPE.REF ) ) );
		init.return_();

		// Method

		CMethodref methodCompare = file.addMethodref( cProxy, "compare", file.addMethodType( int.class, Object.class, Object.class ) );
		CMethodref methodCall = file.addMethodref( cFunctionObject, "call", file.addMethodType( Object.class, Object[].class ) );
		CMethodref methodIntValue = file.addMethodref( cInteger, "intValue", file.addMethodType( int.class ) );

		// public int compare( Object o1, Object o2 )
		//      return ( (Integer)this.function.call( new Object[] { o1, o2 } ) ).intValue()
		solidstack.classgen.Method compare = proxyClass.addMethod( file, methodCompare );
		compare.return_(
			eb.call(
				eb.cast(
					eb.call(
						eb.field( eb.local( 0, TYPE.REF ), fieldFunction ),
						methodCall,
						eb.initArray( cObject, eb.local( 1, TYPE.REF ), eb.local( 2, TYPE.REF ) )
					),
					cInteger
				),
				methodIntValue
			)
		);

		// TODO Use Types.convert to convert to Integer

		Bytes compiled = file.generate();

//		try {
//			FileOutputStream out = new FileOutputStream( "compiled.class" );
//			try {
//				out.write( compiled.toByteArray() );
//			} finally {
//				out.close();
//			}
//		} catch( Exception e ) {
//			throw new SystemException( e );
//		}

		Loader loader = new Loader();
		Class<?> loadedClass = loader.load( proxyClass.name(), compiled.toByteArray() );

		try
		{
			return (T)loadedClass.getConstructor( FunctionObject.class ).newInstance( function );
		}
		catch( InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e )
		{
			throw new SystemException( e );
		}
	}
}
