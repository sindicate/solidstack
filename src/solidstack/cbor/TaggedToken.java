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

public class TaggedToken extends SimpleToken
{
	Tag[] tags;


	TaggedToken( TYPE type )
	{
		super( type );
	}

	@Override
	public boolean hasTag( long value )
	{
		return getTag( value ) != null;
	}

	@Override
	public Tag getTag( long value )
	{
		for( Tag tag : this.tags )
			if( tag.value == value )
				return tag;
		return null;
	}

	@Override
	public String toString()
	{
		if( this.tags == null )
			return super.toString();

		StringBuilder result = new StringBuilder();
		result.append( DefaultClassExtensions.mkString( this.tags, " " ) );
		result.append( ' ' ).append( super.toString() );
		return result.toString();
	}
}
