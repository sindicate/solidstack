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

import java.util.ArrayList;
import java.util.List;

import solidstack.classgen.bytecode.Conditional;
import solidstack.classgen.bytecode.Expression;
import solidstack.classgen.bytecode.For;
import solidstack.classgen.bytecode.Pop;
import solidstack.classgen.bytecode.Return;
import solidstack.classgen.bytecode.Statement;
import solidstack.classgen.constants.CMethodref;
import solidstack.classgen.constants.CUtf8;


public class Method
{
	private ClassFile cls;
	private String name;

	private List<Statement> statements = new ArrayList<Statement>();

	private CMethodref methodref;


	public Method( ClassFile classFile, CMethodref methodref )
	{
		this.cls = classFile;
		this.methodref = methodref;
	}

	public ClassFile classBuilder()
	{
		return this.cls;
	}

	public void write( Bytes bytes )
	{
		bytes.writeShort( 0x1001 ); // public & synthetic
		bytes.writeShort( this.methodref.nameIndex() ); // name_index
		bytes.writeShort( this.methodref.typeIndex() ); // descriptor_index

		// attributes
		bytes.writeShort( 1 ); // attributes_count

		CUtf8 codeAttribute = this.cls.addUtf8( "Code" );

		// Code
		Bytes code = getCode();
		bytes.writeShort( codeAttribute.index() );
		bytes.writeInt( code.size() );
		bytes.write( code );
	}

	private Bytes getCode()
	{
		Bytes bytes = new Bytes();

		Bytes byteCode = getByteCode();

		bytes.writeShort( 10 ); // TODO max_stack
		bytes.writeShort( 10 ); // TODO max_locals
		bytes.writeInt( byteCode.size() ); // code_length
		bytes.write( byteCode );
		bytes.writeShort( 0 ); // TODO exception_table_length
		bytes.writeShort( 0 ); // TODO attributes_length

		return bytes;
	}

	private Bytes getByteCode()
	{
		Bytes bytes = new Bytes();

		for( Statement stat : this.statements )
			stat.toByteCode( bytes );

		return bytes;
	}

	public void return_()
	{
		this.statements.add( new Return() );
	}

	public void return_( Expression value )
	{
		this.statements.add( new Return( value ) );
	}

	public void pop( Expression value )
	{
		this.statements.add( new Pop( value ) );
	}

	public void statement( Statement statement )
	{
		this.statements.add( statement );
	}

	public void for_( Statement initialization, Conditional termination, Statement increment, Statement... statements )
	{
		this.statements.add( new For( initialization, termination, increment, statements ) );
	}
}
