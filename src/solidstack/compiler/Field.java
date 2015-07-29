package solidstack.compiler;

import java.io.DataOutputStream;
import java.io.IOException;

public class Field
{
	private ClassBuilder cls;
	private String name;
	private Class<?> type;
	private boolean privat;

	private ConstantUtf8 nameInfo;
	private ConstantUtf8 descriptor;
	private ConstantFieldref fieldref;

	public Field( ClassBuilder cls, String name, java.lang.Class<?> type )
	{
		this.cls = cls;
		this.name = name;
		this.type = type;
	}

	public String name()
	{
		return this.name;
	}

	public ConstantFieldref fieldref()
	{
		return this.fieldref;
	}

	public void setPrivate( boolean privat )
	{
		this.privat = privat;
	}

	public void collectConstants( ConstantPool pool )
	{
		this.nameInfo = pool.add( new ConstantUtf8( this.name ) );
		this.descriptor = pool.add( new ConstantUtf8( Types.toFieldDescriptor( this.type ) ) );
		this.fieldref = pool.add( new ConstantFieldref( pool, this.cls.classInfo(), this.nameInfo, this.descriptor  ) );
	}

	public void write( DataOutputStream out ) throws IOException
	{
		out.writeShort( this.privat ? 0x1002 : 0x1001 ); // public & synthetic
		out.writeShort( this.nameInfo.index() ); // name_index
		out.writeShort( this.descriptor.index() ); // descriptor_index

		// attributes
		out.writeShort( 0 ); // attributes_count
	}
}
