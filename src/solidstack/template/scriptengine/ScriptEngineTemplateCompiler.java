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

package solidstack.template.scriptengine;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import solidstack.io.StringBuilderReader;
import solidstack.script.java.Java;
import solidstack.template.TemplateCompilerContext;
import solidstack.template.TemplateException;
import solidstack.template.javascript.JavaScriptTemplate;


/**
 * Compiles the given parser events, directives and imports to a {@link JavaScriptTemplate}.
 *
 * @author René de Bloois
 */
public class ScriptEngineTemplateCompiler
{
	private ScriptEngine engine;

	public ScriptEngineTemplateCompiler( ScriptEngine engine )
	{
		this.engine = engine;
	}

	/**
	 * Compiles the script.
	 *
	 * @param context The compilation context.
	 */
	public void compileScript( TemplateCompilerContext context )
	{
		if( !( this.engine instanceof Compilable ) )
			throw new TemplateException( "Scriptengine does not implement Compilable: " + this.engine.getFactory().getEngineName() );

		Compilable compiler = (Compilable)this.engine;
		CompiledScript script;
		try
		{
			script = compiler.compile( new StringBuilderReader( context.getScript() ) );
		}
		catch( ScriptException e )
		{
			throw Java.throwUnchecked( e );
		}

		context.setTemplate( new ScriptEngineTemplate( script ) );
	}
}
