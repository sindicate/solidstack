/*--
 * Copyright 2012 René M. de Bloois
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

package solidstack.script.scopes;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import funny.Symbol;
import solidstack.script.JavaException;
import solidstack.script.Returning;
import solidstack.script.ThreadContext;
import solidstack.script.ThrowException;
import solidstack.script.java.Java;
import solidstack.script.objects.FunctionObject;
import solidstack.script.objects.Type;
import solidstack.script.objects.Util;




public class DefaultScope implements Scope
{
	static public final Symbol THIS = Symbol.apply( "this" );

	Scope parent;
	private ValueMap<Value> values = new ValueMap<Value>();


	public DefaultScope()
	{
	}

	public DefaultScope( Scope parent )
	{
		this.parent = parent;
	}

	public DefaultScope withThis()
	{
		val( THIS, this );
		return this;
	}

	@Override
	// TODO In Java 8 this can be a default method in the interface
	public void setOrCreate( Symbol symbol, Object value )
	{
		try
		{
			set( symbol, value );
		}
		catch( UndefinedException e )
		{
			var( symbol, value );
		}
	}

	@Override
	public void set( Symbol symbol, Object value )
	{
		Value ref = this.values.get( symbol );
		if( ref == null )
		{
			if( this.parent == null )
				throw new UndefinedException();
			this.parent.set( symbol, value );
		}
		else
			ref.set( value );
	}

	// For testing
	public void clear()
	{
		this.values.clear();
	}

	@Override
	public void var( Symbol symbol, Object value )
	{
		this.values.put( new Variable( symbol, value ) );
	}

	@Override
	public void val( Symbol symbol, Object value )
	{
		this.values.put( new Value( symbol, value ) );
	}

	@Override
	public Object get( Symbol symbol )
	{
		Value ref = this.values.get( symbol );
		if( ref != null )
			return ref.get();
		if( this.parent != null )
			return this.parent.get( symbol );
		throw new UndefinedException();
	}

	@Override
	public Object apply( Symbol symbol, Object... args )
	{
		Value ref = this.values.get( symbol );
		if( ref != null )
			return apply( ref.get(), args );
		if( this.parent != null )
			return this.parent.apply( symbol, args );
		throw new UndefinedException();
	}

	static Object apply( Object object, Object... args )
	{
		if( object == null )
			throw new ThrowException( "Function is null", ThreadContext.get().cloneStack() );

		if( object instanceof FunctionObject )
			return ( (FunctionObject)object ).call( ThreadContext.get(), args );

		// TODO This Java stuff should this go to the Java package?
		Object[] pars = Util.toJavaParameters( args );
		try
		{
			if( object instanceof Type )
				return Java.invokeStatic( ( (Type)object ).theClass(), "apply", pars );
			return Java.invoke( object, "apply", pars );
		}
		catch( InvocationTargetException e )
		{
			Throwable t = e.getCause();
			if( t instanceof Returning )
				throw (Returning)t;
			throw new JavaException( t, ThreadContext.get().cloneStack() );
		}
		catch( Returning e )
		{
			throw e;
		}
		catch( Exception e )
		{
			throw new ThrowException( e.getMessage() != null ? e.getMessage() : e.toString(), ThreadContext.get().cloneStack() );
//			throw new JavaException( e, thread.cloneStack( getLocation() ) ); // TODO Debug flag or something?
		}
	}

	@Override
	public Object apply( Symbol symbol, Map args )
	{
		Value ref = this.values.get( symbol );
		if( ref != null )
			return apply( ref.get(), args );
		if( this.parent != null )
			return this.parent.apply( symbol, args );
		throw new UndefinedException();
	}

	static Object apply( Object object, Map args )
	{
		if( object == null )
			throw new ThrowException( "Function is null", ThreadContext.get().cloneStack() );

		if( object instanceof FunctionObject )
			return ( (FunctionObject)object ).call( ThreadContext.get(), args );

		throw new UnsupportedOperationException();
	}
}
