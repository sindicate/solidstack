package solidstack.compiler;

import java.io.DataOutputStream;
import java.io.IOException;

abstract public class Expression
{
	abstract public ConstantClass classInfo();
	abstract public void getByteCode( DataOutputStream out ) throws IOException;
}
