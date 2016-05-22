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

public class MapScope implements Scope
{
	private Scope parent;
	private Map<Object, Object> map;


	public MapScope( Map<Object, Object> map )
	{
		this.map = map;
	}

	public MapScope( Map<Object, Object> map, Scope parent )
	{
		this.map = map;
		this.parent = parent;
	}

	@Override
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
		if( this.map.containsKey( symbol.toString() ) )
			this.map.put( symbol.toString(), value );
		else
		{
			if( this.parent == null )
				throw new UndefinedException();
			this.parent.set( symbol, value );
		}
	}

	@Override
	public void var( Symbol symbol, Object value )
	{
		this.map.put( symbol.toString(), value );
	}

	@Override
	public void val( Symbol symbol, Object value )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Object get( Symbol symbol )
	{
		if( this.map.containsKey( symbol.toString() ) )
			return this.map.get( symbol.toString() );
		if( this.parent == null )
			throw new UndefinedException();
		return this.parent.get( symbol );
	}

	public Object apply( Symbol symbol, Object... args )
	{
		return DefaultScope.apply( get( symbol ), args );
	}

	public Object apply( Symbol symbol, Map args )
	{
		return DefaultScope.apply( get( symbol ), args );
	}
}
