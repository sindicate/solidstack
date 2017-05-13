package solidstack.javac;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import solidstack.io.ByteMatrixInputStream;
import solidstack.io.ByteMatrixOutputStream;

public class ClassFile extends AbstractJavaFileObject
{
	private ByteMatrixOutputStream bytes = new ByteMatrixOutputStream( 1024 );

	public ClassFile( String name )
	{
		super( name, Kind.CLASS );
	}

	@Override
	public OutputStream openOutputStream()
	{
		return this.bytes;
	}

	@Override
	public InputStream openInputStream() throws IOException
	{
		return new ByteMatrixInputStream( this.bytes.toByteMatrix() );
	}

	public byte[] getBytes()
	{
		return this.bytes.toByteArray();
	}

	@Override
	public boolean delete()
	{
		return false;
	}
}
