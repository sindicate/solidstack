package solidstack.compiler;

import java.util.ArrayList;
import java.util.List;

public class ConstantPool
{
	List<Constant> constants = new ArrayList<Constant>();

	public <T extends Constant> T add( T constant )
	{
		int index = this.constants.indexOf( constant );
		if( index >= 0 )
			return (T)this.constants.get( index );
		this.constants.add( constant );
		constant.setIndex( this.constants.size() );
		return constant;
	}

	public int size()
	{
		return this.constants.size();
	}

	public List<Constant> constants()
	{
		return this.constants;
	}

	public ConstantUtf8 addUtf8( String value )
	{
		return add( new ConstantUtf8( value ) );
	}

	public ConstantClass addClass( ConstantUtf8 utf8 )
	{
		return add( new ConstantClass( utf8 ) );
	}

	public ConstantClass addClass( String name )
	{
		return addClass( addUtf8( name.replace( '.', '/' ) ) );
	}

	public ConstantNameAndType addNameAndType( String name, ConstantUtf8 type )
	{
		return add( new ConstantNameAndType( addUtf8( name ), type ) );
	}

	public ConstantFieldref addFieldref( ConstantClass classInfo, ConstantNameAndType nameAndType )
	{
		return add( new ConstantFieldref( classInfo, nameAndType ) );
	}

	public ConstantMethodref addMethodref( ConstantClass classInfo, ConstantNameAndType nameAndType )
	{
		return add( new ConstantMethodref( classInfo, nameAndType ) );
	}
}
