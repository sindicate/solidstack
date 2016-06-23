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
import solidstack.classgen.Types.VMTYPE;


public class AccessLocal implements Expression
{
	private int local;
	private VMTYPE type;


	public AccessLocal( int local, VMTYPE type )
	{
		if( local < 0 )
			throw new IllegalArgumentException();
		this.local = local;
		this.type = type;
	}

	@Override
	public VMTYPE vmType()
	{
		return this.type;
	}

	@Override
	public void toByteCode( Bytes bytes )
	{
		if( this.local >= 256 )
			throw new UnsupportedOperationException(); // TODO Wide

		switch( this.type )
		{
			case INT:
				if( this.local < 4 )
				{
					bytes.writeByte( 0x1A + this.local ); // iload_0
					return;
				}
				bytes.writeByte( 0x15 ); // iload
				bytes.writeByte( this.local );
				return;

			case REF:
				if( this.local < 4 )
				{
					bytes.writeByte( 0x2A + this.local ); // aload_0
					return;
				}
				bytes.writeByte( 0x19 ); // aload
				bytes.writeByte( this.local );
				return;
		}
	}
}
