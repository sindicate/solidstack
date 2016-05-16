package solidstack.classgen;

public class Loader extends ClassLoader
{
	public java.lang.Class<?> load( String name, byte[] bytes )
	{
		return defineClass( name, bytes, 0, bytes.length );
	}
}
