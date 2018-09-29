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

package solidstack.cbor;

import java.util.HashMap;
import java.util.Map;

import solidstack.util.TreeIndex;
import solidstack.util.TreeIndex.Node;

public class SlidingReverseByteStringIndex implements ReverseByteStringIndex
{
	private Map<ByteString, Node<ByteString>> map = new HashMap<>();
	private TreeIndex<ByteString> index = new TreeIndex<>();

	private int capacity;
	private int maxItemSize;


	public SlidingReverseByteStringIndex( int capacity, int maxItemSize )
	{
		this.capacity = capacity;
		this.maxItemSize = maxItemSize;
	}

	@Override
	public void put( ByteString value )
	{
		if( value.length() < 2 )
			return;
		if( value.length() > this.maxItemSize )
			return;

		Node<ByteString> node = this.map.remove( value );
		if( node != null )
			this.index.remove( node );
		else if( this.index.size() >= this.capacity )
			this.map.remove( this.index.removeLast().data() );

		this.map.put( value, this.index.addFirst( value ) );
	}

	@Override
	public ByteString get( int index )
	{
		ByteString result = get0( index );
		put( result ); // New occurrence
		return result;
	}

	ByteString get0( int index )
	{
		return this.index.get( index ).data();
	}
}
