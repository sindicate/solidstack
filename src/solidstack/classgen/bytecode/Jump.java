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

public class Jump
{
	private Bytes bytes;
	private int pos;

	public Jump( Bytes bytes, int pos )
	{
		this.bytes = bytes;
		this.pos = pos;
	}

	public void link( int dest )
	{
		this.bytes.writeShortAt( this.pos + 1, dest - this.pos );
	}
}
