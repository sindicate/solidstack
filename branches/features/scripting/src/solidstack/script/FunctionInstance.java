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

import java.util.List;

public class FunctionInstance
{
	private List<String> parameters;
	private Expression block;

	public FunctionInstance( List<String> parameters, Expression block )
	{
		this.parameters = parameters;
		this.block = block;
	}

	public Object call( Context context, List<?> pars )
	{
		int count = this.parameters.size();
		if( count != pars.size() )
			throw new ScriptException( "Parameter count mismatch" );

		context = new SubContext( context );

		// TODO If we keep the Link we get output parameters!
		for( int i = 0; i < count; i++ )
		{
			Object value = pars.get( i );
			if( value instanceof Value )
				value = ( (Value)value ).get();
			context.set( this.parameters.get( i ), value );
		}
		return this.block.evaluate( context );
	}
}