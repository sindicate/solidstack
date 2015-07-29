package solidstack.compiler;

public class Types
{
	static public String toFieldDescriptor( Class<?> cls )
	{
		if( cls.isPrimitive() )
			return toBaseType( cls );
		if( cls.isArray() )
			throw new UnsupportedOperationException( "arrays not supported yet" );
		return "L" + cls.getName().replace( '.', '/' ) + ";";
	}

	public static String toMethodDescriptor( Class<?> ret, Class<?>... parameters )
	{
		StringBuilder descriptor = new StringBuilder();
		descriptor.append( '(' );
		for( Class<?> parameter : parameters )
			descriptor.append( toFieldDescriptor( parameter ) );
		descriptor.append( ')' );
		if( ret == null )
			descriptor.append( 'V' );
		else
			descriptor.append( toFieldDescriptor( ret ) );
		return descriptor.toString();
	}

	static public String toBaseType( Class<?> cls )
	{
		if( cls == int.class )
			return "I";
		if( cls == boolean.class )
			return "Z";
		if( cls == double.class )
			return "D";
		if( cls == char.class )
			return "C";
		if( cls == long.class )
			return "J";
		if( cls == byte.class )
			return "B";
		if( cls == float.class )
			return "F";
		if( cls == short.class )
			return "S";
		if( cls == void.class )
			return "V";
		throw new UnsupportedOperationException( "Unknown primitive type [" + cls.getName() + "]" );
	}
}
