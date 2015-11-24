package solidstack.javac;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URI;

import javax.tools.SimpleJavaFileObject;


public class ClassFile extends SimpleJavaFileObject
{
	private ByteArrayOutputStream baos = new ByteArrayOutputStream();

	public ClassFile( String className )
	{
		super( URI.create( className ), Kind.CLASS );
	}

	@Override
	public OutputStream openOutputStream()
	{
		return this.baos;
	}

	public byte[] getBytes()
	{
		return this.baos.toByteArray();
	}
}
