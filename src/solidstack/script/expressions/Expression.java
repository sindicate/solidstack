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

package solidstack.script.expressions;

import solidstack.io.SourceLocation;
import solidstack.script.ThreadContext;


/**
 * An expression.
 */
public interface Expression
{
	/**
	 * Evaluate the expression.
	 *
	 * @param thread The thread context.
	 * @return The result of evaluating this expression.
	 */
	Expression compile();

	/**
	 * Evaluate the expression.
	 *
	 * @param thread The thread context.
	 * @return The result of evaluating this expression.
	 */
	Object evaluate( ThreadContext thread );

	/**
	 * @return The source location where this expression is encountered.
	 */
	SourceLocation getLocation();

	/**
	 * Output this expression to a string.
	 *
	 * @param out To write the expression to.
	 */
	void writeTo( StringBuilder out );
}
