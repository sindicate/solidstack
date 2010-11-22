/*--
 * Copyright 2010 Ren� M. de Bloois
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

package solidstack.query;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Decorates an {@link Object} array to let it look like a {@link Map}.
 * 
 * @author Ren� M. de Bloois
 */
public class RowMap implements Map< String, Object >
{
	protected Map< String, Integer > names; // This one is shared among all instances of RowMap
	protected Object[] values;

	public RowMap( Map< String, Integer > names, Object[] values )
	{
		this.names = names;
		this.values = values;
	}

	public int size()
	{
		return this.values.length;
	}

	public boolean isEmpty()
	{
		return this.values.length == 0;
	}

	public boolean containsKey( Object key )
	{
		return this.names.containsKey( key );
	}

	public boolean containsValue( Object value )
	{
		throw new UnsupportedOperationException();
	}

	public Object get( Object key )
	{
		Integer index = this.names.get( key );
		if( index == null )
			return null;
		return this.values[ index ];
	}

	public Object put( String key, Object value )
	{
		throw new UnsupportedOperationException();
	}

	public Object remove( Object key )
	{
		throw new UnsupportedOperationException();
	}

	public void putAll( Map< ? extends String, ? extends Object > m )
	{
		throw new UnsupportedOperationException();
	}

	public void clear()
	{
		throw new UnsupportedOperationException();
	}

	public Set< String > keySet()
	{
		return this.names.keySet();
	}

	public Collection< Object > values()
	{
		return new ValuesList( this.values );
	}

	public Set< java.util.Map.Entry< String, Object >> entrySet()
	{
		throw new UnsupportedOperationException();
	}
}
