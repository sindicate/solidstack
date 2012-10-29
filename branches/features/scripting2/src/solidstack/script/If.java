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

package solidstack.script;

import solidstack.io.SourceLocation;



public class If extends LocalizedExpression
{
	private Expression condition;
	private Expression left;
	private Expression right;


	public If( SourceLocation location, Expression condition, Expression left, Expression right )
	{
		super( location );

		this.condition = condition;
		this.left = left;
		this.right = right;
	}

	public Object evaluate( ThreadContext thread )
	{
		if( Operation.isTrue( Operation.evaluateAndUnwrap( this.condition, thread ) ) )
		{
			if( this.left != null )
				return this.left.evaluate( thread );
		}
		else
		{
			if( this.right != null )
				return this.right.evaluate( thread );
		}
		return null;
	}
}
