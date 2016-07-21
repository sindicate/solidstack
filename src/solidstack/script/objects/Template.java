/*--
 * Copyright 2012 René M. de Bloois
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

package solidstack.script.objects;

import java.io.IOException;
import java.io.StringWriter;

import funny.Symbol;
import solidstack.io.SourceLocation;
import solidstack.lang.SystemException;
import solidstack.script.ThreadContext;
import solidstack.script.expressions.Expression;
import solidstack.script.scopes.ParameterScope;
import solidstack.script.scopes.Scope;
import solidstack.template.EncodingWriter;
import solidstack.template.NoEncodingWriter;
import solidstack.template.funny.FunnyConvertingWriter;


public class Template implements Expression
{
	private Expression expression;


	public Template( Expression expression )
	{
		this.expression = expression;
	}

	@Override
	public Expression compile()
	{
		this.expression = this.expression.compile();
		return this;
	}

	@Override
	public Object evaluate( ThreadContext thread )
	{
		StringWriter out1 = new StringWriter();
		EncodingWriter out2 = new NoEncodingWriter( out1 );
		FunnyConvertingWriter out3 = new FunnyConvertingWriter( out2 );

		ParameterScope parScope = new ParameterScope( thread.getScope() );
		parScope.defParameter( Symbol.apply( "out" ), out3 );

		Scope old = thread.swapScope( parScope );
		try
		{
			this.expression.evaluate( thread );
			out3.flush();
			return out1.toString();
		}
		// TODO Returning?
		catch( IOException e )
		{
			throw new SystemException( e );
		}
		finally
		{
			thread.swapScope( old );
		}
	}

	@Override
	public SourceLocation getLocation()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void writeTo( StringBuilder out )
	{
		throw new UnsupportedOperationException();
	}
}
