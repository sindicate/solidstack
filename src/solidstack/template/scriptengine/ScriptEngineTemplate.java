package solidstack.template.scriptengine;

import java.util.Map;

import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import javax.script.SimpleScriptContext;

import solidstack.template.EncodingWriter;
import solidstack.template.Template;
import solidstack.template.TemplateException;

public class ScriptEngineTemplate extends Template
{
	private CompiledScript script;

	public ScriptEngineTemplate( CompiledScript script )
	{
		this.script = script;
	}

	@Override
	public void apply( Object params, EncodingWriter writer )
	{
		if( params instanceof Map )
		{
			System.out.println( "Map implementation: " + params.getClass().getName() );
			Map<String, Object> p = (Map<String, Object>)params;
			ScriptEngineConvertingWriter out = new ScriptEngineConvertingWriter( writer );
			SimpleScriptContext context = new SimpleScriptContext();
			context.setBindings( new SimpleBindings( p ), ScriptContext.ENGINE_SCOPE );
			context.setWriter( out );
			try
			{
				this.script.eval( context );
				return;
			}
			catch( ScriptException e )
			{
				throw new TemplateException( e );
			}
		}

		throw new UnsupportedOperationException( "params not supported: " + params.getClass().getName() );
	}
}
