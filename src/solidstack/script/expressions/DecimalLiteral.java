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

import java.math.BigDecimal;

import solidstack.io.SourceLocation;
import solidstack.script.ThreadContext;

public class DecimalLiteral extends LocalizedExpression
{
	private BigDecimal value;


	public DecimalLiteral( SourceLocation location, BigDecimal value )
	{
		super( location );

		this.value = value;
	}

	@Override
	public Expression compile()
	{
		return this;
	}

	@Override
	public BigDecimal evaluate( ThreadContext thread )
	{
		return this.value;
	}

	@Override
	public void writeTo( StringBuilder out )
	{
		out.append( this.value );
	}
}
