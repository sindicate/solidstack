package solidstack.javac;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardLocation;

import solidstack.lang.Assert;
import solidstack.lang.SystemException;

public class CustomFileManager extends ForwardingJavaFileManager<JavaFileManager>
{
	private final Map<String, JavaFileObject> classFiles = new HashMap<String, JavaFileObject>();
	private final List<ClassFile> newClasses = new ArrayList<ClassFile>();

	private Map<URI, ZipFile> openZipFiles = new HashMap<URI, ZipFile>();

	protected CustomFileManager( JavaFileManager fileManager )
	{
		super( fileManager );
	}

	public void start()
	{
		this.newClasses.clear();
	}

	public List<ClassFile> getCompiledClasses()
	{
		return this.newClasses;
	}

	@Override
	public JavaFileObject getJavaFileForOutput( Location location, String className, Kind kind, FileObject sibling )
	{
		ClassFile result = new ClassFile( className );
		this.classFiles.put( className, result );
		this.newClasses.add( result );
//		log.debug( "getJavaFileForOutput {} {} {} {} -> {}", location, className, kind, sibling, result );
		return result;
	}

	@Override
	public Iterable<JavaFileObject> list( Location location, String packageName, Set<Kind> kinds, boolean recurse ) throws IOException
	{
		CompilerClassLoader.log.debug( "list {} {} {} {}", location, packageName, kinds, recurse );
		Assert.isFalse( recurse );

		if( location == StandardLocation.PLATFORM_CLASS_PATH )
			return super.list( location, packageName, kinds, recurse );

		if( location == StandardLocation.CLASS_PATH )
			return listClassPath( packageName, kinds );

		throw new UnsupportedOperationException( "Location not supported: " + location );
	}

	private Iterable<JavaFileObject> listClassPath( String packageName, Set<Kind> kinds ) throws IOException
	{
		Map<String, JavaFileObject> result = new HashMap<String, JavaFileObject>();

		// Previously compiled classes
		if( kinds.contains( Kind.CLASS ) )
		{
			String prefix = packageName + ".";
			int prefixLen = prefix.length();
			for( Entry<String, JavaFileObject> entry : this.classFiles.entrySet() )
			{
				String name = entry.getKey();
				if( name.startsWith( prefix ) && name.substring( prefixLen ).indexOf( '.' ) < 0)
					result.put( name, entry.getValue() );
			}
		}

		// Classes from the classpath
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		String resource = packageName.replace( '.', '/' );

		Enumeration<URL> urls = cl.getResources( resource );
		while( urls.hasMoreElements() ) // In the order of the classpath
		{
			URL url = urls.nextElement();
			CompilerClassLoader.log.debug( "Found folder: {}", url );

			if( url.getProtocol().equals( "file" ) )
			{
				File folder = toFile( url );
				if( folder.isDirectory() )
					listFolder( folder.listFiles(), packageName, result );
			}
			else if( url.getProtocol().equals( "jar" ) )
				listJarFolder( url, packageName, resource, result );
			else
				throw new UnsupportedOperationException( "Unsupported protocol: " + url.getProtocol() );
		}
		return result.values();
	}

	private void listFolder( File[] files, String packageName, Map<String, JavaFileObject> result ) throws IOException
	{
		// TODO Also Java files?
		if( files != null )
			for( File file : files )
				if( file.getName().endsWith( ".class" ) )
				{
					String name = file.getName();
					name = packageName + "." + name.substring( 0, name.length() - 6 );
					if( !result.containsKey( name ) )
					{
						JavaFileObject object = new FileJavaFileObject( name, Kind.CLASS, file );
//						log.debug( "Adding: {}", object );
						result.put( name, object );
					}
				}
	}

