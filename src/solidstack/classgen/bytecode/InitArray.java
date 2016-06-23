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
import solidstack.classgen.constants.CClass;

public class InitArray implements Expression
{
	private CClass classInfo;
	private Expression[] values;


	public InitArray( CClass type, Expression... values )
	{
		this.classInfo = type;
		this.values = values;
	}

	@Override
	public VMTYPE vmType()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void toByteCode( Bytes bytes )
	{
		ByteCode.iconst( bytes, this.values.length );
		bytes.writeByte( 0xBD ); // anewarray
		bytes.writeShort( this.classInfo.index() );
		int i = 0;
		for( Expression value : this.values )
		{
			bytes.writeByte( 0x59 ); // dup
			ByteCode.iconst( bytes, i++ );
			value.toByteCode( bytes );
			bytes.writeByte( 0x53 ); // aastore
		}
	}
}
