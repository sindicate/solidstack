/*--
 * Copyright 2016 Ren� M. de Bloois
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
import solidstack.script.scopes.ValueMap.Entry;


public class Value<T> extends Entry
{
	T value;

	Value( Symbol symbol, T value )
	{
		super( symbol );

//		Assert.notNull( value );
		this.value = value;
	}

	public T get()
	{
		return this.value;
	}

	public void set( T value )
	{
		throw new ImmutableException( getKey() );
	}

	public boolean isUndefined()
	{
		return false;
	}
}
