package solidstack.util;


/**
 * A buffer.
 *
 * @param <E> The type of the items in this buffer.
 */
public class WindowBuffer<E>
{
	private int size;

	private E[] elems;
	private int reader;
	private int writer;
	private int mark = -1;


	/**
	 * Instantiates a new buffer.
	 *
	 * @param capacity The number of items this buffer can contain.
	 */
	@SuppressWarnings( "unchecked" )
	public WindowBuffer( int capacity )
	{
		if( capacity < 2 )
			throw new IllegalArgumentException( "Capacity must be at least 2" );
		this.size = capacity + 1;
		this.elems = (E[])new Object[ this.size ];
	}

	/**
	 * Are there items to be read?
	 *
	 * @return true if there are items to be read, false otherwise.
	 */
	public boolean hasRemaining()
	{
		return this.reader != this.writer;
	}

	/**
	 * Get the next item.
	 *
	 * @return The next item.
	 */
	public E get()
	{
		if( !hasRemaining() )
			throw new IllegalStateException( "Buffer underflow" );
		E result = this.elems[ this.reader ];
		this.reader = inc( this.reader );
		return result;
	}

	/**
	 * Returns the last item that was read or written.
	 *
	 * @return The last item that was read or written.
	 */
	public E last()
	{
		// Can't detect if the buffer has sufficiently filled up here
		return this.elems[ dec( this.reader ) ];
	}

	/**
	 * Returns the before last item that was read or written.
	 *
	 * @return The before last item that was read or written.
	 */
	public E beforeLast()
	{
		// Can't detect if the buffer has sufficiently filled up here
		return this.elems[ dec( dec( this.reader ) ) ];
	}

	/**
	 * Write an item into the buffer.
	 *
	 * @param item The item to write into the buffer.
	 */
	public void put( E item )
	{
		this.elems[ this.writer ] = item;
		this.reader = this.writer = inc( this.writer );
		if( this.mark == this.writer )
			this.mark = -1;
	}

	/**
	 * Rewind to the last item read or written.
	 *
	 * @return The last item that has been read or written.
	 */
	public E rewind()
	{
		int r = dec( this.reader );
		if( this.writer == r )
			throw new IllegalStateException( "Buffer underflow" );
		return this.elems[ this.reader = r ];
	}

	/**
	 * Mark the current read position.
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
			throw new IllegalStateException( "Mark expired or not set" );
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
