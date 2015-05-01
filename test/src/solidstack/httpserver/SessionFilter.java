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

import java.util.HashMap;
import java.util.Map;


public class SessionFilter implements Filter
{
	private volatile int sessionid;
	private Map< String, Session > sessions = new HashMap<String, Session>();

	public HttpResponse call( RequestContext context, FilterChain chain )
	{
		// TODO Synchronization
		String sessionId = context.getRequest().getCookie( "SESSIONID" );
		Loggers.httpServer.debug( "session: {}", sessionId );

		Session session = null;
		if( sessionId != null )
			session = this.sessions.get( sessionId );

		if( session != null )
		{
			context.setSession( session );
			return chain.call( context );
		}

		session = new Session();
		context.setSession( session );
		final String newSessionId = Integer.toString( ++this.sessionid );
		this.sessions.put( newSessionId, session );

		final HttpResponse response = chain.call( context );
		return new HttpResponse()
		{
			@Override
			public void write( ResponseOutputStream out )
			{
				Loggers.httpServer.debug( "setCookie: session: {}", newSessionId );
				out.setCookie( "SESSIONID", newSessionId );
				response.write( out );
			}
		};
	}
}
