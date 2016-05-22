package solidstack.classgen.bytecode;

import solidstack.classgen.Types.VMTYPE;
import solidstack.classgen.constants.CClass;
import solidstack.classgen.constants.CFieldref;
import solidstack.classgen.constants.CMethodref;


public class ExpressionBuilder
{
	public ExpressionBuilder()
	{
	}

	public AccessLocal local( int local, VMTYPE type )
	{
		return new AccessLocal( local, type );
	}

	public AssignLocal setLocal( int local, Expression value )
	{
		return new AssignLocal( local, value );
	}

	public AssignField setField( Expression instance, CFieldref field, Expression value )
	{
		return new AssignField( instance, field, value );
	}

	public Statement callSuper( AccessLocal var, CMethodref methodref )
	{
		return new CallSuper( var, methodref );
	}

	public Expression field( Expression instance, CFieldref fieldref )
	{
		return new AccessField( instance, fieldref );
	}

	public Expression initArray( CClass type, Expression... expression )
	{
		return new InitArray( type, expression );
	}

	public Expression call( Expression instance, CMethodref methodref, Expression... parameters )
	{
		return new CallMethod( instance, methodref, parameters );
	}

	public Expression callStatic( CMethodref methodref, Expression... parameters )
	{
		return new CallStaticMethod( methodref, parameters );
	}

	public Expression cast( Expression call, CClass type )
	{
		return new Cast( call, type );
	}

	public Expression literal( int value )
	{
		return new LiteralInt( value );
	}

	public Expression literal( CClass value )
	{
		return new LiteralClass( value );
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
