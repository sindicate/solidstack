package solidstack.template.javascript;

import org.mozilla.javascript.Scriptable;

import funny.Symbol;
import solidstack.script.scopes.Scope;
import solidstack.script.scopes.UndefinedException;

public class ScriptableScope implements Scriptable
{
	private Scriptable parent;
	private Scope scope;

	public ScriptableScope( Scriptable parent, Scope scope )
	{
		this.parent = parent;
		this.scope = scope;
	}

	@Override
	public void delete( String arg0 )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void delete( int arg0 )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Object get( String name, Scriptable start )
	{
		try
		{
			return this.scope.get( Symbol.apply( name ) );
		}
		catch( UndefinedException e )
		{
			return this.parent.get( name, start );
		}
	}

	@Override
	public Object get( int arg0, Scriptable arg1 )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getClassName()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Object getDefaultValue( Class<?> arg0 )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Object[] getIds()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Scriptable getParentScope()
	{
		return this.parent;
	}

	@Override
	public Scriptable getPrototype()
	{
		return null;
	}

	@Override
	public boolean has( String arg0, Scriptable arg1 )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean has( int arg0, Scriptable arg1 )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasInstance( Scriptable arg0 )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void put( String arg0, Scriptable arg1, Object arg2 )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void put( int arg0, Scriptable arg1, Object arg2 )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void setParentScope( Scriptable arg0 )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void setPrototype( Scriptable arg0 )
	{
		throw new UnsupportedOperationException();
	}
}
