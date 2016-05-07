package solidstack.io;

import static org.fest.assertions.api.Assertions.assertThat;

import java.io.Reader;
import java.io.StringReader;

import org.testng.annotations.Test;

public class SourceReaderTests
{
	@Test
	public void testReadLine()
	{
		String newlines = "line1\nline2";

		Reader reader = new StringReader( newlines );
		SourceReader source = new ReaderSourceReader( reader );
		String line = source.readLine();
		assertThat( line ).isEqualTo( "line1" );
		line = source.readLine();
		assertThat( line ).isEqualTo( "line2" );

		source = new StringSourceReader( newlines );
		line = source.readLine();
		assertThat( line ).isEqualTo( "line1" );
		line = source.readLine();
		assertThat( line ).isEqualTo( "line2" );
	}

	@Test
	public void testRewindEOF()
	{
		String text = "x";

		Reader reader = new StringReader( text );
		SourceReader source = new ReaderSourceReader( reader );
		assertThat( source.read() ).isEqualTo( 'x' );
		assertThat( source.read() ).isEqualTo( -1 );
		source.rewind();
		assertThat( source.read() ).isEqualTo( -1 );

		source = new StringSourceReader( text );
		assertThat( source.read() ).isEqualTo( 'x' );
		assertThat( source.read() ).isEqualTo( -1 );
		source.rewind();
		assertThat( source.read() ).isEqualTo( -1 );
	}
}
