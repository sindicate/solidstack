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
import java.util.HashMap;
import java.util.Map;

import solidstack.io.Resource;
import solidstack.io.SourceException;
import solidstack.io.SourceLocation;
import solidstack.io.SourceReader;
import solidstack.io.SourceReaders;
import solidstack.io.UTFEncodingDetector;
import solidstack.script.java.Java;
import solidstack.script.objects.Type;
import solidstack.script.scopes.DefaultScope;
import solidstack.script.scopes.Scope;


// TODO Not used yet
public class GlobalFunctions
{
	private Map<String, Object> modules = new HashMap<>();

	public Object brake()
	{
		return null;
	}

	public Object call( String path )
	{
		return compileFile( path ).eval( new DefaultScope() );
	}

	public Class classOf( Class cls )
	{
		return cls; // The Type wrapper is already removed by conversion to Java
	}

	public Script compile( String source )
	{
		return Script.compile( source );
	}

	public Script compileFile( String path )
	{
		ThreadContext thread = ThreadContext.get();

		SourceLocation location = thread.getStackHead();
		Resource resource = location.getResource();
		// TODO What if the resource is null?
		resource = resource.resolve( path );

		SourceReader reader;
		try
		{
			reader = SourceReaders.forResource( resource, UTFEncodingDetector.INSTANCE, "UTF-8" );
		}
		catch( FileNotFoundException e )
		{
			throw new ThrowException( "File not found: " + resource, thread.cloneStack() );
		}

		try
		{
			return Script.compile( reader );
		}
		catch( SourceException e )
		{
			throw new ThrowException( e.getMessage(), thread.cloneStack() );
		}
	}

	public Scope load( String path )
	{
		Scope result = new DefaultScope();
		compileFile( path ).eval( result );
		return result;
	}

	public Type loadClass( Class cls )
	{
		return new Type( cls );
	}

	public Object loadClass( String cls )
	{
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		try
		{
			return new Type( Java.forName( cls, loader ) );
		}
		catch( ClassNotFoundException e )
		{
			throw new ThrowException( "Class not found: " + cls, ThreadContext.get().cloneStack() ); // TODO Is this correct exception?
		}
	}

	public Object print( Object object )
	{
		System.out.print( object.toString() ); // Or should we call toString() through reflection?
		return object;
	}

	public Object println()
	{
		System.out.println();
		return null;
	}

	public Object println( Object object ) // TODO Vararg?
	{
		System.out.println( object.toString() ); // Or should we call toString() through reflection?
		return object;
	}

	public Object require( String path )
	{
		path += ".funny";

		ThreadContext thread = ThreadContext.get();

		SourceLocation location = thread.getStackHead();
		Resource resource = location.getResource().resolve( path ); // TODO What if the resource is null?
		String id = resource.getNormalized();

		if( this.modules.containsKey( id ) )
			return this.modules.get( id );

		SourceReader reader;
		try
		{
			reader = SourceReaders.forResource( resource, UTFEncodingDetector.INSTANCE, "UTF-8" );
		}
		catch( FileNotFoundException e )
		{
			throw new ThrowException( "File not found: " + resource, thread.cloneStack() );
		}

		Script script;
		try
		{
			script = Script.compile( reader );
		}
		catch( SourceException e )
		{
			throw new ThrowException( e.getMessage(), thread.cloneStack() );
		}

		Object object = script.eval( new DefaultScope() );
		this.modules.put( id, object );

		return object;
	}
}
