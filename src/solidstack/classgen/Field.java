package solidstack.classgen;

import solidstack.classgen.constants.CFieldref;

public class Field
{
	private boolean privat;
	private CFieldref fieldref;

	public Field( CFieldref fieldref )
	{
		this.fieldref = fieldref;
	}

	public String name()
	{
		return this.fieldref.name();
	}

	public CFieldref fieldref()
	{
		return this.fieldref;
	}

	public void setPrivate( boolean privat )
	{
		this.privat = privat;
	}

	public void write( Bytes bytes )
	{
		bytes.writeShort( this.privat ? 0x1002 : 0x1001 ); // public & synthetic
		bytes.writeShort( this.fieldref.nameIndex() ); // name_index
		bytes.writeShort( this.fieldref.typeIndex() ); // descriptor_index

		// attributes
		bytes.writeShort( 0 ); // attributes_count
	}
}
