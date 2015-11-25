/*--
 * Copyright 2015 Ren� M. de Bloois
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


public class ExceptionTests extends Util
{
//	@Test
	public void test1()
	{
		try
		{
			eval( "class( \"solidstack.script.ExceptionTests\" )#raise()" );
		}
		catch( Throwable t )
		{
			t.printStackTrace();
		}
	}

//	@Test
	public void test2()
	{
		try
		{
			eval( "throw( \"exception\" )" );
		}
		catch( Throwable t )
		{
			t.printStackTrace();
		}
	}

	static public void raise()
	{
		throw new RuntimeException( "runtime exception" );
	}
}
