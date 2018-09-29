/*--
 * Copyright 2016 René M. de Bloois
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package solidstack.script.java;

import java.io.FileOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import solidstack.classgen.Bytes;
import solidstack.classgen.ClassFile;
import solidstack.classgen.Loader;
import solidstack.classgen.Types.VMTYPE;
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
	static private final Map<Class<?>, Class<?>> boxes = new HashMap<>();

	static
	{
		boxes.put( Boolean.TYPE, Boolean.class );
		boxes.put( Byte.TYPE, Byte.class );
		boxes.put( Character.TYPE, Character.class );
		boxes.put( Short.TYPE, Short.class );
		boxes.put( Integer.TYPE, Integer.class );
		boxes.put( Long.TYPE, Long.class );
		boxes.put( Float.TYPE, Float.class );
		boxes.put( Double.TYPE, Double.class );
	}

	static public boolean canProxy( Class<?> type )
	{
		// Must be an interface
		if( !type.isInterface() )
			return false;

		// Examine the non-static, non-default methods that are not implemented by java.lang.Object
		boolean found = false;
		for( Method method : type.getMethods() )
			if( Modifier.isAbstract( method.getModifiers() ) ) // This excludes static and default methods
				try
				{
					Object.class.getMethod( method.getName(), method.getParameterTypes() );
				}
				catch( NoSuchMethodException e )
				{
					if( found ) return false; // Already found one, so there are more than one
					found = true;
				}

		return found;
	}

	static public <T> T createProxy( FunctionObject function, Class<T> type )
	{
		// TODO Linenumbers in the ScriptExceptions?
		if( !type.isInterface() )
			throw new ScriptException( type.getName() + " is not an interface" );

		Method m = null;
		for( Method method : type.getMethods() )
			if( Modifier.isAbstract( method.getModifiers() ) ) // This excludes static and default methods
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
		if( m.getParameterTypes().length != function.getParameters().length ) // TODO In Java 8 we have a getParameterCount()
			throw new ScriptException( "Parameter count mismatch" );

		return createProxy( type, m, function );
	}

	static private <T> T createProxyOldWay( Class<T> type, Method method, FunctionObject function )
	{
		InvocationHandler handler = new FunctionObjectInvocationHandler( function, method );
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		return (T)Proxy.newProxyInstance( loader, new Class[] { type }, handler ); // Proxy classes get cached by the JVM
	}

	static private <T> T createProxy( Class<T> type, Method method, FunctionObject function )
	{
		// TODO Caching of the proxy classes

		ClassFile file = new ClassFile();

		CClass cProxy = file.addClass( "solidstack/proxy/Proxy" ); // TODO Give $name with counter
		CClass cObject = file.addClass( Object.class );
		CClass cFunctionObject = file.addClass( FunctionObject.class );
		CClass cInterface = file.addClass( type );
		CClass cTypes = file.addClass( Types.class );

		// The proxy class

		// 'public class implements xxx'
		solidstack.classgen.Class proxyClass = file.createClass( cProxy );
		proxyClass.extend( cObject );
		proxyClass.implement( cInterface );

		// Fields

		CFieldref fFunction = file.addFieldref( cProxy, "function", file.addFieldType( FunctionObject.class ) );

		// 'private FunctionObject function'
		proxyClass.addField( fFunction ).setPrivate( true );

		// Constructor

		CMethodref mInit = file.addMethodref( cProxy, "<init>", file.addMethodType( void.class, FunctionObject.class ) );
		CMethodref mSuper = file.addMethodref( cObject, "<init>", file.addMethodType( void.class ) );

		ExpressionBuilder eb = new ExpressionBuilder();

		// 'public <init>( FunctionObject f )
		//     super();
		//     this.function = f;
		//     return;'
		solidstack.classgen.Method init = proxyClass.addMethod( file, mInit );
		init.statement( eb.callSuper( eb.local( 0, VMTYPE.REF ), mSuper ) );
		init.statement( eb.setField( eb.local( 0, VMTYPE.REF ), fFunction, eb.local( 1, VMTYPE.REF ) ) );
		init.return_();

		// Method

		CMethodref mImpl = file.addMethodref( cProxy, method.getName(), file.addMethodType( method.getReturnType(), method.getParameterTypes() ) );
		CMethodref mCall = file.addMethodref( cFunctionObject, "call", file.addMethodType( Object.class, Object[].class ) );

		solidstack.classgen.Method impl = proxyClass.addMethod( file, mImpl );

		// Read pars from locals
		int count = method.getParameterTypes().length; // TODO In Java 8 we have a getParameterCount()
		Expression[] pars = new Expression[ count ];
		for( int i = 0; i < count; i++ )
			pars[ i ] = eb.local( i + 1, VMTYPE.REF );

		// Call the function
		Expression call = eb.call(
			eb.field( eb.local( 0, VMTYPE.REF ), fFunction ),
			mCall,
			eb.initArray( cObject, pars )
		);

		Class<?> returnType = method.getReturnType();

		if( returnType == void.class )
		{
			// Call, pop the result and return
			impl.pop( call );
			impl.return_();
		}
		else if( returnType.isPrimitive() )
		{
			// Convert to box type, unbox and return
			Class<?> boxType = boxes.get( returnType );
			call = convert( call, boxType, file );
			impl.return_( unbox( call, boxType, returnType, file ) );
		}
		else
			// Convert and return
			impl.return_( convert( call, returnType, file ) );

		// Generate the class file
		Bytes compiled = file.generate();

		// Write to file for testing
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

		// Load the class file
		Loader loader = new Loader();
		Class<?> loadedClass = loader.load( proxyClass.name(), compiled.toByteArray() );

		// Instantiate the proxy object
		try
		{
			return (T)loadedClass.getConstructor( FunctionObject.class ).newInstance( function );
		}
		catch( RuntimeException e )
		{
			throw e;
		}
		catch( Exception e )
		{
			throw new SystemException( e );
		}
	}

	static private Expression convert( Expression exp, Class<?> type, ClassFile file )
	{
		// Add constants
		CClass cTypes = file.addClass( Types.class );
		CMethodref methodConvert = file.addMethodref( cTypes, "convert", file.addMethodType( Object.class, Object.class, Class.class ) );
		CClass cType = file.addClass( type );

		// Call convert and cast
		ExpressionBuilder eb = new ExpressionBuilder();
		return eb.cast( eb.callStatic( methodConvert, exp, eb.literal( cType ) ), cType );
	}

	static private Expression unbox( Expression exp, Class<?> box, Class<?> prim, ClassFile file )
	{
		// Add constants
		CClass cBox = file.addClass( box );
		CMethodref methodUnbox = file.addMethodref( cBox, prim.getName() + "Value", file.addMethodType( prim ) );

		// Call unbox method
		ExpressionBuilder eb = new ExpressionBuilder();
		return eb.call( exp, methodUnbox );
	}
}
