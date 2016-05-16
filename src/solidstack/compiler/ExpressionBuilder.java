package solidstack.compiler;

import solidstack.compiler.AccessLocal.TYPE;

public class ExpressionBuilder
{
	public ExpressionBuilder()
	{
	}

	public AccessLocal local( int local, TYPE type )
	{
		return new AccessLocal( local, type );
	}

	public AssignLocal setLocal( int local, Expression value )
	{
		return new AssignLocal( local, value );
	}

	public AssignField setField( Expression instance, ConstantFieldref field, Expression value )
	{
		return new AssignField( instance, field, value );
	}

	public Statement callSuper( AccessLocal var, ConstantMethodref methodref )
	{
		return new CallSuper( var, methodref );
	}

	public Expression field( Expression instance, ConstantFieldref fieldref )
	{
		return new AccessField( instance, fieldref );
	}

	public Expression initArray( ConstantClass type, Expression... expression )
	{
		return new InitArray( type, expression );
	}

	public Expression call( Expression instance, ConstantMethodref methodref, Expression... parameters )
	{
		return new CallMethod( instance, methodref, parameters );
	}

	public Expression cast( Expression call, ConstantClass type )
	{
		return new Cast( call, type );
	}

	public Expression literal( int value )
	{
		return new LiteralInt( value );
	}

	public Conditional lessThan( Expression left, Expression right )
	{
		return new LessThan( left, right );
	}

	public Expression plus( Expression left, Expression right )
	{
		return new Plus( left, right );
	}
}
