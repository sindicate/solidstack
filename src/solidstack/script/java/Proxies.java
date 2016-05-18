package solidstack.script.java;

import java.io.FileOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;

import solidstack.classgen.Bytes;
import solidstack.classgen.ClassFile;
import solidstack.classgen.Field;
import solidstack.classgen.Loader;
import solidstack.classgen.bytecode.AccessLocal.TYPE;
import solidstack.classgen.bytecode.Expression;
import solidstack.classgen.bytecode.ExpressionBuilder;
import solidstack.classgen.constants.CClass;
import solidstack.classgen.constants.CFieldref;
import solidstack.classgen.constants.CMethodref;
import solidstack.lang.SystemException;
import solidstack.script.ScriptException;
import solidstack.script.objects.FunctionObject;

public class Proxies
{
	static public boolean canProxy( Class<?> type )
	{
		if( !type.isInterface() )
			return false;
		Method[] methods = type.getMethods();
		int count = 0;
		for( Method method : methods )
			if( !Modifier.isStatic( method.getModifiers() ) && !method.isDefault() )
				try
				{
					Object.class.getMethod( method.getName(), method.getParameterTypes() );
				}
				catch( NoSuchMethodException e )
				{
					count++;
				}
		return count == 1;
	}

	static public <T> T createProxy( FunctionObject function, Class<T> type )
	{
		// TODO Linenumbers in the ScriptExceptions?
		if( !type.isInterface() )
			throw new ScriptException( type.getName() + " is not an interface" );
		Method[] methods = type.getMethods();
		Method m = null;
		for( Method method : methods )
			if( !Modifier.isStatic( method.getModifiers() ) && !method.isDefault() )
				try
				{
					Object.class.getMethod( method.getName(), method.getParameterTypes() );
				}
				catch( NoSuchMethodException e )
				{
					if( m != null )
						throw new ScriptException( "Interface is not a functional interface" );
					m = method;
				}
		if( m == null )
			throw new ScriptException( "Interface is not a functional interface" );
		// TODO Varargs?
		if( m.getParameterCount() != function.getParameters().length )
			throw new ScriptException( "Parameter count mismatch" );
		return createProxy2( type, m, function );
	}

	static public <T> T createProxy( Class<T> type, Method method, FunctionObject function )
	{
		InvocationHandler handler = new FunctionObjectInvocationHandler( function, method );
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		return (T)Proxy.newProxyInstance( loader, new Class[] { type }, handler ); // Proxy classes get cached by the JVM
	}

	static public <T> T createProxy2( Class<T> type, Method method, FunctionObject function )
	{
		// TODO Caching of the proxy classes

		ClassFile file = new ClassFile();

		CClass cProxy = file.addClass( "solidstack/proxy/Proxy" ); // TODO Give $name with counter
		CClass cObject = file.addClass( Object.class );
		CClass cFunctionObject = file.addClass( FunctionObject.class );
		CClass cReturn = file.addClass( method.getReturnType() );
		CClass cInterface = file.addClass( type );
		CClass cTypes = file.addClass( Types.class );
		CClass cInteger = file.addClass( Integer.class );

		// The proxy class

		solidstack.classgen.Class proxyClass = file.createClass( cProxy );
		proxyClass.extend( cObject );
		proxyClass.implement( cInterface );

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
		CMethodref methodThe = file.addMethodref( cProxy, method.getName(), file.addMethodType( method.getReturnType(), method.getParameterTypes() ) );
		CMethodref methodCall = file.addMethodref( cFunctionObject, "call", file.addMethodType( Object.class, Object[].class ) );
		CMethodref methodIntValue = file.addMethodref( cInteger, "intValue", file.addMethodType( int.class ) );
		CMethodref methodConvert = file.addMethodref( cTypes, "convert", file.addMethodType( Object.class, Object.class, Class.class ) );

		int count = method.getParameterCount();
		Expression[] pars = new Expression[ count ];
		for( int i = 0; i < count; i++ )
			pars[ i ] = eb.local( i + 1, TYPE.REF );

		Expression call = eb.call(
			eb.field( eb.local( 0, TYPE.REF ), fieldFunction ),
			methodCall,
			eb.initArray( cObject, pars )
		);

		// public int compare( Object o1, Object o2 )
		//      return ( (Integer)this.function.call( new Object[] { o1, o2 } ) ).intValue()
		solidstack.classgen.Method compare = proxyClass.addMethod( file, methodThe );
		if( method.getReturnType() != void.class )
		{
			if( method.getReturnType() != int.class )
				throw new UnsupportedOperationException( "Only int return type supported" );
			compare.return_(
				eb.call(
					eb.cast(
						eb.callStatic(
							methodConvert,
							call ,
							eb.literal( cInteger )
						),
						cInteger
					),
					methodIntValue
				)
			);
		}
		else
		{
			compare.statement( call );
			compare.pop();
			compare.return_();
		}


		Bytes compiled = file.generate();

		try {
			FileOutputStream out = new FileOutputStream( "compiled.class" );
			try {
				out.write( compiled.toByteArray() );
			} finally {
				out.close();
			}
		} catch( Exception e ) {
			throw new SystemException( e );
		}

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
