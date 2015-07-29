package solidstack.compiler;

import java.io.DataOutputStream;
import java.io.IOException;

abstract public class Statement
{
	abstract public void getByteCode( DataOutputStream out ) throws IOException;
	abstract public void collectConstants( ConstantPool pool );
}
