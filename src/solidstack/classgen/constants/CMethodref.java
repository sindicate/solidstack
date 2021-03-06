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

package solidstack.classgen.constants;

import solidstack.classgen.Bytes;

public class CMethodref extends Constant
{
	private CClass classInfo;
	private CNameAndType nameAndType;

	public CMethodref( CClass classInfo, CNameAndType nameAndType )
	{
		this.classInfo = classInfo;
		this.nameAndType = nameAndType;
	}

	@Override
	public void write( Bytes bytes )
	{
		bytes.writeByte( 10 );
		bytes.writeShort( this.classInfo.index() );
		bytes.writeShort( this.nameAndType.index() );
	}

	@Override
	public boolean equals( Object obj )
	{
		if( obj != null )
			if( obj.getClass() == CMethodref.class )
			{
				CMethodref other = (CMethodref)obj;
				return other.classInfo == this.classInfo && other.nameAndType == this.nameAndType;
			}
		return false;
	}

	public int nameIndex()
	{
		return this.nameAndType.nameIndex();
	}

	public int typeIndex()
	{
		return this.nameAndType.typeIndex();
	}

	public CUtf8 type()
	{
		return this.nameAndType.type();
	}
}
