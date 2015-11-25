/*--
 * Copyright 2015 René M. de Bloois
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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class ExtensionMethod
{
	private Method method; // A static method
	private Class[] parameterTypes; // Possible memory leak, but no worries, these are parameter types for the extension methods and will only have standard types.
	private boolean isVararg;

	public ExtensionMethod( Method method )
	{
		this.method = method;

		Class[] types = method.getParameterTypes();
		int count = types.length - 1;
		this.parameterTypes = new Class[ count ];
		System.arraycopy( types, 1, this.parameterTypes, 0, count );

		this.isVararg = ( method.getModifiers() & Modifier.TRANSIENT ) != 0;
	}

	public Class[] getParameterTypes()
	{
		return this.parameterTypes;
	}

	public boolean isVararg()
	{
		return this.isVararg;
	}

	public Method getMethod()
	{
		return this.method;
	}
}
