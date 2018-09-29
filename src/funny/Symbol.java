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

package funny;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

public class Symbol
{
	static private WeakHashMap<String,WeakReference<Symbol>> symbols = new WeakHashMap<>();

	static public Symbol apply( String name )
	{
		synchronized( symbols )
		{
			WeakReference<Symbol> symbol = symbols.get( name );
			Symbol result;
			if( symbol != null )
			{
				result = symbol.get();
				if( result != null )
					return result;
				// else: Symbol is lost, and the original key can't be retrieved, so we need to overwrite it completely
				symbols.remove( name ); // Needed because put() will not replace the key if it already exists
			}
			// else: Key is lost or never existed, which means that the symbol does not exist or never existed

			result = new Symbol( name );
			symbols.put( name, new WeakReference<>( result ) );
			return result;
		}
	}

	String name;
	private int hashCode;

	Symbol( String name )
	{
		this.name = name;
		this.hashCode = name.hashCode();
	}

	Symbol( String name, int hashCode )
	{
		this.name = name;
		this.hashCode = hashCode;
	}

	@Override
	public String toString()
	{
		return this.name;
	}

	@Override
	public int hashCode()
	{
		return this.hashCode;
	}
}
