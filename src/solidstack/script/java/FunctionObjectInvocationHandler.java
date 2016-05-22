package solidstack.script.java;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import solidstack.script.objects.FunctionObject;

public class FunctionObjectInvocationHandler implements InvocationHandler
{
	private FunctionObject function;
	private Method method;


	public FunctionObjectInvocationHandler( FunctionObject function, Method method )
	{
		this.function = function;
		this.method = method;
	}

	@Override
	public Object invoke( Object proxy, Method method, Object[] args ) throws Throwable
	{
		if( method.equals( this.method ) )
			return Types.convert( this.function.call( args ), Integer.class );
		throw new UnsupportedOperationException( "Method not supported: " + method );
	}
}
