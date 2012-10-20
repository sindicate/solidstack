package solidstack.script;

import java.math.BigDecimal;

import solidstack.lang.Assert;


public class Identifier extends Expression
{
	private String name;

	public Identifier( String name )
	{
		this.name = name;
	}

	public String getName()
	{
		return this.name;
	}

	@Override
	public Object evaluate( Context context )
	{
		return new Link( context );
	}

	@Override
	public Object assign( Context context, Object value )
	{
		context.set( this.name, value );
		return value;
	}

	public class Link implements Value
	{
		private Context context;

		public Link( Context context )
		{
			this.context = context;
		}

		public Object get()
		{
			Object result = this.context.get( Identifier.this.name );
			if( result == null )
				return null;
			if( result instanceof Integer ) // TODO Get rid of this
				return new BigDecimal( (Integer)result );
			return result;
		}

		public void set( Object value )
		{
			if( value instanceof BigDecimal || value instanceof String )
			{
				this.context.set( Identifier.this.name, value );
				return;
			}
			Assert.fail( "Unexpected type " + value.getClass().getName() );
		}
	}
}
