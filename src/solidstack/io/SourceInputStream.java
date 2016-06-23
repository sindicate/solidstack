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

package solidstack.io;

import java.io.IOException;
import java.io.InputStream;

public class SourceInputStream extends InputStream
{
	private InputStream in;
	private Resource resource;

	private long pos;


	public SourceInputStream( InputStream in, Resource resource, long pos )
	{
		this.in = in;
		this.resource = resource;
		this.pos = pos;
	}

	@Override
	public void close()
	{
		try
		{
			this.in.close();
		}
		catch( IOException e )
		{
			throw new FatalIOException( e );
		}
	}

	public Resource getResource()
	{
		return this.resource;
	}

	public long getPos()
	{
		return this.pos;
	}

	@Override
	public int read() throws IOException
	{
		int result = this.in.read();
		if( result >= 0 )
			this.pos++;
		return result;
	}

	@Override
	public int read( byte[] b, int off, int len ) throws IOException
	{
		int read = this.in.read( b, off, len );
		if( read > 0 )
			this.pos += read;
		return read;
	}
}
