package solidstack.javac;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;

// Inspired by: https://github.com/OpenHFT/Java-Runtime-Compiler
public class CustomFileManager extends ForwardingJavaFileManager<JavaFileManager>
{
	private final Map<String, ClassFile> classFiles = new LinkedHashMap<String, ClassFile>();

	protected CustomFileManager( JavaFileManager fileManager )
	{
		super( fileManager );
	}

//	@Override
//	public JavaFileObject getJavaFileForInput( Location location, String className, Kind kind ) throws IOException
//	{
//		if( location == StandardLocation.CLASS_OUTPUT && this.classFiles.containsKey( className ) && kind == Kind.CLASS )
//			return this.classFiles.get( className );
//		return this.fileManager.getJavaFileForInput( location, className, kind );
//	}

	@Override
	public JavaFileObject getJavaFileForOutput( Location location, String className, Kind kind, FileObject sibling )
	{
		ClassFile result = new ClassFile( className );
		this.classFiles.put( className, result );
		return result;
	}

	public Map<String, ClassFile> getClassFiles()
	{
		return this.classFiles;
	}
}
