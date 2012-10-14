package solidstack.script;

import java.util.ArrayList;
import java.util.List;

public class Expressions extends Expression
{
	private List<Expression> expressions = new ArrayList<Expression>();

	public Expressions()
	{
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object evaluate( Context context )
	{
		Object result = null;
		for( Expression expression : this.expressions )
			result = expression.evaluate( context );
		return result;
	}

	public void append( Expression expression )
	{
		this.expressions.add( expression );
	}
}
