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

package solidstack.script;

import solidstack.io.SourceException;
import solidstack.io.SourceLocation;
import solidstack.io.SourceReader;
import solidstack.lang.SystemException;


/**
 * A tokenizer for a super string.
 *
 * @author René de Bloois
 */
/*
Scala:
SimpleExpr1  ::= ... | processedStringLiteral
processedStringLiteral
             ::= alphaid`"' {printableChar \ (`"' | `$') | escape} `"'
              |  alphaid `"""' {[`"'] [`"'] char \ (`"' | `$') | escape} {`"'} `"""'
escape       ::= `$$'
              |  `$' letter { letter | digit }
              |  `$'BlockExpr
alphaid      ::=  upper idrest
              |  varid
 */
// TODO Include 'raw' and 'f'
public class ProcessedStringScanner extends ScriptScanner
{
	private boolean first = true;
	private boolean found;
//	private boolean foundScriptlet;
	private boolean doubleQuoted;
	private boolean tripleQuoted;


	/**
	 * @param in The source reader.
	 */
	public ProcessedStringScanner( SourceReader in, boolean doubleQuoted )
	{
		super( in );
		this.doubleQuoted = doubleQuoted;
	}

	/**
	 * Read a string fragment. A fragment ends at a ${ or at the end of the string. After calling this method the method
	 * {@link #foundExpression()} indicates if an ${ expression was encountered while reading the last fragment.
	 *
	 * @return The fragment. Maybe empty but never null.
	 */
	// TODO No escaping allowed, no " allowed
	// TODO """ processed string
	public Fragment getFragment()
	{
		SourceReader in = getIn();
		int ch;

		if( this.first )
		{
			if( this.doubleQuoted )
			{
				if( ( ch = in.read() ) != '"' )
					throw new SystemException( "Unexpected char: " + ch );
				in.mark();
				boolean triple = in.read() == '"' && in.read() == '"';
				if( !triple )
					in.reset();
				else
					this.tripleQuoted = true;
			}
			this.first = false;
		}

		this.found = false;
		StringBuilder result = clearBuffer();
		SourceLocation location = in.getLocation();

		while( true )
		{
			switch( ch = in.read() )
			{
				case -1:
					if( this.doubleQuoted )
						throw new SourceException( "Missing '\"'", in.getLastLocation() );
					return new Fragment( location, result.toString() ); // end-of-input: we're done

				case '"':
					if( !this.tripleQuoted )
						return new Fragment( location, result.toString() ); // end-of-string: we're done
					in.mark();
					if( in.read() == '"' && in.read() == '"' )
					{
						while( in.read() == '"' )
							result.append( '"' );
						in.rewind();
						return new Fragment( location, result.toString() ); // end-of-string: we're done
					}
					in.reset();
					break;

				case '$':
					int ch2 = in.read();
					if( ch2 == '{' )
					{
						this.found = true;
						return new Fragment( location, result.toString() );
					}
					if( ch2 != '$' ) // TODO Or just \$ ?
						throw new SourceException( "$ must start an escape like ${...} or $$", in.getLocation() );
					break;

//				case '<':
//					ch2 = in.read();
//					if( ch2 == '%' )
//					{
//						this.foundScriptlet = true;
//						return new Fragment( location, result.toString() );
//					}
//					in.rewind();
//					break;

				// TODO This Tokenizer should not process escapes, that should be done in the specific implementations
				// TODO Escape <
				case '\\':
					switch( ch = in.read() )
					{
						case '\n': continue; // Skip newline
						case 'b': ch = '\b'; break;
						case 'f': ch = '\f'; break;
						case 'n': ch = '\n'; break;
						case 'r': ch = '\r'; break;
						case 't': ch = '\t'; break;
						case '"': // TODO And what if not double quoted?
//						case '\'':
						case '\\': break;
						case 'u': // TODO Actually, these escapes should be active through the entire script, like Java and Scala do. Maybe disabled by default. Or removed and optional for String literals.
							char[] codePoint = new char[ 4 ];
							for( int i = 0; i < 4; i++ )
							{
								codePoint[ i ] = Character.toUpperCase( (char)( ch = in.read() ) );
								if( !( ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' ) )
									throw new SourceException( "Illegal escape sequence: \\u" + new String( codePoint, 0, i + 1 ), in.getLastLocation() );
							}
							ch = Integer.valueOf( new String( codePoint ), 16 );
							break;
						default:
							throw new SourceException( "Illegal escape sequence: \\" + ( ch >= 0 ? (char)ch : "" ), in.getLastLocation() );
					}
					break;

			}
			result.append( (char)ch );
		}
	}

	/**
	 * @return True if a ${ expression was found while reading the last fragment.
	 */
	public boolean foundExpression()
	{
		return this.found;
	}

//	public boolean foundScriptlet()
//	{
//		return this.foundScriptlet;
//	}
//
//	public boolean foundLogic()
//	{
//		return this.found || this.foundScriptlet;
//	}

	/**
	 * A fragment.
	 */
	static public class Fragment
	{
		private SourceLocation location;
		private String value;

		Fragment( SourceLocation location, String value )
		{
			this.location = location;
			this.value = value;
		}

		/**
		 * @return The location of the token in the source.
		 */
		public SourceLocation getLocation()
		{
			return this.location;
		}

		/**
		 * @return The value of the token.
		 */
		public String getValue()
		{
			return this.value;
		}

		public int length()
		{
			return this.value.length();
		}
	}
}
