package solidstack.javac;

import java.net.URI;

import javax.tools.SimpleJavaFileObject;


public class JavaFile extends SimpleJavaFileObject
{
	private String contents = null;

	public JavaFile( String className, String contents )
	{
		super( URI.create( "string:///" + className.replace( '.', '/' ) + Kind.SOURCE.extension ), Kind.SOURCE );
		this.contents = contents;
	}

	@Override
	public CharSequence getCharContent( boolean ignoreEncodingErrors )
	{
		return this.contents;
	}
}
