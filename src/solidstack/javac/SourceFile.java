package solidstack.javac;

import java.net.URI;

public class SourceFile extends AbstractJavaFileObject
{
	private CharSequence contents = null;

	public SourceFile( String name, CharSequence contents )
	{
		super( name, Kind.SOURCE );
		this.contents = contents;
	}

	@Override
	public CharSequence getCharContent( boolean ignoreEncodingErrors )
	{
		return this.contents;
	}

	@Override
	public Kind getKind()
	{
		return Kind.SOURCE;
	}

	@Override
	public boolean isNameCompatible( String simpleName, Kind kind )
	{
		if( kind != Kind.SOURCE )
			return false;
		String name = getName();
		return simpleName.equals( name ) || name.endsWith( "." + simpleName );
	}

	@Override
	public URI toUri()
	{
		return URI.create( getName() );
	}
}
