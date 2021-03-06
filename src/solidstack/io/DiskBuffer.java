/*--
 * Copyright 2016 Ren� M. de Bloois
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

package solidstack.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.BitSet;

import org.apache.commons.collections.primitives.ArrayIntList;
import org.apache.commons.collections.primitives.IntIterator;
import org.apache.commons.collections.primitives.IntList;


/**
 * A map that stores the values in a temporary file instead of memory.
 *
 * @author Ren� M. de Bloois
 * @since 2016
 */
// TODO Do we need file extents? Grow with bigger jumps?
public class DiskBuffer
{
	static public final int DEFAULT_BLOCKSIZE = 0x1000;

	/**
	 * Block size for the temporary file.
	 */
	final int blockSize;

	/**
	 * Random access file.
	 */
	private RandomAccessFile store;

	/**
	 * Underlying temporary file.
	 */
	private File tempFile;

	/**
	 * A bit array with a bit for every block. A bit is set if the block is in use.
	 */
	private BitSet usedBlocks;

	/**
	 * Lower limit for the empty blocks. Improves performance for searching empty blocks.
	 */
	private int emptyBlockStart;


	/**
	 * Constructs a disk buffer with the default block size {@link #DEFAULT_BLOCKSIZE}.
	 *
	 * @param filePrefix The prefix of the temporary file.
	 */
	public DiskBuffer( String filePrefix )
	{
		this( filePrefix, DEFAULT_BLOCKSIZE );
	}

	/**
	 * Constructs a disk map with the given block size.
	 *
	 * @param filePrefix The prefix of the temporary file.
	 * @param blockSize The block size for the data written to the temporary file.
	 */
	public DiskBuffer( String filePrefix, int blockSize )
	{
		try
		{
			this.tempFile = File.createTempFile( filePrefix, null );
			this.blockSize = blockSize;
			this.store = new RandomAccessFile( this.tempFile, "rw" );
			this.usedBlocks = new BitSet();
			this.emptyBlockStart = 0;
		}
		catch( IOException e )
		{
			throw new FatalIOException( e );
		}
	}

	public InputStream buffer( InputStream in )
	{
		DiskMapOutputStream out = new DiskMapOutputStream();
		byte[] buffer = new byte[ this.blockSize ];
		try
		{
			for( int len = in.read( buffer ); len >= 0; len = in.read( buffer ) )
				out.write( buffer, 0, len ); // TODO Use buffer?
			out.close();
		}
		catch( IOException e )
		{
			throw new FatalIOException( e );
		}

		return out.toInputStream();
	}

	int writeBlock( byte[] bytes ) throws IOException
	{
		int block = this.usedBlocks.nextClearBit( this.emptyBlockStart );
		this.emptyBlockStart = block + 1;
		this.usedBlocks.set( block );
		this.store.seek( (long)block * this.blockSize );
		this.store.write( bytes );
		return block;
	}

	void readBlock( int block, byte[] bytes ) throws IOException
	{
		this.store.seek( (long)block * this.blockSize );
		this.store.read( bytes );
	}

	void clearBlocks( IntList blocks )
	{
		for( IntIterator i = blocks.iterator(); i.hasNext(); )
		{
			int block = i.next();
			if( block < this.emptyBlockStart )
				this.emptyBlockStart = block;
			this.usedBlocks.clear( block );
		}
	}

	/**
	 * Closes the disk map, deletes the temporary file and removes the shutdown hook.
	 *
	 * @throws IOException
	 */
	public void close()
	{
		try
		{
			this.store.close();
		}
		catch( IOException e )
		{
			throw new FatalIOException( e );
		}
		this.tempFile.delete();
	}

	public long length() throws IOException
	{
		return this.store.length();
	}

	public int getBlockSize()
	{
		return this.blockSize;
	}

	private class DiskMapOutputStream extends OutputStream
	{
		private ByteBuffer buffer = ByteBuffer.allocate( DiskBuffer.this.blockSize );
		private IntList blocks = new ArrayIntList();
		private int size;

		public DiskMapOutputStream()
		{
			// TODO Auto-generated constructor stub
		}

		@Override
		public void write( int b ) throws IOException
		{
			this.buffer.put( (byte)b );
			if( !this.buffer.hasRemaining() )
				writeBuffer();
		}

		@Override
		public void write( byte[] b, int off, int len ) throws IOException
		{
			int blockSize = DiskBuffer.this.blockSize;
			int rem = this.buffer.remaining();
			if( len >= rem )
			{
				this.buffer.put( b, off, rem );
				writeBuffer();
				off += rem;
				len -= rem;
			}
			while( len >= blockSize )
			{
				this.buffer.put( b, off, blockSize );
				writeBuffer();
				off += blockSize;
				len -= blockSize;
			}
			if( len > 0 )
				this.buffer.put( b, off, len );
		}

		@Override
		public void close() throws IOException
		{
			if( this.buffer.position() > 0 )
				writeBuffer();
			super.close();
		}

		private void writeBuffer() throws IOException
		{
			int block = writeBlock( this.buffer.array() );
			this.blocks.add( block );
			this.size += this.buffer.position();
			this.buffer.rewind();
		}

		public InputStream toInputStream()
		{
			return new DiskMapInputStream( this.blocks, this.size );
		}
	}

	private class DiskMapInputStream extends InputStream
	{
		private ByteBuffer buffer;
		private IntList blocks;
		private int currentBlock;
		private int size;

		public DiskMapInputStream( IntList blocks, int size )
		{
			this.buffer = ByteBuffer.allocate( DiskBuffer.this.blockSize );
			this.buffer.position( this.buffer.limit() );
			this.blocks = blocks;
			this.size = size;
			this.currentBlock = 0;
		}

		@Override
		public int read() throws IOException
		{
			if( this.size <= 0 )
				return -1;
			this.size--;

			if( !this.buffer.hasRemaining() )
				readBuffer();

			return this.buffer.get() & 255;
		}

		@Override
		public int read( byte[] b, int off, int len ) throws IOException
		{
			if( len == 0 )
				return 0;
			if( len > this.size )
				len = this.size;
			if( len <= 0 )
				return -1;
			this.size -= len;

			int l = len;
			while( l > 0 )
			{
				int rem = this.buffer.remaining();
				if( rem <= 0 )
				{
					readBuffer();
					rem = DiskBuffer.this.blockSize;
				}
				if( l <= rem )
				{
					this.buffer.get( b, off, l );
					break;
				}
				this.buffer.get( b, off, rem );
				off += rem;
				l -= rem;
			}
			return len;
		}

		private void readBuffer() throws IOException
		{
			readBlock( this.blocks.get( this.currentBlock++ ), this.buffer.array() );
			this.buffer.rewind();
		}
	}
}
