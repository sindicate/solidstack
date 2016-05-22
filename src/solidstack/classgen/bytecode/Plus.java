package solidstack.classgen.bytecode;

import solidstack.classgen.Bytes;
import solidstack.classgen.Types.VMTYPE;

public class Plus implements Expression
{
	private Expression left;
	private Expression right;

	public Plus( Expression left, Expression right )
	{
		this.left = left;
		this.right = right;
	}

	@Override
	public VMTYPE vmType()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void toByteCode( Bytes bytes )
	{
		this.left.toByteCode( bytes );
		this.right.toByteCode( bytes );
		bytes.writeByte( 0x60 ); // iadd
	}
}
