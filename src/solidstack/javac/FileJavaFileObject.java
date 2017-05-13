package solidstack.javac;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileJavaFileObject extends AbstractJavaFileObject
{
	private File file;

	public FileJavaFileObject( String name, Kind kind, File file )
	{
		super( name, kind );
		this.file = file;
	}

	@Override
	public String toString()
	{
		return "FileJavaFileObject [file=" + this.file + ", getName()=" + getName() + ", getKind()=" + getKind() + "]";
	}

	@Override
	public InputStream openInputStream() throws IOException
	{
		return new FileInputStream( this.file ); // TODO Need to buffer this?
	}
}
