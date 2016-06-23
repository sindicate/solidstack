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

public class AssignField implements Statement
{
	private Expression instance;
	private String name;
	private String descriptor;
	private Expression value;

	private CFieldref fieldref;

	public AssignField( Expression instance, CFieldref fieldref, Expression value )
	{
		this.instance = instance;
		this.fieldref = fieldref;
		this.value = value;
	}

	@Override
	public void toByteCode( Bytes bytes )
	{
		this.instance.toByteCode( bytes );
		this.value.toByteCode( bytes );
		bytes.writeByte( 0xB5 ); // putfield
		bytes.writeShort( this.fieldref.index() );
	}
}
