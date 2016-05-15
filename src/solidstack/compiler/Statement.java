package solidstack.compiler;

public interface Statement
{
	void getByteCode( Bytes bytes );
	void collectConstants( ConstantPool pool );
}
