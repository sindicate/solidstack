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

package solidstack.template.javascript;

import org.mozilla.javascript.Scriptable;

import funny.Symbol;
import solidstack.script.scopes.Scope;
import solidstack.script.scopes.UndefinedException;

public class ScriptableScope implements Scriptable
{
	private Scriptable parent;
	private Scope scope;

	public ScriptableScope( Scriptable parent, Scope scope )
	{
		this.parent = parent;
		this.scope = scope;
	}

	@Override
	public void delete( String arg0 )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void delete( int arg0 )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Object get( String name, Scriptable start )
	{
		try
		{
			return this.scope.get( Symbol.apply( name ) );
		}
		catch( UndefinedException e )
		{
			return this.parent.get( name, start );
		}
	}

	@Override
	public Object get( int arg0, Scriptable arg1 )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getClassName()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Object getDefaultValue( Class<?> arg0 )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Object[] getIds()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Scriptable getParentScope()
	{
		return this.parent;
	}

	@Override
	public Scriptable getPrototype()
	{
		return null;
	}

	@Override
	public boolean has( String arg0, Scriptable arg1 )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean has( int arg0, Scriptable arg1 )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasInstance( Scriptable arg0 )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void put( String arg0, Scriptable arg1, Object arg2 )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void put( int arg0, Scriptable arg1, Object arg2 )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void setParentScope( Scriptable arg0 )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void setPrototype( Scriptable arg0 )
	{
		throw new UnsupportedOperationException();
	}
}
