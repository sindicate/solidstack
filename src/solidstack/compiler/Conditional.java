package solidstack.compiler;

public interface Conditional extends Expression
{
	Jump getJump( Bytes bytes );
}
