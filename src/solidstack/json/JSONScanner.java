/*--
 * Copyright 2010 Ren� M. de Bloois
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

package solidstack.json;

import java.math.BigDecimal;

import solidstack.io.SourceException;
import solidstack.io.SourceLocation;
import solidstack.io.SourceReader;
import solidstack.json.JSONScanner.Token.TYPE;


/**
 * This is a tokenizer for CSV. It maintains the current line number, and it ignores whitespace.
 *
 * @author Ren� M. de Bloois
 */
// TODO Rename to scanner
public class JSONScanner
{
	/**
	 * The reader used to read from and push back characters.
	 */
	protected SourceReader in;

	private boolean emptyLineIsEOF;
	private boolean startOfLine;
	private boolean eof;

	/**
	 * Buffer for the result.
	 */
	protected StringBuilder result = new StringBuilder( 256 );


	/**
	 * Constructs a new instance of the Tokenizer.
	 *
	 * @param in The input.
	 */
	public JSONScanner( SourceReader in, boolean emptyLineIsEOF )
	{
		this.in = in;
		this.emptyLineIsEOF = emptyLineIsEOF;
		this.startOfLine = true;
	}

	/**
	 * Returns the next token from the input.
	 *
	 * @return A token from the input. Null if there are no more tokens available.
	 */
	public Token get()
	{
		if( this.eof )
			return Token.EOF;

		StringBuilder result = this.result;
		result.setLength( 0 );
		SourceReader in = this.in;

		while( true )
		{
			int ch = in.read();
			switch( ch )
			{
				case -1:
					this.eof = true;
					return Token.EOF;
				// Whitespace
				case ' ':
				case '\t':
				case '\r': // Does not occur
					continue;
				case '\n':
					if( this.emptyLineIsEOF )
					{
						if( this.startOfLine )
						{
							this.eof = true;
							return Token.EOF;
						}
						this.startOfLine = true;
					}
					continue;
			}
			this.startOfLine = false;
			switch( ch )
			{
				case ',':
					return Token.VALUE_SEPARATOR;
				case ':':
					return Token.NAME_SEPARATOR;
				case '[':
					return Token.BEGIN_ARRAY;
				case ']':
					return Token.END_ARRAY;
				case '{':
					return Token.BEGIN_OBJECT;
				case '}':
					return Token.END_OBJECT;
				case '"':
					while( true )
					{
						ch = in.read();
						if( ch == -1 )
							throw new SourceException( "Missing \"", in.getLocation() );
						if( ch == '"' )
							break;
						if( ch == '\\' )
						{
							ch = in.read();
							if( ch == -1 )
								throw new SourceException( "Incomplete escape sequence", in.getLocation() );
							switch( ch )
							{
								case 'b': ch = '\b'; break;
								case 'f': ch = '\f'; break;
								case 'n': ch = '\n'; break;
								case 'r': ch = '\r'; break;
								case 't': ch = '\t'; break;
								case '\"': break;
								case '\\': break;
								case 'u':
									char[] codePoint = new char[ 4 ];
									for( int i = 0; i < 4; i++ )
									{
										ch = in.read();
										codePoint[ i ] = (char)ch;
										if( !( ch >= '0' && ch <= '9' ) )
											throw new SourceException( "Illegal escape sequence: \\u" + String.valueOf( codePoint, 0, i + 1 ), in.getLocation() );
									}
									ch = Integer.valueOf( String.valueOf( codePoint ), 16 );
									break;
								default:
									throw new SourceException( "Illegal escape sequence: \\" + ( ch >= 0 ? (char)ch : "" ), in.getLocation() );
							}
						}
						result.append( (char)ch );
					}
					return new Token( TYPE.STRING, result.toString() );
				case '+':
				case '-':
					result.append( (char)ch );
					ch = in.read();
					if( !( ch >= '0' && ch <= '9' ) )
						throw new SourceException( "Invalid number", in.getLocation() );
					//$FALL-THROUGH$
				case '0': case '1': case '2': case '3': case '4':
				case '5': case '6': case '7': case '8': case '9':
					while( ch >= '0' && ch <= '9' )
					{
						result.append( (char)ch );
						ch = in.read();
					}
					if( ch == '.' )
					{
						result.append( (char)ch );
						ch = in.read();
						if( !( ch >= '0' && ch <= '9' ) )
							throw new SourceException( "Invalid number", in.getLocation() );
						while( ch >= '0' && ch <= '9' )
						{
							result.append( (char)ch );
							ch = in.read();
						}
					}
					if( ch == 'E' || ch == 'e' )
					{
						result.append( (char)ch );
						ch = in.read();
						if( ch == '+' || ch == '-' )
						{
							result.append( (char)ch );
							ch = in.read();
						}
						if( !( ch >= '0' && ch <= '9' ) )
							throw new SourceException( "Invalid number", in.getLocation() );
						while( ch >= '0' && ch <= '9' )
						{
							result.append( (char)ch );
							ch = in.read();
						}
					}
					in.rewind();
					return new Token( TYPE.NUMBER, new BigDecimal( result.toString() ) );
				case 'a': case 'b': case 'c': case 'd': case 'e':
				case 'f': case 'g': case 'h': case 'i': case 'j':
				case 'k': case 'l': case 'm': case 'n': case 'o':
				case 'p': case 'q': case 'r': case 's': case 't':
				case 'u': case 'v': case 'w': case 'x': case 'y':
				case 'z':
					while( ch >= 'a' && ch <= 'z' )
					{
						result.append( (char)ch );
						ch = in.read();
					}
					in.rewind();

					String keyword = result.toString();
					if( keyword.equals( "false" ) )
						return Token.FALSE;
					if( keyword.equals( "null" ) )
						return Token.NULL;
					if( keyword.equals( "true" ) )
						return Token.TRUE;

					throw new SourceException( "Unexpected keyword " + keyword, in.getLocation() );
				default:
					throw new SourceException( "Unexpected character '" + (char)ch + "'", in.getLocation() );
			}
		}
	}

