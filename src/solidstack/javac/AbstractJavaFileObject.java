package solidstack.javac;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.tools.JavaFileObject;

abstract public class AbstractJavaFileObject implements JavaFileObject
{
	private Kind kind;
	private String name;

	public AbstractJavaFileObject( String name, Kind kind )
	{
		this.kind = kind;
		this.name = name;
	}

	@Override
	public String getName()
	{
		return this.name;
	}

	@Override
	public Kind getKind()
	{
		return this.kind;
	}

	@Override
	public URI toUri()
	{
		throw new UnsupportedOperationException( this.getClass().getName() );
	}


	@Override
	public InputStream openInputStream() throws IOException
	{
		throw new UnsupportedOperationException( this.getClass().getName() );
	}

	@Override
	public OutputStream openOutputStream() throws IOException
	{
		throw new UnsupportedOperationException( this.getClass().getName() );
	}

	@Override
	public Reader openReader( boolean ignoreEncodingErrors ) throws IOException
	{
		throw new UnsupportedOperationException( this.getClass().getName() );
	}

	@Override
	public CharSequence getCharContent( boolean ignoreEncodingErrors ) throws IOException
	{
		throw new UnsupportedOperationException( this.getClass().getName() );
	}

	@Override
	public Writer openWriter() throws IOException
	{
		throw new UnsupportedOperationException( this.getClass().getName() );
	}

	@Override
	public long getLastModified()
	{
		throw new UnsupportedOperationException( this.getClass().getName() );
	}

	@Override
	public boolean delete()
	{
		throw new UnsupportedOperationException( this.getClass().getName() );
	}

	@Override
	public boolean isNameCompatible( String simpleName, Kind kind )
	{
		throw new UnsupportedOperationException( this.getClass().getName() );
	}

	@Override
	public NestingKind getNestingKind()
	{
		throw new UnsupportedOperationException( this.getClass().getName() );
	}

	@Override
	public Modifier getAccessLevel()
	{
		throw new UnsupportedOperationException( this.getClass().getName() );
	}
}
