/*--
 * Copyright 2010 René M. de Bloois
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package solidstack.template.java;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.GlobPatternMapper;
import org.apache.tools.ant.util.SourceFileScanner;


public class JavaTemplateCompilerTask extends MatchingTask
{
	private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();

	private Path src;
	private File destDir;
	private File[] compileList = new File[ 0 ];

	/**
     * Javac task for compilation of Java files.
     */
    public JavaTemplateCompilerTask() {
    }

	/**
	 * Adds a path for source compilation.
	 *
	 * @return a nested src element.
	 */
	public Path createSrc()
	{
		if( this.src == null )
			this.src = new Path( getProject() );
		return this.src.createPath();
	}

	/**
	 * Recreate src.
	 *
	 * @return a nested src element.
	 */
	protected Path recreateSrc()
	{
		this.src = null;
		return createSrc();
	}

	/**
	 * Set the source directories to find the source Java files.
	 *
	 * @param srcDir the source directories as a path
	 */
	public void setSrcdir( final Path srcDir )
	{
		if( this.src == null )
			this.src = srcDir;
		else
			this.src.append( srcDir );
	}

	/**
	 * Gets the source dirs to find the source java files.
	 *
	 * @return the source directories as a path
	 */
	public Path getSrcdir()
	{
		return this.src;
	}

	/**
	 * Set the destination directory into which the Java source files should be compiled.
	 *
	 * @param destDir the destination director
	 */
	public void setDestdir( final File destDir )
	{
		this.destDir = destDir;
	}

	/**
	 * Gets the destination directory into which the java source files should be compiled.
	 *
	 * @return the destination directory
	 */
	public File getDestdir()
	{
		return this.destDir;
	}

	/**
	 * Executes the task.
	 *
	 * @exception BuildException if an error occurs
	 */
	@Override
	public void execute() throws BuildException
	{
		checkParameters();
		resetFileLists();

		// scan source directories and dest directory to build up
		// compile lists
		final String[] list = this.src.list();
		for( int i = 0; i < list.length; i++ )
		{
			final File srcDir = getProject().resolveFile( list[ i ] );
			if( !srcDir.exists() )
			{
				throw new BuildException( "srcdir \"" + srcDir.getPath() + "\" does not exist!", getLocation() );
			}

			final DirectoryScanner ds = getDirectoryScanner( srcDir );
			final String[] files = ds.getIncludedFiles();

			scanDir( srcDir, this.destDir != null ? this.destDir : srcDir, files );
		}

//        compile();
//		for( File file : this.compileList )
//			log( file.getAbsolutePath() );
	}

	/**
	 * Clear the list of files to be compiled and copied..
	 */
	protected void resetFileLists()
	{
		this.compileList = new File[ 0 ];
	}

	/**
	 * Scans the directory looking for source files to be compiled. The results are returned in the class variable
	 * compileList
	 *
	 * @param srcDir The source directory
	 * @param destDir The destination directory
	 * @param files An array of filenames
	 */
	protected void scanDir( final File srcDir, final File destDir, final String[] files )
	{
		final GlobPatternMapper m = new GlobPatternMapper();
		final String[] extensions = findSupportedFileExtensions();

		for( int i = 0; i < extensions.length; i++ )
		{
			m.setFrom( extensions[ i ] );
			m.setTo( "*.java" );
			final SourceFileScanner sfs = new SourceFileScanner( this );
			final File[] newFiles = sfs.restrictAsFiles( files, srcDir, destDir, m );

			if( newFiles.length > 0 )
			{
				final File[] newCompileList = new File[ this.compileList.length + newFiles.length ];
				System.arraycopy( this.compileList, 0, newCompileList, 0, this.compileList.length );
				System.arraycopy( newFiles, 0, newCompileList, this.compileList.length, newFiles.length );
				this.compileList = newCompileList;
			}
		}
	}

	private String[] findSupportedFileExtensions()
	{
		return new String[] { "*.slt" };
	}

	/**
	 * Gets the list of files to be compiled.
	 *
	 * @return the list of files as an array
	 */
	public File[] getFileList()
	{
		return this.compileList;
	}

	/**
	 * Check that all required attributes have been set and nothing silly has been entered.
	 *
	 * @since Ant 1.5
	 * @exception BuildException if an error occurs
	 */
	protected void checkParameters() throws BuildException
	{
		if( this.src == null )
			throw new BuildException( "srcdir attribute must be set!", getLocation() );
		if( this.src.size() == 0 )
			throw new BuildException( "srcdir attribute must be set!", getLocation() );

		if( this.destDir != null && !this.destDir.isDirectory() )
			throw new BuildException( "destination directory \"" + this.destDir + "\" does not exist " + "or is not a directory", getLocation() );
	}
}
