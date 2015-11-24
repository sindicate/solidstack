package solidstack.lang;

public class DynamicClassLoader extends ClassLoader
{
	public DynamicClassLoader( ClassLoader parent )
	{
		super( parent );
	}

	public Class<?> defineClass( String name, byte[] bytes )
	{
		return defineClass( name, bytes, 0, bytes.length );
	}
}
