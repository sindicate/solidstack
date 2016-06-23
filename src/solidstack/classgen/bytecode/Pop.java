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

public class Pop implements Statement
{
	private Expression expression;

	public Pop( Expression value )
	{
		this.expression = value;
	}

	@Override
	public void toByteCode( Bytes bytes )
	{
		this.expression.toByteCode( bytes );
		int b;
		switch( this.expression.vmType() ) // TODO Or we could return the type from the getByteCode
		{
			case REF: case INT: case FLOAT: b = 0x57; break; // pop
			case LONG: case DOUBLE: b = 0x58; break; // pop2
			default: throw new UnsupportedOperationException( this.expression.vmType().toString() );
		}
		bytes.writeByte( b );
	}
}
