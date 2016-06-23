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

package solidstack.script.java;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import solidstack.script.objects.FunctionObject;

public class FunctionObjectInvocationHandler implements InvocationHandler
{
	private FunctionObject function;
	private Method method;


	public FunctionObjectInvocationHandler( FunctionObject function, Method method )
	{
		this.function = function;
		this.method = method;
	}

	@Override
	public Object invoke( Object proxy, Method method, Object[] args ) throws Throwable
	{
		if( method.equals( this.method ) )
			return Types.convert( this.function.call( args ), Integer.class );
		throw new UnsupportedOperationException( "Method not supported: " + method );
	}
}
