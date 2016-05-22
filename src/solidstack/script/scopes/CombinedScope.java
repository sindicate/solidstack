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
	public void var( Symbol symbol, Object value )
	{
		// Maybe make scope1 vs. scope2 configurable
		this.scope1.var( symbol, value );
	}

	@Override
	public void val( Symbol symbol, Object value )
	{
		this.scope1.val( symbol, value );
	}

	@Override
	public Object get( Symbol symbol )
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

	public Object apply( Symbol symbol, Object... pars )
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

	public Object apply( Symbol symbol, Map args )
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
}
