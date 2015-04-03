/*--
 * Copyright 2012 Ren� M. de Bloois
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

package solidstack.script.operators;

import solidstack.script.Script;
import solidstack.script.ThreadContext;
import solidstack.script.UndefinedPropertyException;
import solidstack.script.expressions.Expression;


public class And extends Operator
{
	public And( String name, Expression left, Expression right)
	{
		super( name, left, right );
	}

	public Object evaluate( ThreadContext thread )
	{
		try
		{
			Object left = this.left.evaluate( thread );
			if( !Script.isTrue( left ) )
				return left;
		}
		catch( UndefinedPropertyException e ) // TODO But what if deeper into the expression this exception is thrown?
		{
			return null;
		}
		return this.right.evaluate( thread );
	}
}
