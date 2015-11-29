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

package solidstack.template.java;

import java.util.Map;

import solidstack.template.EncodingWriter;
import solidstack.template.Template;
import solidstack.template.java.scope.DefaultScope;
import solidstack.template.java.scope.Scope;


/**
 * A compiled Java template.
 *
 * @author René M. de Bloois
 */
abstract public class JavaTemplate extends Template
{
	@Override
	public void apply( Object params, EncodingWriter writer )
	{
		if( params instanceof Map<?, ?> )
			execute( writer, new DefaultScope( (Map<String, Object>)params ) );
		else if( params instanceof Scope )
			execute( writer, (Scope)params );
		else
			throw new UnsupportedOperationException( params.getClass().getName() + " not supported as params" );
	}

	abstract public void execute( EncodingWriter out, Scope scope );
}
