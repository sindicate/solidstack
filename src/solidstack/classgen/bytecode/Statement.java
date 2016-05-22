package solidstack.classgen.bytecode;

import solidstack.classgen.Bytes;

public interface Statement
{
	void toByteCode( Bytes bytes );
}
