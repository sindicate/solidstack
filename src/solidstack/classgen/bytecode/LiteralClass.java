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

public class LiteralClass implements Expression
{
	private CClass value;

	public LiteralClass( CClass value )
	{
		this.value = value;
	}

	@Override
	public VMTYPE vmType()
	{
		return VMTYPE.REF;
	}

	@Override
	public void toByteCode( Bytes bytes )
	{
		int i = this.value.index();
		if( i > 255 )
			throw new UnsupportedOperationException();

		bytes.writeByte( 0x12 ); // ldc
		bytes.writeByte( i );
	}
}
