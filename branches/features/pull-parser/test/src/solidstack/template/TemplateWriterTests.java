/*--
 * Copyright 2006 Ren� M. de Bloois
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

package solidstack.template;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;


public class TemplateWriterTests
{
	@Test
	public void testXMLWriter() throws IOException
	{
		StringWriter s = new StringWriter();
		EncodingWriter writer = new XMLEncodingWriter( s );

		writer.writeEncoded( "1234&1234" );
		Assert.assertEquals( s.toString(), "1234&amp;1234" );

		writer.writeEncoded( "&<>\"'" );
		Assert.assertEquals( s.toString(), "1234&amp;1234&amp;&lt;&gt;&#034;&#039;" );
	}

	@Test
	public void testXMLTemplate() throws IOException
	{
		TemplateManager templates = new TemplateManager();
		templates.setPackage( "solidstack.template" );

		Template template = templates.getTemplate( "test.gxml" );
		Map< String, Object > pars = new HashMap< String, Object >();
		pars.put( "test", "&" );

		String result = template.apply( pars );
		Assert.assertEquals( result, "<test>&amp;&</test>" );
	}
}
