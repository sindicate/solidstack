package solidstack.compiler;

import java.io.DataOutputStream;
import java.io.IOException;

abstract public class Constant
{
	private int index;

	abstract public void write( DataOutputStream out ) throws IOException;

	public void setIndex( int index )
	{
		this.index = index;
	}

	public int index()
	{
		return this.index;
	}
}
