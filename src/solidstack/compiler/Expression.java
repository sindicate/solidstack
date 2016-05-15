package solidstack.compiler;

public interface Expression extends Statement
{
	ConstantClass classInfo();
	String getFieldDescriptor();
}
