package solidstack.cbor;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.testng.annotations.Test;


public class SlidingByteStringIndexTests
{
	@Test
	public void test1()
	{
		SlidingByteStringIndex index = new SlidingByteStringIndex( 4, 64 );
		ByteString b1 = new ByteString( false, new byte[] { 1, 0 } );
		ByteString b2 = new ByteString( false, new byte[] { 2, 0 } );
		ByteString b3 = new ByteString( false, new byte[] { 3, 0 } );
		ByteString b4 = new ByteString( false, new byte[] { 4, 0 } );
		ByteString b5 = new ByteString( false, new byte[] { 5, 0 } );
		ByteString b6 = new ByteString( false, new byte[] { 6, 0 } );

		index.put( b1 );
		assertThat( index.get( b1 ) ).isEqualTo( 0 );

		index.put( b2 );
		assertThat( index.get( b1 ) ).isEqualTo( 1 );
		assertThat( index.get( b2 ) ).isEqualTo( 0 );

		index.put( b3 );
		assertThat( index.get( b1 ) ).isEqualTo( 2 );
		assertThat( index.get( b2 ) ).isEqualTo( 1 );
		assertThat( index.get( b3 ) ).isEqualTo( 0 );

		index.put( b4 );
		assertThat( index.get( b1 ) ).isEqualTo( 3 );
		assertThat( index.get( b2 ) ).isEqualTo( 2 );
		assertThat( index.get( b3 ) ).isEqualTo( 1 );
		assertThat( index.get( b4 ) ).isEqualTo( 0 );

		index.put( b5 );
		assertThat( index.get( b1 ) ).isNull();
		assertThat( index.get( b2 ) ).isEqualTo( 3 );
		assertThat( index.get( b3 ) ).isEqualTo( 2 );
		assertThat( index.get( b4 ) ).isEqualTo( 1 );
		assertThat( index.get( b5 ) ).isEqualTo( 0 );

		index.put( b6 );
		assertThat( index.get( b1 ) ).isNull();
		assertThat( index.get( b2 ) ).isNull();
		assertThat( index.get( b3 ) ).isEqualTo( 3 );
		assertThat( index.get( b4 ) ).isEqualTo( 2 );
		assertThat( index.get( b5 ) ).isEqualTo( 1 );
		assertThat( index.get( b6 ) ).isEqualTo( 0 );

		index.put( b3 ); // The one that would fall off the window
		assertThat( index.get( b1 ) ).isNull();
		assertThat( index.get( b2 ) ).isNull();
		assertThat( index.get( b4 ) ).isEqualTo( 3 );
		assertThat( index.get( b5 ) ).isEqualTo( 2 );
		assertThat( index.get( b6 ) ).isEqualTo( 1 );
		assertThat( index.get( b3 ) ).isEqualTo( 0 );

		ByteString b7 = new ByteString( false, new byte[] { 1, 0 } );
		index.put( b7 ); // Not in the index
		assertThat( index.get( b1 ) ).isEqualTo( 0 );
		assertThat( index.get( b2 ) ).isNull();
		assertThat( index.get( b4 ) ).isNull();
		assertThat( index.get( b5 ) ).isEqualTo( 3 );
		assertThat( index.get( b6 ) ).isEqualTo( 2 );
		assertThat( index.get( b3 ) ).isEqualTo( 1 );
		assertThat( index.get( b7 ) ).isEqualTo( 0 );

		// TODO The old one is removed from the index, so we have holes, so the index will not contain as much as it can, but otherwise a performance problem?
		ByteString b8 = new ByteString( false, new byte[] { 6, 0 } );
		index.put( b8 ); // Already in the index
		assertThat( index.get( b1 ) ).isEqualTo( 1 );
		assertThat( index.get( b2 ) ).isNull();
		assertThat( index.get( b4 ) ).isNull();
		assertThat( index.get( b5 ) ).isEqualTo( 3 );
		assertThat( index.get( b6 ) ).isEqualTo( 0 );
		assertThat( index.get( b3 ) ).isEqualTo( 2 );
		assertThat( index.get( b7 ) ).isEqualTo( 1 );
		assertThat( index.get( b8 ) ).isEqualTo( 0 );
	}

	@Test
	public void test2()
	{
		int count = 1000;

		SlidingByteStringIndex tree = new SlidingByteStringIndex( 1000, Integer.MAX_VALUE );

		List<ByteString> index = new ArrayList<>();

		Random rnd = new Random( 0 ); // Always generates the same sequence
		for( int i = 0; i < count; i++ )
		{
//			System.out.println( i );
			byte in = (byte)rnd.nextInt( 100 );

			ByteString s = new ByteString( false, new byte[] { in, in, in, in, in, in, in } );

			int ii = index.indexOf( s );
			Integer iii = tree.putOrGet( s );
			if( ii >= 0 )
			{
				assertThat( ii ).isEqualTo( iii );
				index.remove( ii );
			}
			else
				assertThat( iii ).isNull();

			index.add( 0, s );

			int len = index.size();
			for( int j = 0; j < len; j++ )
			{
				s = index.get( j );
				int iiii = tree.get( s );
				assertThat( iiii ).isEqualTo( j );
			}
		}
	}
}
