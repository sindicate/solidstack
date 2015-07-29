package solidstack.compiler;

public class ExpressionBuilder
{
	private Method method;

	public ExpressionBuilder( Method method )
	{
		this.method = method;
	}

	public AccessLocal var( int i )
	{
		return new AccessLocal( this.method, i );
	}

	public AssignField setField( Expression instance, String name, String descriptor, Expression value )
	{
		return new AssignField( this.method.classBuilder(), instance, name, descriptor, value );
	}

	public void statement( Statement statement )
	{
		this.method.addStatement( statement );
	}

	public Statement callSuper( AccessLocal var, String name )
	{
		return new CallSuper( var, name );
	}

	public Expression field( Expression instance, String name, String descriptor )
	{
		return new AccessField( instance, name, descriptor );
	}

	public Expression initArray( String type, Expression... expression )
	{
		return new InitArray( type, expression );
	}

	public Expression call( Expression instance, String name, String descriptor, Expression... parameter )
	{
		return new CallMethod( instance, name, descriptor, parameter );
	}

	public Expression cast( Expression call, String type )
	{
		return new Cast( call, type );
	}
}
