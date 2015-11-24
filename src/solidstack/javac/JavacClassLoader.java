package solidstack.javac;

public class JavacClassLoader extends ClassLoader
{
	public JavacClassLoader( ClassLoader parent )
	{
		super( parent );
	}

	protected Class<?> defineClass( String name, byte[] bytes )
	{
		return defineClass( name, bytes, 0, bytes.length );
	}
}
