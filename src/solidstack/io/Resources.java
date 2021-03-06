/*--
 * Copyright 2009 Ren� M. de Bloois
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

package solidstack.io;

import java.io.File;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.BitSet;


/**
 * A factory to create resources.
 *
 * @author Ren� M. de Bloois
 */
public final class Resources
{
	private Resources()
	{
		// Utility class
	}

	/**
	 * @param path The path for the resource.
	 * @return The resource.
	 */
	static public Resource getResource( String path )
	{
		if( path.equals( "-" ) )
			return new SystemInOutResource();
		URI uri = URI.create( path );
		String scheme = uri.getScheme();
		if( scheme == null || scheme.length() == 1 || scheme.equals( "file" ) && uri.getAuthority() == null )
			return new FileResource( path ); // The path, not the uri: enables relative paths
		return getResource( uri );
	}

	/**
	 * @param file A file.
	 * @return The resource.
	 */
	static public Resource getResource( File file )
	{
		return new FileResource( file );
	}

	/**
	 * @param uri The URI of the resource.
	 * @return The resource.
	 */
	static public Resource getResource( URI uri )
	{
		String scheme = uri.getScheme();
		if( scheme == null || scheme.length() == 1 || scheme.equals( "file" ) && uri.getAuthority() == null )
			return new FileResource( uri );
		if( scheme.equals( "classpath" ) )
			return new ClassPathResource( uri );
		return new URIResource( uri );
	}

	/**
	 * @param bytes Bytes.
	 * @return The resource.
	 */
	static public Resource getResource( byte[] bytes )
	{
		return new MemoryResource( bytes );
	}

	/**
	 * @return The current folder as a Resource.
	 */
	static public Resource currentFolder()
	{
		return new FileResource( "" ); // TODO Unit test
	}

	/**
	 * Makes sure the path ends with a / or \.
	 *
	 * @param path The path to folderize.
	 * @return The folderized path.
	 */
	static public String folderize( String path )
	{
		if( path.endsWith( "/" ) || path.endsWith( "\\" ) )
			return path;
		return path + "/";
	}

	static private BitSet no;
	static private Charset UTF8 = Charset.forName( "UTF-8" ); // TODO Use this everywhere

	static
	{
		no = new BitSet( 256 );
		for( int i = 'a'; i <= 'z'; i++ )
			no.set( i );
		for( int i = 'A'; i <= 'Z'; i++ )
			no.set( i );
		for( int i = '0'; i <= '9'; i++ )
			no.set( i );
		no.set( '-' );
		no.set( '_' );
		no.set( '.' );
		no.set( '*' );
		no.set( '/' );
		no.set( '\\' );
		no.set( ':' );
		no.set( '(' );
		no.set( ')' );
	}

	// TODO Is this according to some kind of spec?
	static public String path2uri( String path )
	{
		byte[] bytes = path.getBytes( UTF8 );
		int len = bytes.length;
		StringBuilder out = new StringBuilder( len );
		boolean changed = false;
		for( int i = 0; i < len; i++ )
		{
			int c = bytes[ i ];
			if( no.get( c ) )
				out.append( (char)c );
			else
			{
				changed = true;
				out.append( '%' );
				out.append( Character.toUpperCase( Character.forDigit( c >> 4, 16 ) ) );
				out.append( Character.toUpperCase( Character.forDigit( c & 0xF, 16 ) ) );
			}
		}

		return changed ? out.toString() : path;
	}
}
