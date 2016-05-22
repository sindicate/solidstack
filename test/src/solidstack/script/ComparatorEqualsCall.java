package solidstack.script;

import java.util.Comparator;

public class ComparatorEqualsCall
{
	public void sort( Comparator comparator )
	{
		comparator.equals( new Object() );
	}
}
