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
import java.net.MalformedURLException;
import java.net.URI;


/**
 * A factory to create some difficult data structures.
 *
 * @author Ren� M. de Bloois
 */
public final class ResourceFactory
{
	/**
	 * This utility class cannot be instantiated.
	 */
	private ResourceFactory()
	{
		super();
	}

	/**
	 *
	 * @param parent The parent folder of the resource.
	 * @param path The path for the resource.
	 * @return The resource.
	 */
	static public Resource getResource( String path )
	{
		if( path.equals( "-" ) )
			return new SystemInOutResource();

		if( path.startsWith( "classpath:" ) )
			return new ClassPathResource( path );

		if( path.startsWith( "file:" ) )
			return new FileResource( path );

		try
		{
			return new URLResource( path );
		}
		catch( MalformedURLException e )
		{
			return new FileResource( path );
		}
	}

	/**
	 *
	 * @param parent The parent folder of the resource.
	 * @param path The path for the resource.
	 * @return The resource.
	 */
	static public Resource getResource( File file )
	{
		return new FileResource( file );
	}

	/**
	 *
	 * @param parent The parent folder of the resource.
	 * @param path The path for the resource.
	 * @return The resource.
	 */
	static public Resource getResource( URI uri )
	{
		// TODO Do it the other way around
		return getResource( uri.toString() );
	}

	static public Resource currentFolder()
	{
		return getResource( "" ); // TODO Unit test
	}

	static public String folderize( String path )
	{
		if( path.endsWith( "/" ) || path.endsWith( "\\" ) )
			return path;
		return path + "/";
	}
}