	private void listJarFolder( URL url, String packageName, String resource, Map<String, JavaFileObject> result ) throws IOException
	{
		// TODO Jar in jar?
		String path = url.toString();
		int exclamation = path.indexOf( '!' );
		if( exclamation < 0 )
			throw new IllegalArgumentException( "Expecting an ! in the jar url" );
		path = path.substring( 4, exclamation );

		URI uri = URI.create( path );
		if( uri.getScheme() == null || uri.getScheme().length() == 1 )
			uri = URI.create( "file:///" + path ); // TODO Hostname?

		if( "file".equals( uri.getScheme() ) )
		{
			// Look in zipfile cache
			ZipFile zip = this.openZipFiles.get( uri );
			if( zip == null )
			{
				zip = new ZipFile( new File( uri ) );
				this.openZipFiles.put( uri, zip );
			}

			Enumeration<? extends ZipEntry> entries = zip.entries();
			String prefix = resource + "/";
			int prefixLen = prefix.length();
			while( entries.hasMoreElements() )
			{
				ZipEntry entry = entries.nextElement();
				if( !entry.isDirectory() )
					if( entry.getName().endsWith( ".class" ) )
						if( entry.getName().startsWith( prefix ) && entry.getName().substring( prefixLen ).indexOf( '/' ) < 0 )
						{
							String name = entry.getName().replace( '/', '.' );
							name = name.substring( 0, name.length() - 6 );
							if( !result.containsKey( name ) )
							{
								JavaFileObject object = new InputStreamJavaFileObject( name, Kind.CLASS, zip.getInputStream( entry ) );
//								log.debug( "Adding: {}", object );
								result.put( name, object );
							}
						}
			}
		}
	}

	private File toFile(URL url)
	{
		try
		{
			return new File( url.toURI() );
		}
		catch( URISyntaxException e )
		{
			throw new SystemException( e );
		}
	}

	@Override
	public String inferBinaryName( Location location, JavaFileObject file )
	{
		String result;
		if( file instanceof AbstractJavaFileObject )
			result = ( (AbstractJavaFileObject)file ).getName();
		else
			result = super.inferBinaryName( location, file );
//		log.debug( "inferBinaryName {} {} -> {}", location, file, result );
		return result;
	}

	@Override
	public ClassLoader getClassLoader( Location location )
	{
		ClassLoader result = super.getClassLoader( location );
//		log.debug( "getClassLoader {} -> {}", location, result );
		return result;
	}

	@Override
	public boolean isSameFile( FileObject a, FileObject b )
	{
		boolean result = super.isSameFile( a, b );
//		log.debug( "isSameFile {} {} -> {}", a, b, result );
		return result;
	}

	@Override
	public boolean handleOption( String current, Iterator<String> remaining )
	{
		boolean result = super.handleOption( current, remaining );
//		log.debug( "handleOption {} {} -> {}", current, remaining, result );
		return result;
	}

	@Override
	public boolean hasLocation( Location location )
	{
		boolean result = super.hasLocation( location );
//		log.debug( "hasLocation {} -> {}", location, result );
		return result;
	}

	@Override
	public int isSupportedOption( String option )
	{
		int result = super.isSupportedOption( option );
		CompilerClassLoader.log.debug( "isSupportedOption {} -> {}", option, result );
		return result;
	}

	@Override
	public JavaFileObject getJavaFileForInput( Location location, String className, Kind kind ) throws IOException
	{
		JavaFileObject result = super.getJavaFileForInput( location, className, kind );
		CompilerClassLoader.log.debug( "getJavaFileForInput {} {} {} -> {}", location, className, kind, result );
		return result;
	}

	@Override
	public FileObject getFileForInput( Location location, String packageName, String relativeName )
			throws IOException
	{
		FileObject result = super.getFileForInput( location, packageName, relativeName );
		CompilerClassLoader.log.debug( "getFileForInput {} {} {} -> {}", location, packageName, relativeName, result );
		return result;
	}

	@Override
	public FileObject getFileForOutput( Location location, String packageName, String relativeName,
			FileObject sibling ) throws IOException
	{
		FileObject result = super.getFileForOutput( location, packageName, relativeName, sibling );
		CompilerClassLoader.log.debug( "getFileForOutput {} {} {} {} -> {}", location, packageName, relativeName, sibling, result );
		return result;
	}

	@Override
	public void flush() throws IOException
	{
//		log.debug( "flush" );
		super.flush();
	}

	@Override
	public void close() throws IOException
	{
		super.close();
		for( ZipFile zip : this.openZipFiles.values() )
			try
			{
				zip.close();
			}
			catch( IOException e )
			{
				throw new SystemException( e );
			}
		this.openZipFiles.clear();
	}
}
