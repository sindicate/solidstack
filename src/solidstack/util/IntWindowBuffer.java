/*--
 * Copyright 2016 René M. de Bloois
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

package solidstack.util;


/**
 * An int buffer.
 */
public class IntWindowBuffer
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
	public IntWindowBuffer( int capacity )
	{
		if( capacity < 2 )
			throw new IllegalArgumentException( "Capacity must be at least 2" );
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
	 */
	public int get()
	{
		if( !hasRemaining() )
			throw new IllegalStateException( "Buffer underflow" );
		int result = this.ints[ this.reader ];
		this.reader = inc( this.reader );
		return result;
	}

	/**
	 * Returns the before last int that has been read or written.
	 *
	 * @return The before last int that has been read or written.
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
	 */
	public int rewind()
	{
		int r = dec( this.reader );
		if( this.writer == r )
			throw new IllegalStateException( "Buffer underflow" );
		return this.ints[ this.reader = r ];
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
