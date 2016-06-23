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

package solidstack.classgen.bytecode;

import solidstack.classgen.Types.VMTYPE;
import solidstack.classgen.constants.CClass;
import solidstack.classgen.constants.CFieldref;
import solidstack.classgen.constants.CMethodref;


public class ExpressionBuilder
{
	public ExpressionBuilder()
	{
	}

	public AccessLocal local( int local, VMTYPE type )
	{
		return new AccessLocal( local, type );
	}

	public AssignLocal setLocal( int local, Expression value )
	{
		return new AssignLocal( local, value );
	}

	public AssignField setField( Expression instance, CFieldref field, Expression value )
	{
		return new AssignField( instance, field, value );
	}

	public Statement callSuper( AccessLocal var, CMethodref methodref )
	{
		return new CallSuper( var, methodref );
	}

	public Expression field( Expression instance, CFieldref fieldref )
	{
		return new AccessField( instance, fieldref );
	}

	public Expression initArray( CClass type, Expression... expression )
	{
		return new InitArray( type, expression );
	}

	public Expression call( Expression instance, CMethodref methodref, Expression... parameters )
	{
		return new CallMethod( instance, methodref, parameters );
	}

	public Expression callStatic( CMethodref methodref, Expression... parameters )
	{
		return new CallStaticMethod( methodref, parameters );
	}

	public Expression cast( Expression call, CClass type )
	{
		return new Cast( call, type );
	}

	public Expression literal( int value )
	{
		return new LiteralInt( value );
	}

	public Expression literal( CClass value )
	{
		return new LiteralClass( value );
	}

	public Conditional lessThan( Expression left, Expression right )
	{
		return new LessThan( left, right );
	}

	public Expression plus( Expression left, Expression right )
	{
		return new Plus( left, right );
	}
}
