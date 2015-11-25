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

import java.io.FileNotFoundException;

import funny.Symbol;
import solidstack.io.Resource;
import solidstack.io.Resources;
import solidstack.io.SourceException;
import solidstack.io.SourceReader;
import solidstack.io.SourceReaders;
import solidstack.io.UTFEncodingDetector;
import solidstack.script.scopes.DefaultScope;


public class Exec
{
	// TODO Use user exceptions
	// TODO System.exit() but without disturbing the unit tests. Maybe look at the stack trace to see.
	public static void main( String... args )
	{
		if( args.length == 0 )
		{
			System.out.println( "Usage: solidstack.script.Exec <script file> [ args... ]" );
			return;
		}

		Resource resource = Resources.getResource( args[ 0 ] );

		SourceReader reader;
		try
		{
			reader = SourceReaders.forResource( resource, UTFEncodingDetector.INSTANCE, "UTF-8" );
		}
		catch( FileNotFoundException e )
		{
			System.err.println( "File not found: " + resource );
			return;
		}

		Script script;
		try
		{
			script = Script.compile( reader );
		}
		catch( SourceException e )
		{
			System.err.println( e.getMessage() );
			return;
		}

		DefaultScope scope = new DefaultScope();
		scope.var( Symbol.apply( "args" ), args );

		try
		{
			script.eval( scope );
		}
		catch( ScriptException e )
		{
			System.err.println( e.getMessage() );
			return;
		}
	}
}
