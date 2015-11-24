package solidstack.template.java;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;



public class MyJavaFileManager implements JavaFileManager
{
	private ClassLoader loader;


	public MyJavaFileManager( ClassLoader loader )
	{
		this.loader = loader;
	}

	@Override
	public int isSupportedOption( String option )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public ClassLoader getClassLoader( Location location )
	{
		if( location.getName().equals( "ANNOTATION_PROCESSOR_PATH" ) )
			return null;
		if( location.getName().equals( "CLASS_PATH" ) )
			return this.loader;
		throw new UnsupportedOperationException( location.toString() );
	}

	@Override
	public Iterable<JavaFileObject> list( Location location, String packageName, Set<Kind> kinds, boolean recurse ) throws IOException
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String inferBinaryName( Location location, JavaFileObject file )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isSameFile( FileObject a, FileObject b )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean handleOption( String current, Iterator<String> remaining )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasLocation( Location location )
	{
		if( location.getName().equals( "ANNOTATION_PROCESSOR_PATH" ) )
			return false;
		throw new UnsupportedOperationException( location.toString() );
	}

	@Override
	public JavaFileObject getJavaFileForInput( Location location, String className, Kind kind ) throws IOException
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public JavaFileObject getJavaFileForOutput( Location location, String className, Kind kind, FileObject sibling ) throws IOException
	{
		System.err.println( "***** getJavaFileForOutput" );
		throw new UnsupportedOperationException();
	}

	@Override
	public FileObject getFileForInput( Location location, String packageName, String relativeName ) throws IOException
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public FileObject getFileForOutput( Location location, String packageName, String relativeName, FileObject sibling ) throws IOException
	{
		System.err.println( "***** getFileForOutput" );
		throw new UnsupportedOperationException();
	}

	@Override
	public void flush() throws IOException
	{
		// Nothing to flush
	}

	@Override
	public void close() throws IOException
	{
		throw new UnsupportedOperationException();
	}
}
