package solidstack.classgen.bytecode;

import solidstack.classgen.Bytes;

public class Jump
{
	private Bytes bytes;
	private int pos;

	public Jump( Bytes bytes, int pos )
	{
		this.bytes = bytes;
		this.pos = pos;
	}

	public void link( int dest )
	{
		this.bytes.writeShortAt( this.pos + 1, dest - this.pos );
	}
}
