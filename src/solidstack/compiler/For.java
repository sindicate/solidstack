package solidstack.compiler;

public class For implements Statement
{
	private Statement initialization;
	private Conditional termination;
	private Statement increment;
	private Statements statements;


	public For( Statement initialization, Conditional termination, Statement increment, Statement... statements )
	{
		this.initialization = initialization;
		this.termination = termination;
		this.increment = increment;
		this.statements = new Statements( statements );
	}

	@Override
	public void getByteCode( Bytes bytes )
	{
		// Initialization
		this.initialization.getByteCode( bytes );

		// Conditional jump to below
		int continu = bytes.size();
		Jump jump = this.termination.getJump( bytes );

		// All the statements
		this.statements.getByteCode( bytes );

		// The iteration
		this.increment.getByteCode( bytes );

		// Jump to the 'continu' above
		int pc = bytes.size();
		bytes.writeByte( 0xA7 ); // goto
		bytes.writeShort( continu - pc );

		// Link the conditional jump above with the current position
		jump.link( bytes.size() );
	}
}