	/**
	 * Returns the current line number.
	 *
	 * @return The current line number.
	 */
	public int getLineNumber()
	{
		return this.in.getLineNumber();
	}

	/**
	 * Returns the current file location.
	 *
	 * @return The current file location.
	 */
	public SourceLocation getLocation()
	{
		return this.in.getLocation();
	}

	/**
	 * Returns the underlying reader. But only if the back buffer is empty, otherwise an IllegalStateException is thrown.
	 *
	 * @return The underlying reader.
	 */
	public SourceReader getReader()
	{
		return this.in;
	}


	/**
	 * A CSV token.
	 *
	 * @author Ren� M. de Bloois
	 */
	// TODO Maybe we should remove this token class, and introduce the even mechanism like in JSONParser.
	static public class Token
	{
		static public enum TYPE { BEGIN_ARRAY, END_ARRAY, BEGIN_OBJECT, END_OBJECT, NAME_SEPARATOR, VALUE_SEPARATOR, STRING, NUMBER, BOOLEAN, NULL, EOF }

		static final protected Token BEGIN_ARRAY = new Token( TYPE.BEGIN_ARRAY );
		static final protected Token END_ARRAY = new Token( TYPE.END_ARRAY );
		static final protected Token BEGIN_OBJECT = new Token( TYPE.BEGIN_OBJECT );
		static final protected Token END_OBJECT = new Token( TYPE.END_OBJECT );
		static final protected Token NAME_SEPARATOR = new Token( TYPE.NAME_SEPARATOR );
		static final protected Token VALUE_SEPARATOR = new Token( TYPE.VALUE_SEPARATOR );

		static final protected Token FALSE = new Token( TYPE.BOOLEAN, Boolean.FALSE );
		static final protected Token NULL = new Token( TYPE.NULL );
		static final protected Token TRUE = new Token( TYPE.BOOLEAN, Boolean.TRUE );

		static final protected Token EOF = new Token( TYPE.EOF );

		/**
		 * The type of the token.
		 */
		private TYPE type;

		/**
		 * The value of the token.
		 */
		private Object value;

		/**
		 * Constructs a new token.
		 *
		 * @param type The type of the token.
		 */
		private Token( TYPE type )
		{
			this.type = type;
		}

		protected Token( TYPE type, Object value )
		{
			this.type = type;
			this.value = value;
		}

		public TYPE getType()
		{
			return this.type;
		}

		/**
		 * Returns the value of token.
		 *
		 * @return The value of token.
		 */
		public Object getValue()
		{
			if( this.type == TYPE.NULL )
				return null;
			if( this.value == null )
				throw new IllegalStateException( "Value is null" );
			return this.value;
		}

		@Override
		public String toString()
		{
			if( this.value != null )
				return this.value.toString();
			if( this.type == TYPE.EOF )
				return "EOF";
			return this.type.toString(); // TODO Is this correct?
		}
	}

	public void close()
	{
		this.in.close();
	}
}
