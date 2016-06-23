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


public class SlidingByteStringIndex implements ByteStringIndex
{
	private Map<ByteString, Node<ByteString>> map = new HashMap<ByteString, Node<ByteString>>();
	private TreeIndex<ByteString> index = new TreeIndex<ByteString>();

	private int capacity;
	private int maxItemSize;


	public SlidingByteStringIndex( int capacity, int maxItemSize )
	{
		this.capacity = capacity;
		this.maxItemSize = maxItemSize;
	}

	void put( ByteString value )
	{
		Node<ByteString> node = this.map.remove( value );
		if( node != null )
			this.index.remove( node );
		else if( this.index.size() >= this.capacity )
			this.map.remove( this.index.removeLast().data() );

		this.map.put( value, this.index.addFirst( value ) );
	}

	@Override
	public Integer putOrGet( ByteString value )
	{
		if( value.length() < 2 )
			return null;
		if( value.length() > this.maxItemSize )
			return null;
		Integer result = get( value );
		put( value ); // New occurrence
		if( result == null )
			return null;
		if( value.length() >= CBORWriter.getUIntSize( result ) + 2 )
			return result;
		return null;
	}

	Integer get( ByteString value )
	{
		Node<ByteString> node = this.map.get( value );
		if( node == null )
			return null;
		return node.index();
	}

	@Override
	public int memoryUsage()
	{
		return 0;
	}
}
