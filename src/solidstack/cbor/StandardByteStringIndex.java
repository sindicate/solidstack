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


public class StandardByteStringIndex implements ByteStringIndex
{
	/* *******
	 * 32-bit JVM or 64-bit with UseCompressedOops=true (default)
	 *
	 * dictionary = HashMap
	 * 		table
	 *		4+8		--> HashMap$Node
	 *		4			hash
	 *		4+8			--> key = ByteString
	 *		4+8				--> byte[]
	 *		1				boolean
	 *		4			--> next
	 *		4+8			--> value = Integer
	 *		4				value
	 *		3	padding
	 */
	static public int MEMORY_OVERHEAD = 64;

	private Map<ByteString, Integer> map = new HashMap<ByteString, Integer>();
	private int memoryUsage;


	void put( ByteString value )
	{
		int index = this.map.size();
		if( value.length() >= CBORWriter.getUIntSize( index ) + 2 )
		{
			this.map.put( value, index );
			this.memoryUsage += value.length() + MEMORY_OVERHEAD;
		}
	}

	@Override
	public Integer putOrGet( ByteString value )
	{
		Integer result = get( value );
		if( result != null )
			return result;
		put( value );
		return null;
	}

	Integer get( ByteString value )
	{
		return this.map.get( value );
	}

	@Override
	public int memoryUsage()
	{
		return this.memoryUsage;
	}
}
