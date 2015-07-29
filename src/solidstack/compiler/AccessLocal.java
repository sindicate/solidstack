package solidstack.compiler;

import java.io.DataOutputStream;
import java.io.IOException;

public class AccessLocal extends Expression
{
	private Method method;
	private int local;

	public AccessLocal( Method method, int local )
	{
		this.method = method;
		this.local = local;
	}

	@Override
	public ConstantClass classInfo()
	{
		if( this.local == 0 ) // TODO And non static
			return this.method.classBuilder().classInfo();
		throw new UnsupportedOperationException();
	}

	@Override
	public void getByteCode( DataOutputStream out ) throws IOException
	{
		out.writeByte( 0x19 );
		out.writeByte( this.local );
	}

	@Override
	public void collectConstants( ConstantPool pool )
	{
	}

	@Override
	public String getFieldDescriptor()
	{
		throw new UnsupportedOperationException();
	}
}
