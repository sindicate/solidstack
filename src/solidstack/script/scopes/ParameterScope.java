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

import funny.Symbol;


public class ParameterScope extends DefaultScope
{
	public ParameterScope( Scope parent )
	{
		super( parent );
		if( parent == null )
			throw new NullPointerException( "parent is null" );
	}

	public void defParameter( Symbol symbol, Object value )
	{
		super.var( symbol, value );
	}

	@Override
	public void var( Symbol symbol, Object value )
	{
		if( this.parent == null )
			throw new NullPointerException( "????" );
		this.parent.var( symbol, value );
	}

	@Override
	public void val( Symbol symbol, Object value )
	{
		this.parent.val( symbol, value );
	}
}
