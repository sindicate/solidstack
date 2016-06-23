/*--
 * Copyright 2016 René M. de Bloois
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package solidstack.classgen;

import solidstack.lang.Assert;

public class Types
{
	static public enum VMTYPE { INT, LONG, FLOAT, DOUBLE, REF, VOID }

	static public String toFieldDescriptor( java.lang.Class<?> cls )
	{
		if( cls.isPrimitive() )
			return toBaseType( cls );
		if( cls.isArray() )
			return "[" + toFieldDescriptor( cls.getComponentType() );
		return "L" + cls.getName().replace( '.', '/' ) + ";";
	}

	static public String toMethodDescriptor( java.lang.Class<?> ret, java.lang.Class<?>... parameters )
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

	static public String classDescriptorToClassName( String descriptor )
	{
		return descriptor.replace( '/', '.' );
	}

	static public String fieldDescriptorToClassName( String descriptor )
	{
		Assert.isTrue( descriptor.charAt( 0 ) == 'L' );
		String result = descriptor.substring( 1, descriptor.length() - 1 );
		return result.replace( '/', '.' );
	}

	static public String classNameToFieldDescriptor( String className )
	{
		String result = "L" + className + ";";
		return result.replace( '.', '/' );
	}

	static public VMTYPE methodDescriptorToVMType( String descriptor )
	{
		int pos = descriptor.indexOf( ')' );
		if( pos < 0 )
			throw new ClassGenException( ") not found" );
		return fieldDescriptorToVMType( descriptor.substring( pos + 1 ) );
	}

	public static VMTYPE fieldDescriptorToVMType( String descriptor )
	{
		char t = descriptor.charAt( 0 );
		switch( t )
		{
			case 'I':
			case 'Z':
			case 'C':
			case 'B':
			case 'S': return VMTYPE.INT;
			case 'D': return VMTYPE.DOUBLE;
			case 'J': return VMTYPE.LONG;
			case 'F': return VMTYPE.FLOAT;
			case 'V': return VMTYPE.VOID;
			case '[':
			case 'L': return VMTYPE.REF;
		}
		throw new ClassGenException( "Unexpected: " + t );
	}
}
