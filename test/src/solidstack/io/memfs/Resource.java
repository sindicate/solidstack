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

package solidstack.io.memfs;

import java.io.FileNotFoundException;
import java.io.Reader;
import java.io.StringReader;

public class Resource extends solidstack.io.Resource
{
	private String name;
	private String contents;
	private Folder parent;

	public Resource( Folder parent, String name, String contents )
	{
		this.parent = parent;
		this.name = name;
		this.contents = contents;
	}

	public void setContents( String contents )
	{
		this.contents = contents;
	}

	@Override
	public String getNormalized()
	{
		return "mem:" + this.name;
	}

	@Override
	public boolean supportsReader()
	{
		return true;
	}

	@Override
	public Reader newReader() throws FileNotFoundException
	{
		return new StringReader( this.contents );
	}

	@Override
	public solidstack.io.Resource resolve( String path )
	{
		if( path.indexOf( '/' ) != -1 )
			throw new UnsupportedOperationException();
		return this.parent.getResource( path );
	}

	@Override
	public String toString()
	{
		return this.name;
	}
}
