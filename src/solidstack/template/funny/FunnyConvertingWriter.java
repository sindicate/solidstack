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

package solidstack.template.funny;

import java.io.IOException;

import solidstack.script.objects.PString;
import solidstack.template.ConvertingWriter;
import solidstack.template.EncodingWriter;


/**
 * A ConvertingWriter that converts FunnyScript specific data types to Java data types.
 *
 * @author René de Bloois
 */
public class FunnyConvertingWriter implements ConvertingWriter
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
	public FunnyConvertingWriter( EncodingWriter writer )
	{
		this.writer = writer;
	}

	public void write( Object o ) throws IOException
	{
		if( o == null )
			this.writer.write( null );
		else if( o instanceof String )
			this.writer.write( (String)o );
		else if( o instanceof PString )
		{
			PString pString = (PString)o;
			String[] strings = pString.getFragments();
			Object[] values = pString.getValues();
			int v = 0;
			for( int i = 0; i < strings.length; i++ )
			{
				if( strings[ i ] != null )
					this.writer.write( strings[ i ] );
				else
					writeEncoded( values[ v++ ] );
			}
		}
//		else if( o instanceof Closure )
//		{
//			Closure c = (Closure)o;
//			int pars = c.getMaximumNumberOfParameters();
//			if( pars > 0 )
//				throw new TemplateException( "Closures with parameters are not supported in expressions." );
//			write( c.call() ); // May be recursive
//		}
		else
			this.writer.write( o.toString() );
//			this.writer.write( (String)InvokerHelper.invokeMethod( o, "asType", String.class ) );
	}

	public void writeEncoded( Object o ) throws IOException
	{
		if( o == null )
			this.writer.writeEncoded( null );
		else if( o instanceof String )
			this.writer.writeEncoded( o );
		else if( o instanceof PString )
			this.writer.writeEncoded( o.toString() );
//		else if( o instanceof Closure )
//		{
//			Closure c = (Closure)o;
//			int pars = c.getMaximumNumberOfParameters();
//			if( pars > 0 )
//				throw new TemplateException( "Closures with parameters are not supported in expressions." );
//			writeEncoded( c.call() ); // May be recursive
//		}
		else
		{
			if( this.writer.stringsOnly() )
//				this.writer.writeEncoded( InvokerHelper.invokeMethod( o, "asType", String.class ) );
				this.writer.write( o.toString() );
			else
				this.writer.writeEncoded( o );
		}
	}

	public void flush() throws IOException
	{
		this.writer.flush();
	}
}
