/*--
 * Copyright 2012 René M. de Bloois
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

package solidstack.io;


/**
 * A location in a resource.
 *
 * @author René de Bloois
 */
public class SourceLocation
{
	private Resource resource;
	private int lineNumber;
	private long column;


	// TODO Add column number
	static public SourceLocation forText( Resource resource, int lineNumber )
	{
		return new SourceLocation( resource, lineNumber, -1 );
	}

	static public SourceLocation forBinary( Resource resource, long column )
	{
		return new SourceLocation( resource, -1, column );
	}

	private SourceLocation( Resource resource, int lineNumber, long column )
	{
		this.resource = resource;
		this.lineNumber = lineNumber;
		this.column = column;
	}

	/**
	 * @return The resource.
	 */
	public Resource getResource()
	{
		return this.resource;
	}

	public boolean isTextLocation()
	{
		return this.lineNumber >= 0;
	}

	/**
	 * @return The line number.
	 */
	public int getLineNumber()
	{
		if( this.lineNumber < 0 )
			throw new UnsupportedOperationException( "lineNumber not supported for binary files" );
		return this.lineNumber;
	}

	/**
	 * @return A source location where the line number is decremented by one.
	 */
	public SourceLocation previousLine()
	{
		int l = getLineNumber();
		if( l <= 0 )
			throw new FatalIOException( "There is no previous line" );
		return SourceLocation.forText( this.resource, l - 1 );
	}

	/**
	 * @return A source location where the line number is incremented by one.
	 */
	public SourceLocation nextLine()
	{
		int l = getLineNumber();
		return SourceLocation.forText( this.resource, l + 1 );
	}

	/**
	 * @param lineNumber The line number.
	 * @return A source location with the line number overwritten.
	 */
	public SourceLocation lineNumber( int lineNumber )
	{
		return SourceLocation.forText( this.resource, lineNumber );
	}

	@Override
	public String toString()
	{
		StringBuilder result = new StringBuilder();
		if( this.lineNumber >= 0 )
		{
			result.append( "line " ).append( this.lineNumber );
			if( this.column >= 0 )
				result.append( ", char " ).append( this.column );
		}
		else
			result.append( "byte " ).append( this.column );
		if( this.resource != null )
			result.append( " of file " ).append( this.resource );
		return result.toString();
	}
}
