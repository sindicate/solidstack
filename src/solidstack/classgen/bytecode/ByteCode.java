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

package solidstack.classgen.bytecode;

import solidstack.classgen.Bytes;

public class ByteCode
{
	static public void iconst( Bytes bytes, int i )
	{
		if( i >= -1 && i <= 5 )
		{
			bytes.writeByte( 0x03 + i ); // iconst_0 + the value
			return;
		}
		if( i >= -128 && i < 128 )
		{
			bytes.writeByte( 0x10 ); // bipush
			bytes.writeByte( i );
			return;
		}
		if( i >= -32768 && i < 32768 )
		{
			bytes.writeByte( 0x11 ); // sipush
			bytes.writeShort( i );
			return;
		}
		throw new UnsupportedOperationException();
	}

	static public void jump( Bytes bytes, int delta )
	{
		if( delta >= -0x8000 && delta < 0x8000 )
		{
			bytes.writeByte( 0xA7 ); // goto
			bytes.writeShort( delta );
		}
		else
		{
			bytes.writeByte( 0xC8 ); // goto_w
			bytes.writeInt( delta );
		}
	}
}
