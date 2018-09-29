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

package solidstack.script;

import org.testng.Assert;
import org.testng.annotations.Test;

import funny.Symbol;
import solidstack.script.scopes.ValueMap;
import solidstack.script.scopes.ValueMap.Entry;

@SuppressWarnings( "javadoc" )
public class ValueMapTests
{
	@Test
	static public void test1()
	{
		int COUNT = 100;

		ValueMap<TestEntry> values = new ValueMap<>();

		for( int i = 0; i < COUNT; i++ )
		{
			String key = Integer.toString( i );
			TestEntry entry = new TestEntry( key );
			values.put( entry );
		}
		Assert.assertEquals( values.size(), COUNT );

		System.out.println( "largest bucket: " + values.largestBucketSize() );
		System.out.println( "empty buckets: " + values.emptyBucketCount() );
		System.out.println( "chaining unknown: " + values.averageChainingUnknownKeys() );
		System.out.println( "chaining known: " + values.averageChainingKnownKeys() );

		for( int i = 0; i < COUNT; i++ )
		{
			Symbol key = Symbol.apply( Integer.toString( i ) );
			TestEntry entry = values.remove( key );
			Assert.assertEquals( entry.getKey(), key );
		}
		Assert.assertEquals( values.size(), 0 );

		for( int i = 0; i < COUNT; i++ )
		{
			String key = Integer.toString( i );
			TestEntry entry = new TestEntry( key );
			values.put( entry );
		}
		Assert.assertEquals( values.size(), COUNT );
		for( int i = COUNT - 1; i >= 0; i-- )
		{
			Symbol key = Symbol.apply( Integer.toString( i ) );
			TestEntry entry = values.remove( key );
			Assert.assertEquals( entry.getKey(), key );
		}
		Assert.assertEquals( values.size(), 0 );
	}

	static public class TestEntry extends Entry
	{
		public TestEntry( String key )
		{
			super( Symbol.apply( key ) );
		}
	}
}
