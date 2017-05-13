package solidstack.javac;

import java.io.IOException;
import java.io.InputStream;

public class InputStreamJavaFileObject extends AbstractJavaFileObject
{
	private InputStream in;

	public InputStreamJavaFileObject( String name, Kind kind, InputStream in )
	{
		super( name, kind );
		this.in = in;
	}

	@Override
	public InputStream openInputStream() throws IOException
	{
		return this.in;
	}

	@Override
	public String toString()
	{
		return "InputStreamJavaFileObject [getName()=" + getName() + ", getKind()=" + getKind() + "]";
	}
}
