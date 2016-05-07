package solidstack.io;


/**
 * An int buffer.
 */
public class RotatingIntBuffer
{
	private int size;

	private int[] ints;
	private int reader;
	private int writer;
	private int mark = -1;


	/**
	 * Instantiates a new int buffer.
	 *
	 * @param capacity The number of ints this buffer can contain.
	 */
	public RotatingIntBuffer( int capacity )
	{
		if( capacity < 2 )
			throw new FatalIOException( "Capacity must be at least 2" );
		this.size = capacity + 1;
		this.ints = new int[ this.size ];
	}

	/**
	 * Are there ints to be read?
	 *
	 * @return true if there are ints to be read, false otherwise.
	 */
	public boolean hasRemaining()
	{
		return this.reader != this.writer;
	}

	/**
	 * Get the next int.
	 *
	 * @return The next int.
	 * @throws FatalIOException whenever there are no ints to be read.
	 */
	public int get()
	{
		if( !hasRemaining() )
			throw new FatalIOException( "Buffer underflow" );
		int result = this.ints[ this.reader ];
		this.reader = inc( this.reader );
		return result;
	}

	/**
	 * Returns the before last int that has been read or written.
	 *
	 * @return The before last in that has been read or written.
	 */
	public int beforeLast()
	{
		// Can't detect if the buffer has sufficiently filled up here
		return this.ints[ dec( dec( this.reader ) ) ];
	}

	/**
	 * Write an int into the buffer.
	 *
	 * @param i The int to write into the buffer.
	 */
	public void put( int i )
	{
		this.ints[ this.writer ] = i;
		this.reader = this.writer = inc( this.writer );
		if( this.mark == this.writer )
			this.mark = -1;
	}

	/**
	 * Rewind to the last int read or written.
	 *
	 * @return The last int that has been read or written.
	 * @throws FatalIOException whenever you are rewinding too far.
	 */
	public int rewind()
	{
		int r = dec( this.reader );
		if( this.writer == r )
			throw new FatalIOException( "Buffer underflow" );
		return this.ints[ this.reader = r ];
	}

	/**
	 * Mark the corrent read position.
	 */
	public void mark()
	{
		this.mark = this.reader;
	}

	/**
	 * Reset the read position to the marked position.
	 *
	 * @return The number of positions moved.
	 */
	public int reset()
	{
		if( this.mark < 0 )
			throw new FatalIOException( "Mark expired or not set" );
		int delta = this.reader - this.mark;
		if( delta < 0 )
			delta += this.size;
		this.reader = this.mark;
		return delta;
	}

	private int inc( int i )
	{
		i++;
		if( i < this.size )
			return i;
		return 0;
	}

	private int dec( int i )
	{
		if( i > 0 )
			return i - 1;
		return this.size - 1;
	}
}
