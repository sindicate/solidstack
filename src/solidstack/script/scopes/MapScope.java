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

import java.util.Map;

import funny.Symbol;
import solidstack.script.java.Java;


public class MapScope implements Scope
{
	private Scope parent;
	private Map<String, Object> map;


	public MapScope( Map<String, Object> map )
	{
		this.map = map;
	}

	public MapScope( Map<String, Object> map, Scope parent )
	{
		this.map = map;
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
		String name = symbol.toString();
		if( this.map.containsKey( name ) )
			this.map.put( name, value );
		else
		{
			if( this.parent == null )
				throw new UndefinedException( symbol.toString() );
			this.parent.set( symbol, value );
		}
	}

	@Override
	public <T> void var( Symbol symbol, T value )
	{
		this.map.put( symbol.toString(), value );
	}

	@Override
	public <T> void val( Symbol symbol, T value )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T find( Symbol symbol )
	{
		String name = symbol.toString();
		if( this.map.containsKey( name ) )
			return (T)this.map.get( name );
		if( this.parent != null )
			return this.parent.get( symbol );
		return null;
	}

	@Override
	public <T> T get( Symbol symbol )
	{
		String name = symbol.toString();
		if( this.map.containsKey( name ) )
			return (T)this.map.get( name );
		if( this.parent != null )
			return this.parent.get( symbol );
		throw new UndefinedException( symbol.toString() );
	}

	@Override
	public <T> T apply( Symbol symbol, Object... args )
	{
		return Java.applyTo( get( symbol ), args );
	}

	@Override
	public <T> T apply( Symbol symbol, Map<String, Object> args )
	{
		return Java.applyTo( get( symbol ), args );
	}

	@Override
	public <T> T find( String name )
	{
		if( this.map.containsKey( name ) )
			return (T)this.map.get( name );
		if( this.parent != null )
			return this.parent.find( name );
		return null;
	}

	@Override
	public <T> T get( String name )
	{
		if( this.map.containsKey( name ) )
			return (T)this.map.get( name );
		if( this.parent != null )
			return this.parent.get( name );
		throw new UndefinedException( name );
	}

	@Override
	public <T> void setOrVar( String name, T value )
	{
		try
		{
			set( name, value );
		}
		catch( UndefinedException e )
		{
			var( name, value );
		}
	}

	@Override
	public <T> void set( String name, T value )
	{
		if( this.map.containsKey( name ) )
			this.map.put( name, value );
		else
		{
			if( this.parent == null )
				throw new UndefinedException( name );
			this.parent.set( name, value );
		}
	}

	@Override
	public <T> void var( String name, T value )
	{
		this.map.put( name, value );
	}

	@Override
	public <T> void val( String name, T value )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T apply( String name, Object... args )
	{
		return Java.applyTo( get( name ), args );
	}

	@Override
	public <T> T apply( String name, Map<String, Object> args )
	{
		return Java.applyTo( get( name ), args );
	}
}
