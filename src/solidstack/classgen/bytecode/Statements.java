package solidstack.classgen.bytecode;

import solidstack.classgen.Bytes;

public class Statements implements Statement
{
	private Statement[] statements;


	public Statements( Statement... statements )
	{
		this.statements = statements;
	}

	@Override
	public void getByteCode( Bytes bytes )
	{
		for( Statement statement : this.statements )
			statement.getByteCode( bytes );
	}
}
