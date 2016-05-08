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

package solidstack.template.scriptengine;

import java.io.IOException;
import java.io.Writer;

import solidstack.template.ConvertingWriter;
import solidstack.template.EncodingWriter;


/**
 * A ConvertingWriter that converts Groovy specific data types to Java data types.
 *
 * @author René de Bloois
 */
public class ScriptEngineConvertingWriter extends Writer implements ConvertingWriter
{
	/**
	 * The EncodingWriter to write to.
	 */
	protected EncodingWriter writer;

	/**
	 * Constructor.
	 *
	 * @param writer The EncodingWriter to write to.
	 */
	public ScriptEngineConvertingWriter( EncodingWriter writer )
	{
		this.writer = writer;
	}

	@Override
	public void write( Object o ) throws IOException
	{
		if( o == null )
			this.writer.write( null );
		else
			this.writer.write( o.toString() );
	}

	@Override
	public void writeEncoded( Object o ) throws IOException
	{
		if( o == null )
			this.writer.writeEncoded( null );
		else if( o instanceof String )
			this.writer.writeEncoded( o );
		else if( this.writer.stringsOnly() )
			this.writer.writeEncoded( o.toString() );
		else
			this.writer.writeEncoded( o );
	}

	@Override
	public void flush() throws IOException
	{
		this.writer.flush();
	}

	@Override
	public void write( char[] cbuf, int off, int len ) throws IOException
	{
		this.writer.write( new String( cbuf, off, len ) );
	}

	@Override
	public void close() throws IOException
	{
		// Nothing to do
	}
}
