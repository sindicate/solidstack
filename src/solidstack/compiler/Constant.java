package solidstack.compiler;

import java.io.IOException;

abstract public class Constant
{
	private int index;

	abstract public void write( Bytes bytes ) throws IOException;

	public void setIndex( int index )
	{
		this.index = index;
	}

	public int index()
	{
		return this.index;
	}
}
