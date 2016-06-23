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
import solidstack.classgen.constants.CFieldref;

public class AssignLocal implements Statement
{
	private int local;
	private Expression value;

	private CFieldref fieldref;


	public AssignLocal( int local, Expression value )
	{
		if( local < 0 )
			throw new IllegalArgumentException();
		this.local = local;
		this.value = value;
	}

	@Override
	public void toByteCode( Bytes bytes )
	{
		this.value.toByteCode( bytes );
		if( this.local < 4 )
		{
			bytes.writeByte( 0x3B + this.local ); // istore_0
			return;
		}
		if( this.local >= 256 )
			throw new UnsupportedOperationException(); // TODO Wide
		bytes.writeByte( 0x36 ); // istore
		bytes.writeByte( this.local );
	}
}
