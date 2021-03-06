/*--
 * Copyright 2016 Ren� M. de Bloois
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

public class Cast implements Expression
{
	private CClass classInfo;
	private Expression value;

	public Cast( Expression value, CClass type )
	{
		this.classInfo = type;
		this.value = value;
	}

	@Override
	public VMTYPE vmType()
	{
		return VMTYPE.REF; // I think there won't be primitive casts
	}

	@Override
	public void toByteCode( Bytes bytes )
	{
		this.value.toByteCode( bytes );
		bytes.writeByte( 0xC0 );
		bytes.writeShort( this.classInfo.index() );
	}
}
