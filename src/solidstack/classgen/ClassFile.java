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

import solidstack.classgen.constants.CClass;
import solidstack.classgen.constants.CFieldref;
import solidstack.classgen.constants.CMethodref;
import solidstack.classgen.constants.CUtf8;
import solidstack.classgen.constants.Constant;
import solidstack.classgen.constants.ConstantPool;

public class ClassFile
{
	private ConstantPool pool = new ConstantPool();
	private Class cls;


	public ClassFile()
	{
	}

	public CUtf8 addUtf8( String value )
	{
		return this.pool.addUtf8( value );
	}

	public CClass addClass( String name )
	{
		return this.pool.addClass( name );
	}

	public CClass addClass( java.lang.Class<?> type )
	{
		return addClass( type.getName() );
	}

	public CUtf8 addFieldType( java.lang.Class<?> type )
	{
		return this.pool.addUtf8( Types.toFieldDescriptor( type ) );
	}

	public CUtf8 addMethodType( java.lang.Class<?> ret, java.lang.Class<?>... args )
	{
		return this.pool.addUtf8( Types.toMethodDescriptor( ret, args ) );
	}

	public CFieldref addFieldref( CClass classInfo, String name, CUtf8 type )
	{
		return this.pool.addFieldref( classInfo, this.pool.addNameAndType( name, type ) );
	}

	public CMethodref addMethodref( CClass classInfo, String name, CUtf8 type )
	{
		return this.pool.addMethodref( classInfo, this.pool.addNameAndType( name, type ) );
	}

	public Class createClass( CClass name )
	{
		return this.cls = new Class( name );
	}

	public Bytes generate()
	{
		if( this.cls == null )
			throw new ClassGenException( "No class set" );

		Bytes bytes = new Bytes();

		bytes.writeInt( 0xCAFEBABE ); // magic
		bytes.writeShort( 0 ); // minor version
		bytes.writeShort( 49 ); // major version

		// Write the constant pool
		this.pool.addUtf8( "Code" ); // TODO Attribute names only if needed

		bytes.writeShort( this.pool.size() + 1 );
		for( Constant constant : this.pool.constants() )
			constant.write( bytes );

		// Write the class
		this.cls.write( bytes );

		// attributes
		bytes.writeShort( 0 );

		return bytes;
	}
}
