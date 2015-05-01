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

import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import solidstack.template.Template;
import solidstack.template.TemplateLoader;
import solidstack.template.TemplateNotFoundException;
import solidstack.util.Pars;

public class SltServlet implements Servlet
{
	private TemplateLoader loader;

	public SltServlet( TemplateLoader loader )
	{
		this.loader = loader;
	}

	public HttpResponse call( RequestContext context )
	{
		// TODO / should be allowed after fixing the other todo
		String url = context.getRequest().getParameter( "path" );
//		if( url.startsWith( "/" ) )
//			url = url.substring( 1 );

		try
		{
			final Template template = this.loader.getTemplate( url );
			final Pars pars = new Pars( "session", context.getSession(), "request", context.getRequest(), "args", context.getArgs() ); // TODO response
			return new HttpResponse()
			{
				@Override
				public void write( ResponseOutputStream out )
				{
					String contentType = template.getContentType();
					String charSet = template.getCharSet();
					out.setContentType( contentType, charSet );

					// Actually, when content type is not set, the char set is not added to the response. But then again, it is set, so we must use it.
					if( charSet != null )
						try
						{
							template.apply( pars, new OutputStreamWriter( out, charSet ) );
						}
						catch( UnsupportedEncodingException e )
						{
							throw new HttpException( e );
						}
					else
						template.apply( pars, new OutputStreamWriter( out ) );
				}
			};
		}
		catch( TemplateNotFoundException e )
		{
			return new StatusResponse( 404, "Not found" );
		}

//		url = url.replaceAll( "[\\\\/]", "." );
//		url = url.replaceAll( "[\\.-]", "_" );
//		request.applicationContext.callJsp( url, request );
	}
}
