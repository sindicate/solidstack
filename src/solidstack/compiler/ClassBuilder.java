package solidstack.compiler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import solidstack.lang.Assert;
import solidstack.lang.SystemException;

public class ClassBuilder
{
	private String name;
	private Class<?> superClass;
	private List<Class<?>> interfaces;
	private List<Method> methods = new ArrayList<Method>();
	private List<Field> fields = new ArrayList<Field>();

	private ConstantClass classInfo;
	private ConstantClass superClassInfo;
	private List<ConstantClass> interfacesInfo;

	public ClassBuilder( String name, Class<?>... extend )
	{
		this.name = name;
		for( Class<?> cls : extend )
		{
			if( cls.isInterface() )
			{
				if( this.interfaces == null )
					this.interfaces = new ArrayList<Class<?>>();
				this.interfaces.add( cls );
			}
			else
			{
				// TODO Check other types of Class
				Assert.isNull( this.superClass );
				this.superClass = cls;
			}
		}
	}

	public String name()
	{
		return this.name;
	}

	public ConstantClass classInfo()
	{
		Assert.notNull( this.classInfo );
		return this.classInfo;
	}

	public ConstantFieldref fieldref( String name )
	{
		for( Field field : this.fields )
		{
			if( field.name().equals( name ) )
				return field.fieldref();
		}
		Assert.fail();
		return null;
	}

	public Method addMethod( String name, Class<?> ret, Class<?>... parameters )
	{
		Method result = new Method( this, name, ret, parameters );
		this.methods.add( result );
		return result;
	}

	public Method addConstructor( Class<?>... parameters )
	{
		return addMethod( "<init>", null, parameters );
	}

	public Field addField( String name, Class<?> type )
	{
		Field result = new Field( this, name, type );
		this.fields.add( result );
		return result;
	}

	public Bytes compile()
	{
		Bytes bytes = new Bytes();
		try
		{
			bytes.writeInt( 0xCAFEBABE ); // magic
			bytes.writeShort( 0 ); // minor version
			bytes.writeShort( 49 ); // major version

			ConstantPool pool = collectConstants();
			bytes.writeShort( pool.size() + 1 );
			for( Constant constant : pool.constants() )
				constant.write( bytes );

			bytes.writeShort( 0x1001 ); // public & synthetic
			bytes.writeShort( this.classInfo.index() ); // this_class

			// super_class
			bytes.writeShort( this.superClassInfo.index() ); // super_class

			// interfaces
			if( this.interfacesInfo == null )
				bytes.writeShort( 0 );
			else
			{
				bytes.writeShort( this.interfaces.size() ); // interfaces_count
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

			// attributes
			bytes.writeShort( 0 );
		}
		catch( IOException e )
		{
			throw new SystemException( e );
		}
		return bytes;
	}

	private ConstantPool collectConstants()
	{
		ConstantPool pool = new ConstantPool();

		// this_class
		this.classInfo = pool.add( new ConstantClass( pool, this.name ) );

		// super_class
		// According to the Java 8 spec, this may be null or 0
		this.superClassInfo = pool.add( new ConstantClass( pool, this.superClass != null ? this.superClass.getName() : "java.lang.Object" ) );

		// interfaces
		if( this.interfaces != null )
		{
			this.interfacesInfo = new ArrayList<ConstantClass>();
			for( Class<?> interfac : this.interfaces )
				this.interfacesInfo.add( pool.add( new ConstantClass( pool, interfac.getName() ) ) );
		}

		// fields
		for( Field field : this.fields )
			field.collectConstants( pool );

		// methods
		for( Method method : this.methods )
			method.collectConstants( pool );

		return pool;
	}
}
