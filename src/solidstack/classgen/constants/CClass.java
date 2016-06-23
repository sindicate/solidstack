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

package solidstack.classgen.constants;

import solidstack.classgen.Bytes;

public class CClass extends Constant
{
	private CUtf8 utf8;

	public CClass( CUtf8 utf8 )
	{
		this.utf8 = utf8;
	}

	@Override
	public void write( Bytes bytes )
	{
		bytes.writeByte( 7 );
		bytes.writeShort( this.utf8.index() );
	}

	@Override
	public boolean equals( Object obj )
	{
		if( obj != null )
			if( obj.getClass() == CClass.class )
			{
				CClass other = (CClass)obj;
				return other.utf8 == this.utf8;
			}
		return false;
	}

	public String name()
	{
		return this.utf8.value();
	}
}
