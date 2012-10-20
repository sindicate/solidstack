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

package solidstack.script.operations;

import java.math.BigDecimal;

import solidstack.lang.Assert;
import solidstack.script.Context;
import solidstack.script.Expression;
import solidstack.script.FunctionInstance;
import solidstack.script.Operation;


public class Assign extends Operation
{
	public Assign( String name, Expression left, Expression right)
	{
		super( name, left, right );
	}

	@Override
	public Object evaluate( Context context )
	{
		Object right = evaluateAndUnwrap( this.right, context );
		if( right == null || right instanceof BigDecimal || right instanceof String || right instanceof FunctionInstance )
		{
			// TODO Replace this with Value?
			this.left.assign( context, right );
			return right;
		}
		Assert.fail( "Unexpected " + this.right.getClass() );
		return null;
	}
}