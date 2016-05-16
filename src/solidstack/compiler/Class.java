package solidstack.compiler;

import java.util.ArrayList;
import java.util.List;

public class Class
{
	private ConstantClass info;
	private ConstantClass superInfo;
	private ConstantClass[] interfacesInfo;
	private List<Field> fields = new ArrayList<Field>();
	private List<Method> methods = new ArrayList<Method>();


	public Class( ConstantClass info )
	{
		this.info = info;
	}

	public void extend( ConstantClass info )
	{
		this.superInfo = info;
	}

	public void implement( ConstantClass... info )
	{
		this.interfacesInfo = info;
	}

	public Field addField( ConstantFieldref fieldref )
	{
		Field result = new Field( fieldref );
		this.fields.add( result );
		return result;
	}

	public Method addMethod( ClassBuilder classFile, ConstantMethodref methodref )
	{
		Method result = new Method( classFile, methodref );
		this.methods.add( result );
		return result;
	}

	public String name()
	{
		return this.info.name();
	}

	public void write( Bytes bytes )
	{
		if( this.superInfo == null )
			throw new CompilerException( "No super class set" );

		bytes.writeShort( 0x1001 ); // public & synthetic
		bytes.writeShort( this.info.index() ); // this_class

		// super_class
		bytes.writeShort( this.superInfo.index() ); // super_class

		// interfaces
		if( this.interfacesInfo == null )
			bytes.writeShort( 0 );
		else
		{
			bytes.writeShort( this.interfacesInfo.length ); // interfaces_count
			for( ConstantClass info : this.interfacesInfo )
				bytes.writeShort( info.index() );
		}

		// fields
		bytes.writeShort( this.fields.size() );
		for( Field field : this.fields )
			field.write( bytes );

		// methods
		bytes.writeShort( this.methods.size() );
		for( Method method : this.methods )
			method.write( bytes );
	}
}
