package solidstack.classgen.bytecode;

import solidstack.classgen.Types.VMTYPE;

public interface Expression extends Statement
{
	VMTYPE vmType();
}
