/*--
 * Copyright 2012 Ren� M. de Bloois
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

package solidstack.script.java;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import solidstack.script.objects.Assoc;


/**
 * Default Java extensions.
 */
public class DefaultClassExtensions
{
	static public StringBuilder addString( Object[] array, StringBuilder buf, String start, String separator, String end )
	{
		buf.append( start );
		int len = array.length;
		if( len > 0 )
			buf.append( array[ 0 ] );
		for( int i = 1; i < len; i++ )
		{
			buf.append( separator );
			buf.append( array[ i ] );
		}
		buf.append( end );
		return buf;
	}

	static public List static_apply( LinkedList list, Object... objects )
	{
		return new LinkedList( Arrays.asList( objects ) );
	}

	static public Class static_apply( Class dummy, String name ) throws ClassNotFoundException
	{
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
//		try
//		{
			return Java.forName( name, loader );
//		}
//		catch( ClassNotFoundException e )
//		{
//			throw new ThrowException( "No such class: " + e.getMessage(), ThreadContext.get().cloneStack() );
//		}
	}

	static public List static_apply( List list, Object... objects )
	{
		return new ArrayList( Arrays.asList( objects ) );
	}

	static public Map static_apply( Map map, Assoc... entries )
	{
		HashMap result = new HashMap();
		for( Assoc labeled : entries )
			result.put( labeled.getLabel(), labeled.getValue() );
		return result;
	}

	static public Set static_apply( Set set, Object... objects )
	{
		return new HashSet( Arrays.asList( objects ) );
	}

	static public Object apply( List list, int index )
	{
		return list.get( index );
	}

	static public Object apply( Map map, Object key )
	{
		return map.get( key );
	}

	static public Object apply( Object[] array, int index )
	{
		return array[ index ];
	}

	static public List collect( Collection collection, Function function )
	{
		List result = new ArrayList(collection.size());
		for( Object object : collection )
			result.add( function.call( object ) );
		return result;
	}

	static public List collect( Object[] array, Function function )
	{
		List result = new ArrayList(array.length);
		for( Object object : array )
			result.add( function.call( object ) );
		return result;
	}

	static public Object each( Collection collection, Function function )
	{
		// TODO Or should the ThreadContext be a parameter too?
		int count = function.getParameters().length;
		if( count == 2 )
		{
			Object result = null;
			int index = 0;
			for( Object object : collection )
				result = function.call( index++, object );
			return result;
		}
		Object result = null;
		for( Object object : collection )
			result = function.call( object );
		return result;
	}

	static public Object each( Map<?,?> map, Function function )
	{
		Object result = null;
		for( Entry<?,?> entry : map.entrySet() )
			result = function.call( entry.getKey(), entry.getValue() );
		return result;
	}

	static public Object eachKey( Map<?,?> map, Function function )
	{
		Object result = null;
		for( Object key : map.keySet() )
			result = function.call( key );
		return result;
	}

	static public String mkString( Object[] array, String start, String separator, String end )
	{
		StringBuilder buf = new StringBuilder();
		addString( array, buf, start, separator, end );
		return buf.toString();
	}

	static public int size( Object[] array )
	{
		return array.length;
	}

	static public int size( boolean[] array )
	{
		return array.length;
	}

	static public int size( char[] array )
	{
		return array.length;
	}

	static public int size( byte[] array )
	{
		return array.length;
	}

	static public int size( short[] array )
	{
		return array.length;
	}

	static public int size( int[] array )
	{
		return array.length;
	}

	static public int size( long[] array )
	{
		return array.length;
	}

	static public int size( float[] array )
	{
		return array.length;
	}

	static public int size( double[] array )
	{
		return array.length;
	}

	static public int size( String string )
	{
		return string.length();
	}

	static public Object update( List list, int index, Object value )
	{
		if( index >= list.size() )
		{
			// TODO Is this what we want?
			while( index > list.size() )
				list.add( null );
			list.add( value );
			return value;
		}
		list.set( index, value );
		return value;
	}

	static public Object update( Map map, Object key, Object value )
	{
		map.put( key, value );
		return value;
	}

	static public Object update( Object[] array, int index, Object value )
	{
		array[ index ] = value;
		return value;
	}
}
