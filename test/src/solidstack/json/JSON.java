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

package solidstack.json;

import java.io.FileNotFoundException;

import org.testng.annotations.Test;

import solidstack.io.FileResource;
import solidstack.io.Resource;
import solidstack.io.SourceReader;
import solidstack.io.SourceReaders;

public class JSON
{
	@Test
	public void testJSON1() throws FileNotFoundException
	{
		Resource resource = new FileResource( "test/json/test1.json" );
		SourceReader reader = SourceReaders.forResource( resource );
		JSONReader json = new JSONReader( reader );
		Object object = json.read();
		json.close();

		resource = new FileResource( "json/output1.json" );
		JSONWriter writer = new JSONWriter( resource );
		writer.writeFormatted( object, 80 );
		writer.close();
	}

	@Test
	public void testJSON2() throws FileNotFoundException
	{
		Resource resource = new FileResource( "test/json/test2.json" );
		SourceReader reader = SourceReaders.forResource( resource );
		JSONReader json = new JSONReader( reader );
		Object object = json.read();

		resource = new FileResource( "json/output2.json" );
		JSONWriter writer = new JSONWriter( resource );
		writer.writeFormatted( object, 80 );
		writer.close();
	}
}
