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

import java.util.ArrayList;
import java.util.List;


public class StandardReverseByteStringIndex implements ReverseByteStringIndex
{
	private List<ByteString> list = new ArrayList<ByteString>();


	@Override
	public void put( ByteString value )
	{
		int index = this.list.size();
		if( value.length() >= CBORWriter.getUIntSize( index ) + 2 )
			this.list.add( value );
	}

	@Override
	public ByteString get( int index )
	{
		return this.list.get( index );
	}
}
