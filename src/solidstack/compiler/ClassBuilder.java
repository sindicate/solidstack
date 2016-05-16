package solidstack.compiler;

import java.io.IOException;

import solidstack.lang.SystemException;

public class ClassBuilder
{
	private ConstantPool pool = new ConstantPool();
	private Class cls;


	public ClassBuilder()
	{
	}

	public ConstantUtf8 addConstantUtf8( String value )
	{
		return this.pool.addUtf8( value );
	}

	public ConstantClass addConstantClass( String name )
	{
		return this.pool.addClass( name );
	}

	public ConstantUtf8 addFieldType( java.lang.Class<?> type )
	{
		return this.pool.addUtf8( Types.toFieldDescriptor( type ) );
	}

	public ConstantUtf8 addMethodType( java.lang.Class<?> result, java.lang.Class<?>... args )
	{
		return this.pool.addUtf8( Types.toMethodDescriptor( result, args ) );
	}

	public ConstantFieldref addConstantFieldref( ConstantClass classInfo, String name, ConstantUtf8 type )
	{
		return this.pool.addFieldref( classInfo, this.pool.addNameAndType( name, type ) );
	}

	public ConstantMethodref addConstantMethodref( ConstantClass classInfo, String name, ConstantUtf8 type )
	{
		return this.pool.addMethodref( classInfo, this.pool.addNameAndType( name, type ) );
	}

	public Class addClass( ConstantClass name )
	{
		return this.cls = new Class( name );
	}

//	public ClassBuilder( String name, Class<?>... extend )
//	{
//		this.name = name;
//		for( Class<?> cls : extend )
//		{
//			if( cls.isInterface() )
//			{
//				if( this.interfaces == null )
//					this.interfaces = new ArrayList<Class<?>>();
//				this.interfaces.add( cls );
//			}
//			else
//			{
//				// TODO Check other types of Class
//				Assert.isNull( this.superClass );
//				this.superClass = cls;
//			}
//		}
//	}

//	public String name()
//	{
//		return this.name;
//	}

//	public ConstantClass classInfo()
//	{
//		Assert.notNull( this.classInfo );
//		return this.classInfo;
//	}

//	public ConstantFieldref fieldref( String name )
//	{
//		for( Field field : this.fields )
//		{
//			if( field.name().equals( name ) )
//				return field.fieldref();
//		}
//		Assert.fail();
//		return null;
//	}

	public Bytes compile()
	{
		if( this.cls == null )
			throw new CompilerException( "No class set" );

		Bytes bytes = new Bytes();
		try
		{
			bytes.writeInt( 0xCAFEBABE ); // magic
			bytes.writeShort( 0 ); // minor version
			bytes.writeShort( 49 ); // major version

			this.pool.addUtf8( "Code" ); // TODO Attribute names only if needed

			bytes.writeShort( this.pool.size() + 1 );
			for( Constant constant : this.pool.constants() )
				constant.write( bytes );

			this.cls.write( bytes );

			// attributes
			bytes.writeShort( 0 );
		}
		catch( IOException e )
		{
			throw new SystemException( e );
		}
		return bytes;
	}

//	private ConstantPool collectConstants()
//	{
//		ConstantPool pool = new ConstantPool();
//
//		// this_class
//		this.classInfo = pool.add( new ConstantClass( pool, this.name ) );
//
//		// super_class
//		// According to the Java 8 spec, this may be null or 0
//		this.superClassInfo = pool.add( new ConstantClass( pool, this.superClass != null ? this.superClass.getName() : "java.lang.Object" ) );
//
//		// interfaces
//		if( this.interfaces != null )
//		{
//			this.interfacesInfo = new ArrayList<ConstantClass>();
//			for( Class<?> interfac : this.interfaces )
//				this.interfacesInfo.add( pool.add( new ConstantClass( pool, interfac.getName() ) ) );
//		}
//
//		// fields
//		for( Field field : this.fields )
//			field.collectConstants( pool );
//
//		// methods
//		for( Method method : this.methods )
//			method.collectConstants( pool );
//
//		return pool;
//	}
}
