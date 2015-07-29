package solidstack.compiler;

import java.io.DataOutputStream;
import java.io.IOException;

public interface Statement
{
	void getByteCode( DataOutputStream out ) throws IOException;
	void collectConstants( ConstantPool pool );
}
