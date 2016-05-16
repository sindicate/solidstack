package solidstack.classgen;

import solidstack.lang.Assert;

public class Types
{
	static public String toFieldDescriptor( java.lang.Class<?> cls )
	{
		if( cls.isPrimitive() )
			return toBaseType( cls );
		if( cls.isArray() )
			return "[" + toFieldDescriptor( cls.getComponentType() );
		return "L" + cls.getName().replace( '.', '/' ) + ";";
	}

	public static String toMethodDescriptor( java.lang.Class<?> ret, java.lang.Class<?>... parameters )
	{
		StringBuilder descriptor = new StringBuilder();
		descriptor.append( '(' );
		for( java.lang.Class<?> parameter : parameters )
			descriptor.append( toFieldDescriptor( parameter ) );
		descriptor.append( ')' );
		if( ret == null )
			descriptor.append( 'V' );
		else
			descriptor.append( toFieldDescriptor( ret ) );
		return descriptor.toString();
	}

	static public String toBaseType( java.lang.Class<?> cls )
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

	public static String classDescriptorToClassName( String descriptor )
	{
		return descriptor.replace( '/', '.' );
	}

	public static String fieldDescriptorToClassName( String descriptor )
	{
		Assert.isTrue( descriptor.charAt( 0 ) == 'L' );
		String result = descriptor.substring( 1, descriptor.length() - 1 );
		return result.replace( '/', '.' );
	}

	public static String classNameToFieldDescriptor( String className )
	{
		String result = "L" + className + ";";
		return result.replace( '.', '/' );
	}
}
