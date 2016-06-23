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


public interface Token
{
	static public enum TYPE { UINT, NINT, BYTES, TEXT, ARRAY, MAP, IBYTES, ITEXT, IARRAY, IMAP, TAG, BOOL, NULL, UNDEF, HFLOAT, SFLOAT, DFLOAT, BREAK, EOF };

	static public final SimpleToken EOF = new SimpleToken( TYPE.EOF );
	static public final SimpleToken IBSTRING = new SimpleToken( TYPE.IBYTES );
	static public final SimpleToken ITSTRING = new SimpleToken( TYPE.ITEXT );
	static public final SimpleToken IARRAY = new SimpleToken( TYPE.IARRAY );
	static public final SimpleToken IMAP = new SimpleToken( TYPE.IMAP );
	static public final SimpleToken BREAK = new SimpleToken( TYPE.BREAK );
	static public final SimpleToken FALSE = new SimpleToken( false );
	static public final SimpleToken TRUE = new SimpleToken( true );
	static public final SimpleToken NULL = new SimpleToken( TYPE.NULL );
	static public final SimpleToken UNDEF = new SimpleToken( TYPE.UNDEF );

	TYPE type();
	boolean isTag();
	long longValue();
	double doubleValue();
	boolean booleanValue();
	int length();
	boolean hasTag( long value );
	Tag getTag( long value );
}
