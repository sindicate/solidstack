/*--
 * Copyright 2012 René M. de Bloois
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

package solidstack.httpserver;

import java.io.IOException;
import java.io.InputStream;


public class DefaultServlet implements Servlet
{
	public HttpResponse call( RequestContext context )
	{
//		Response response = context.getResponse();

		String url = context.getRequest().getUrl();
		if( url.startsWith( "/" ) )
			url = url.substring( 1 );

		// TODO Should we open in the response?
		// TODO Introduce a ResourceLoader. One that can also be used by the TemplateLoader.
		final InputStream in = DefaultServlet.class.getClassLoader().getResourceAsStream( url );
		if( in == null )
			return new StatusResponse( 404, "Not found" );

		String e = null;
		int pos = url.lastIndexOf( '.' );
		if( pos > 0 )
			if( pos > url.lastIndexOf( '/' ) )
				e = url.substring( pos + 1 );
		final String extension = e;

		return new HttpResponse()
		{
			@Override
			public void write( ResponseOutputStream out )
			{
				if( extension.equals( "properties" ) )
					out.setContentType( "text/plain", "ISO-8859-1" );
				else if( extension.equals( "ico" ) )
					out.setContentType( "image/vnd.microsoft.icon", null );
				else if( extension.equals( "js" ) )
					out.setContentType( "text/javascript", null );
				else if( extension.equals( "css" ) )
					out.setContentType( "text/css", null );

				out.setHeader( "Cache-Control", "max-age=3600" );

				try
				{
					try
					{
						// TODO Make the buffer size configurable
						byte[] buffer = new byte[ 4096 ];
						int len = in.read( buffer );
						while( len >= 0 )
						{
							out.write( buffer, 0, len );
							len = in.read( buffer );
						}
					}
					finally
					{
						in.close();
					}
				}
				catch( IOException e )
				{
					throw new HttpException( e );
				}
			}
		};
	}
}
