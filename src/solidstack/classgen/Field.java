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

package solidstack.classgen;

import solidstack.classgen.constants.CFieldref;

public class Field
{
	private boolean privat;
	private CFieldref fieldref;

	public Field( CFieldref fieldref )
	{
		this.fieldref = fieldref;
	}

	public String name()
	{
		return this.fieldref.name();
	}

	public CFieldref fieldref()
	{
		return this.fieldref;
	}

	public void setPrivate( boolean privat )
	{
		this.privat = privat;
	}

	public void write( Bytes bytes )
	{
		bytes.writeShort( this.privat ? 0x1002 : 0x1001 ); // public & synthetic
		bytes.writeShort( this.fieldref.nameIndex() ); // name_index
		bytes.writeShort( this.fieldref.typeIndex() ); // descriptor_index

		// attributes
		bytes.writeShort( 0 ); // attributes_count
	}
}
