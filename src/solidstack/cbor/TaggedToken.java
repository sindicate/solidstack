package solidstack.cbor;

import solidstack.script.java.DefaultClassExtensions;

public class TaggedToken extends SimpleToken
{
	Tag[] tags;


	TaggedToken( TYPE type )
	{
		super( type );
	}

	@Override
	public boolean hasTag( long value )
	{
		return getTag( value ) != null;
	}

	@Override
	public Tag getTag( long value )
	{
		for( Tag tag : this.tags )
			if( tag.value == value )
				return tag;
		return null;
	}

	@Override
	public String toString()
	{
		if( this.tags == null )
			return super.toString();

		StringBuilder result = new StringBuilder();
		result.append( DefaultClassExtensions.mkString( this.tags, " " ) );
		result.append( ' ' ).append( super.toString() );
		return result.toString();
	}
}
