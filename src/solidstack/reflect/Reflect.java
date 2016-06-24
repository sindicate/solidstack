package solidstack.reflect;

import java.lang.reflect.Field;

import solidstack.lang.SystemException;

public class Reflect
{
	static public <T> T getPrivate( Object object, String name )
	{
		Field field;
		try
		{
			field = object.getClass().getDeclaredField( name );
		}
		catch( NoSuchFieldException e )
		{
			throw new SystemException( e );
		}
		if( !field.isAccessible() )
			field.setAccessible( true );
		try
		{
			return (T)field.get( object );
		}
		catch( IllegalAccessException e )
		{
			throw new SystemException( e );
		}
	}
}
