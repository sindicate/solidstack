/*--
 * Copyright 2015 René M. de Bloois
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
import solidstack.script.java.MissingFieldException;
import solidstack.script.java.MissingMethodException;
import solidstack.script.objects.Type;
import solidstack.script.objects.Util;

public class ObjectScope implements Scope
{
	private Scope parent;
	private Object object;


	public ObjectScope( Object object )
	{
		this.object = object;
	}

	public ObjectScope( Object object, Scope parent )
	{
		this( object );
		this.parent = parent;
	}

	@Override
	public <T> void setOrVar( Symbol symbol, T value )
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
	public <T> void set( Symbol symbol, T value )
	{
		try
		{
			if( this.object instanceof Type )
				Java.setStatic( ( (Type)this.object ).theClass(), symbol.toString(), value );
			else
				Java.set( this.object, symbol.toString(), value ); // TODO Use resolve() instead.
		}
		catch( InvocationTargetException e )
		{
			// TODO Do we want this here? Should it be in Java class? And the Returning does not work yet.
			Throwable t = e.getCause();
			if( t instanceof Returning )
				throw (Returning)t;
			throw new JavaException( t, ThreadContext.get().cloneStack( /* TODO getLocation() */ ) );
		}
		catch( MissingFieldException e )
		{
			if( this.parent == null )
				throw new UndefinedException();
			this.parent.set( symbol, value );
		}
	}

	@Override
	public <T> void var( Symbol symbol, T value )
	{
		this.parent.var( symbol, value );
	}

	@Override
	public <T> void val( Symbol symbol, T value )
	{
		this.parent.val( symbol, value );
	}

	@Override
	public <T> T find( Symbol symbol )
	{
		try
		{
			if( this.object instanceof Type )
				return (T)Java.getStatic( ( (Type)this.object ).theClass(), symbol.toString() );
			return (T)Java.get( this.object, symbol.toString() ); // TODO Use resolve() instead.
		}
		catch( InvocationTargetException e )
		{
			Throwable t = e.getCause();
			if( t instanceof Returning )
				throw (Returning)t;
			throw new JavaException( t, ThreadContext.get().cloneStack( /* TODO getLocation() */ ) );
		}
		catch( MissingFieldException e )
		{
			if( this.parent != null )
				return this.parent.get( symbol );
			return null;
		}
	}

	@Override
	public <T> T get( Symbol symbol )
	{
		try
		{
			if( this.object instanceof Type )
				return (T)Java.getStatic( ( (Type)this.object ).theClass(), symbol.toString() );
			return (T)Java.get( this.object, symbol.toString() ); // TODO Use resolve() instead.
		}
		catch( InvocationTargetException e )
		{
			Throwable t = e.getCause();
			if( t instanceof Returning )
				throw (Returning)t;
			throw new JavaException( t, ThreadContext.get().cloneStack( /* TODO getLocation() */ ) );
		}
		catch( MissingFieldException e )
		{
			if( this.parent != null )
				return this.parent.get( symbol );
			throw new UndefinedException();
		}
	}

	@Override
	public <T> T apply( Symbol symbol, Object... args )
	{
		// TODO Could be that there is a FunctionObject in a property

		args = Util.toJavaParameters( args );
		try
		{
			if( this.object instanceof Type )
				return (T)Java.invokeStatic( ( (Type)this.object ).theClass(), symbol.toString(), args );
			return (T)Java.invoke( this.object, symbol.toString(), args );
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
		catch( MissingMethodException e )
		{
			if( this.parent != null )
				return this.parent.apply( symbol, args );
			throw new UndefinedException();
		}
		catch( Exception e )
		{
			throw new ThrowException( e.getMessage() != null ? e.getMessage() : e.toString(), ThreadContext.get().cloneStack() );
//			throw new JavaException( e, thread.cloneStack( getLocation() ) ); // TODO Debug flag or something?
		}
	}

	@Override
	public <T> T apply( Symbol symbol, Map<String, Object> args )
	{
		// TODO This can be supported if it returns a FunctionObject
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T find( String name )
	{
		return find( Symbol.apply( name ) );
	}

	@Override
	public <T> void var( String name, T value )
	{
		this.parent.var( name, value );
	}

	@Override
	public <T> void val( String name, T value )
	{
		this.parent.val( name, value );
	}

	@Override
	public <T> T get( String name )
	{
		return get( Symbol.apply( name ) );
	}

	@Override
	public <T> void setOrVar( String name, T value )
	{
		setOrVar( Symbol.apply( name ), value );
	}

	@Override
	public <T> void set( String name, T value )
	{
		set( Symbol.apply( name ), value );
	}

	@Override
	public <T> T apply( String name, Object... args )
	{
		return apply( Symbol.apply( name ), args );
	}

	@Override
	public <T> T apply( String name, Map<String, Object> args )
	{
		return apply( Symbol.apply( name ), args );
	}
}
