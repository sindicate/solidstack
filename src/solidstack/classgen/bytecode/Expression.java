package solidstack.classgen.bytecode;

import solidstack.classgen.constants.CClass;

public interface Expression extends Statement
{
	CClass classInfo();
	String getFieldDescriptor();
}
