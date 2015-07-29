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

	public AssignField setField( Expression instance, String name, Expression value )
	{
		return new AssignField( this.method.classBuilder(), instance, name, value );
	}

	public void statement( Statement statement )
	{
		this.method.addStatement( statement );
	}
}
