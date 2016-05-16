package solidstack.classgen.bytecode;

import solidstack.classgen.Bytes;

public interface Conditional extends Expression
{
	Jump getJump( Bytes bytes );
}
