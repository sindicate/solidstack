/*--
 * Copyright 2015 René M. de Bloois
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

package solidstack.script.objects;

import java.util.ArrayList;
import java.util.List;

import solidstack.script.objects.FunctionObject.ParWalker;

public class Util
{
	static public Object toJava( Object value )
	{
		if( value instanceof Tuple )
			return ( (Tuple)value ).list();
		if( value == null )
			return null;
//		if( value instanceof PString )
//			return value.toString();
		if( value instanceof Type )
			return ( (Type)value ).theClass();
		return value;
	}

	static public Object[] toJavaParameters( Object... pars )
	{
		List<Object> result = new ArrayList<Object>();
		ParWalker pw = new ParWalker( pars );
		while( pw.hasNext() )
			result.add( toJava( pw.get() ) );
		return result.toArray( new Object[ result.size() ] );
	}

	static public final Object[] EMPTY_ARRAY = new Object[ 0 ];

	static public Object[] toArray( Object values )
	{
		Object[] result;
		if( values instanceof Tuple )
			return ( (Tuple)values ).list().toArray();
		return new Object[] { values };
	}
}
