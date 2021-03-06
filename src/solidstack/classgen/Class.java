/*--
 * Copyright 2016 Ren� M. de Bloois
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

import java.util.ArrayList;
import java.util.List;

import solidstack.classgen.constants.CClass;
import solidstack.classgen.constants.CFieldref;
import solidstack.classgen.constants.CMethodref;

public class Class
{
	private CClass info;
	private CClass superInfo;
	private CClass[] interfacesInfo;
	private List<Field> fields = new ArrayList<>();
	private List<Method> methods = new ArrayList<>();


	public Class( CClass info )
	{
		this.info = info;
	}

	public void extend( CClass info )
	{
		this.superInfo = info;
	}

	public void implement( CClass... info )
	{
		this.interfacesInfo = info;
	}

	public Field addField( CFieldref fieldref )
	{
		Field result = new Field( fieldref );
		this.fields.add( result );
		return result;
	}

	public Method addMethod( ClassFile classFile, CMethodref methodref )
	{
		Method result = new Method( classFile, methodref );
		this.methods.add( result );
		return result;
	}

	public String name()
	{
		return Types.classDescriptorToClassName( this.info.name() );
	}

	public void write( Bytes bytes )
	{
		if( this.superInfo == null )
			throw new ClassGenException( "No super class set" );

		bytes.writeShort( 0x1001 ); // public & synthetic
		bytes.writeShort( this.info.index() ); // this_class

		// super_class
		bytes.writeShort( this.superInfo.index() ); // super_class

		// interfaces
		if( this.interfacesInfo == null )
			bytes.writeShort( 0 );
		else
		{
			bytes.writeShort( this.interfacesInfo.length ); // interfaces_count
			for( CClass info : this.interfacesInfo )
				bytes.writeShort( info.index() );
		}

		// fields
		bytes.writeShort( this.fields.size() );
		for( Field field : this.fields )
			field.write( bytes );

		// methods
		bytes.writeShort( this.methods.size() );
		for( Method method : this.methods )
			method.write( bytes );
	}
}
