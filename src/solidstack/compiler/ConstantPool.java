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
}
