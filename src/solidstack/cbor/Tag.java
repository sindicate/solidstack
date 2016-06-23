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

package solidstack.cbor;

import solidstack.script.java.DefaultClassExtensions;

public class Tag
{
	long value;
	Token[] parameters;

	public Tag( long value )
	{
		this.value = value;
	}

	public void setParameters( Token[] parameters )
	{
		this.parameters = parameters;
	}

	@Override
	public String toString()
	{
		StringBuilder result = new StringBuilder( "TAG 0x" );
		SimpleToken.appendHex( result, this.value );
		if( this.parameters != null )
			result.append( DefaultClassExtensions.mkString( this.parameters, "(", ", ", ")" ) );
		return result.toString();
	}
}
