package solidstack.template;

import java.io.IOException;
import java.io.Writer;


/**
 * An encoding writer for XML.
 * 
 * @author Ren� M. de Bloois
 */
public class XMLEncodingWriter extends NoEncodingWriter
{
	/**
	 * A factory for producing new XMLEncodingWriters.
	 */
	static public final EncodingWriterFactory FACTORY = new EncodingWriterFactory()
	{
		//@Override
		public NoEncodingWriter createWriter( Writer writer )
		{
			return new XMLEncodingWriter( writer );
		}
	};

	/**
	 * Constructor.
	 * 
	 * @param writer The writer to write to.
	 */
	public XMLEncodingWriter( Writer writer )
	{
		super( writer );
	}

	/**
	 * Write the specified string to the writer XML encoded.
	 * 
	 * @param s The string to write.
	 * @throws IOException Whenever an IOException occurs.
	 */
	@Override
	public void writeEncoded( String s ) throws IOException
	{
		if( s == null )
			return;

		char[] chars = s.toCharArray();
		int len = chars.length;
		int start = 0;
		String replace = null;
		for( int i = 0; i < len; )
		{
			switch( chars[ i ] )
			{
				case '&': replace = "&amp;"; break;
				case '<': replace = "&lt;"; break;
				case '>': replace = "&gt;"; break;
				case '"': replace = "&#034;"; break;
				case '\'': replace = "&#039;"; break;
				default:
			}
			if( replace != null )
			{
				this.writer.write( chars, start, i - start );
				this.writer.write( replace );
				replace = null;
				start = ++i;
			}
			else
				i++;
		}
		this.writer.write( chars, start, len - start );
	}
}