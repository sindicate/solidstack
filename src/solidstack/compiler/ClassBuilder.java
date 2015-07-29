package solidstack.compiler;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
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

	public Method addMethod( String name, Class<?> ret, Class<?>... parameters )
	{
		Method result = new Method( name, ret, parameters );
		this.methods.add( result );
		return result;
	}

	public byte[] compile()
	{
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream( bytes );
		try
		{
			out.writeInt( 0xCAFEBABE ); // magic
			out.writeShort( 0 ); // minor version
			out.writeShort( 49 ); // major version

			ConstantPool pool = collectConstants();
			out.writeShort( pool.size() + 1 );
			for( Constant constant : pool.constants() )
				constant.write( out );

			out.writeShort( 0x1001 ); // public & synthetic
			out.writeShort( this.classInfo.index() ); // this_class

			// super_class
			out.writeShort( this.superClassInfo.index() ); // super_class

			// interfaces
			if( this.interfacesInfo == null )
				out.writeShort( 0 );
			else
			{
				out.writeShort( this.interfaces.size() ); // interfaces_count
				for( ConstantClass info : this.interfacesInfo )
					out.writeShort( info.index() );
			}

			// fields
			out.writeShort( 0 );

			// methods
			out.writeShort( this.methods.size() );
			for( Method method : this.methods )
				method.write( out );

			// attributes
			out.writeShort( 0 );

			out.flush();
		}
		catch( IOException e )
		{
			throw new SystemException( e );
		}
		return bytes.toByteArray();
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

		// methods
		for( Method method : this.methods )
			method.collectConstants( pool );

		return pool;
	}
}
