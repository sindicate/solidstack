package solidstack.template.java;

import java.net.URI;
import java.util.Iterator;

import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;


public class StringJavaFileObject extends SimpleJavaFileObject implements Iterable<JavaFileObject>
{
	final StringBuilder code;

	StringJavaFileObject( String name, StringBuilder code )
	{
		super( URI.create( "string:///" + name.replace( '.', '/' ) + Kind.SOURCE.extension ), Kind.SOURCE );
		this.code = code;
	}

	@Override
	public CharSequence getCharContent( boolean ignoreEncodingErrors )
	{
		return this.code;
	}

	@Override
	public Iterator<JavaFileObject> iterator()
	{
		return new Iterator<JavaFileObject>()
		{
			boolean done;

			@Override
			public boolean hasNext()
			{
				return !this.done;
			}

			@Override
			public JavaFileObject next()
			{
				this.done = true;
				return StringJavaFileObject.this;
			}
		};
	}
}
