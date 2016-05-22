package solidstack.classgen.constants;

import solidstack.classgen.Bytes;

abstract public class Constant
{
	private int index;

	abstract public void write( Bytes bytes );

	public void setIndex( int index )
	{
		this.index = index;
	}

	public int index()
	{
		return this.index;
	}
}
