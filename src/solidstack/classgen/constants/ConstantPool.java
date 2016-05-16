package solidstack.classgen.constants;

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

	public CUtf8 addUtf8( String value )
	{
		return add( new CUtf8( value ) );
	}

	public CClass addClass( CUtf8 utf8 )
	{
		return add( new CClass( utf8 ) );
	}

	public CClass addClass( String name )
	{
		return addClass( addUtf8( name.replace( '.', '/' ) ) );
	}

	public CNameAndType addNameAndType( String name, CUtf8 type )
	{
		return add( new CNameAndType( addUtf8( name ), type ) );
	}

	public CFieldref addFieldref( CClass classInfo, CNameAndType nameAndType )
	{
		return add( new CFieldref( classInfo, nameAndType ) );
	}

	public CMethodref addMethodref( CClass classInfo, CNameAndType nameAndType )
	{
		return add( new CMethodref( classInfo, nameAndType ) );
	}
}
