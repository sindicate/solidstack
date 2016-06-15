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

import java.util.Map;

import funny.Symbol;
import solidstack.script.java.Java;


public class DefaultScope implements Scope
{
	static public final Symbol THIS = Symbol.apply( "this" );

	Scope parent;
	private ValueMap<Value<?>> values = new ValueMap<Value<?>>();


	public DefaultScope()
	{
		this( null, true );
	}

	public DefaultScope( Scope parent )
	{
		this( parent, true );
	}

	DefaultScope( Scope parent, boolean withThis )
	{
		this.parent = parent;
		if( withThis )
			val( THIS, this );
	}

	@Override
	// TODO In Java 8 this can be a default method in the interface
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
		Value<T> ref = (Value<T>)this.values.get( symbol );
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
	public <T> void var( Symbol symbol, T value )
	{
		this.values.put( new Variable<T>( symbol, value ) );
	}

	@Override
	public <T> void val( Symbol symbol, T value )
	{
		this.values.put( new Value<T>( symbol, value ) );
	}

	@Override
	public <T> T find( Symbol symbol )
	{
		Value<T> ref = (Value<T>)this.values.get( symbol );
		if( ref != null )
			return ref.get();
		if( this.parent != null )
			return this.parent.get( symbol );
		return null;
	}

	@Override
	public <T> T get( Symbol symbol )
	{
		Value<T> ref = (Value<T>)this.values.get( symbol );
		if( ref != null )
			return ref.get();
		if( this.parent != null )
			return this.parent.get( symbol );
		throw new UndefinedException();
	}

	@Override
	public <T> T apply( Symbol symbol, Object... args )
	{
		Value<?> ref = this.values.get( symbol );
		if( ref != null )
			return Java.applyTo( ref.get(), args );
		if( this.parent != null )
			return this.parent.apply( symbol, args );
		throw new UndefinedException();
	}

	@Override
	public <T> T apply( Symbol symbol, Map<String, Object> args )
	{
		Value<?> ref = this.values.get( symbol );
		if( ref != null )
			return Java.applyTo( ref.get(), args );
		if( this.parent != null )
			return this.parent.apply( symbol, args );
		throw new UndefinedException();
	}

	@Override
	public <T> T find( String name )
	{
		return find( Symbol.apply( name ) );
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
	public <T> void var( String name, T value )
	{
		var( Symbol.apply( name ), value );
	}

	@Override
	public <T> void val( String name, T value )
	{
		val( Symbol.apply( name ), value );
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
