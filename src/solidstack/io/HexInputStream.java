package solidstack.io;

import java.io.IOException;


public class HexInputStream extends SourceInputStream
{
	private SourceReader reader;
	private boolean startOfLine;
	private boolean eof;


	public HexInputStream( SourceReader reader )
	{
		this.reader = reader;
		this.startOfLine = true;
	}

	@Override
	public Resource getResource()
	{
		throw new UnsupportedOperationException();
	}

	// TODO Cache location in the SourceReader
	@Override
	public SourceLocation getLocation()
	{
		return this.reader.getLocation();
	}

	@Override
	public int read() throws IOException
	{
		if( this.eof )
			return -1;

		SourceReader in = this.reader;
		int ch;
		for( ;; )
		{
			SourceLocation loc = in.getLocation();
			switch( ch = in.read() )
			{
				case -1:
					return -1;
				case ' ':
				case '\t':
					continue;
				case '-':
					loc = in.getLocation();
					ch = in.read();
					if( ch != '-' )
						throw new SourceException( "Unexpected: " + ch, loc );
					do
						ch = in.read();
					while( ch != '\n' && ch != -1 );
					this.startOfLine = true;
					continue;
				case '\n':
					if( this.startOfLine )
					{
						this.eof = true;
						return -1;
					}
					this.startOfLine = true;
					continue;
			}
			this.startOfLine = false;
			switch( ch )
			{
				case '0': case '1': case '2': case '3': case '4': case '5': case '6': case '7': case '8': case '9':
				case 'A': case 'B': case 'C': case 'D': case 'E': case 'F':
				case 'a': case 'b': case 'c': case 'd': case 'e': case 'f':
					loc = in.getLocation();
					int ch2 = in.read();
					switch( ch2 )
					{
						case '0': case '1': case '2': case '3': case '4': case '5': case '6': case '7': case '8': case '9':
						case 'A': case 'B': case 'C': case 'D': case 'E': case 'F':
						case 'a': case 'b': case 'c': case 'd': case 'e': case 'f':
							return Integer.parseInt( String.valueOf( new char[] { (char)ch, (char)ch2 } ), 16 );
						default:
							throw new SourceException( "Unexpected: " + ch2, loc );
					}
				default:
					throw new SourceException( "Unexpected char: " + (char)ch, loc );
			}
		}
	}

	@Override
	public void close()
	{
		throw new UnsupportedOperationException();
	}
}
