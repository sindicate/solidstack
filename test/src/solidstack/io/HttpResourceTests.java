/*--
 * Copyright 2012 Ren� M. de Bloois
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
import java.net.URL;

import org.testng.annotations.Test;

import solidstack.httpserver.ApplicationContext;
import solidstack.httpserver.DefaultServlet;
import solidstack.httpserver.Server;

public class HttpResourceTests
{
	@Test
	static public void test1() throws InterruptedException, IOException
	{
		Server server = new Server( 123 );
		server.addApplication( new ApplicationContext()
		{
			{
				registerServlet( ".*", new DefaultServlet() );
			}
		} );

		server.start();

		URL url = new URL( "http://localhost:123/styles.css" );
		Object content = url.getContent();
		System.out.println( content.getClass() );

		server.interruptAndJoin();
	}
}
