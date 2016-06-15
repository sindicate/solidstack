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


public class CombinedScope implements Scope
{
	private Scope scope1, scope2;


	public CombinedScope( Scope scope1, Scope scope2 )
	{
		this.scope1 = scope1;
		this.scope2 = scope2;
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
			this.scope1.set( symbol, value );
		}
		catch( UndefinedException e )
		{
			this.scope2.set( symbol, value );
		}
	}

	@Override
	public <T> void var( Symbol symbol, T value )
	{
		// TODO Maybe make scope1 vs. scope2 configurable
		// TODO Or maybe make scopes lockable
		this.scope1.var( symbol, value );
	}

	@Override
	public <T> void val( Symbol symbol, T value )
	{
		this.scope1.val( symbol, value );
	}

	@Override
	public <T> T find( Symbol symbol )
	{
		try
		{
			return this.scope1.get( symbol );
		}
		catch( UndefinedException e )
		{
			return this.scope2.find( symbol );
		}
	}

	@Override
	public <T> T get( Symbol symbol )
	{
		try
		{
			return this.scope1.get( symbol );
		}
		catch( UndefinedException e )
		{
			return this.scope2.get( symbol );
		}
	}

	@Override
	public <T> T apply( Symbol symbol, Object... pars )
	{
		try
		{
			return this.scope1.apply( symbol, pars );
		}
		catch( UndefinedException e )
		{
			return this.scope2.apply( symbol, pars );
		}
	}

	@Override
	public <T> T apply( Symbol symbol, Map<String, Object> args )
	{
		try
		{
			return this.scope1.apply( symbol, args );
		}
		catch( UndefinedException e )
		{
			return this.scope2.apply( symbol, args );
		}
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
