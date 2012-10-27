/*--
 * Copyright 2012 Ren� M. de Bloois
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

import solidstack.io.PushbackReader;
import solidstack.io.SourceException;
import solidstack.io.SourceLocation;
import solidstack.io.SourceReader;
import solidstack.lang.Assert;
import solidstack.script.ScriptTokenizer.Token.TYPE;


/**
 * This is a tokenizer for CSV. It maintains the current line number, and it ignores whitespace.
 *
 * @author Ren� M. de Bloois
 */
public class ScriptTokenizer
{
	/**
	 * The reader used to read from and push back characters.
	 */
	private PushbackReader in;

	/**
	 * Buffer for the result.
	 */
	private StringBuilder buffer = new StringBuilder( 256 );

	/**
	 * The last token read.
	 */
	private Token last;

	private int lastLineNumber;

	/**
	 * The last token is pushed back.
	 */
	private boolean pushed;


	/**
	 * Constructs a new instance of the Tokenizer.
	 *
	 * @param in The input.
	 */
	public ScriptTokenizer( SourceReader in )
	{
		this.in = new PushbackReader( in );
	}

	/**
	 * @return The underlying reader.
	 */
	public PushbackReader getIn()
	{
		return this.in;
	}

	/**
	 * Clears the buffer and returns it.
	 *
	 * @return The cleared buffer.
	 */
	public StringBuilder clearBuffer()
	{
		StringBuilder buffer = this.buffer;
		buffer.setLength( 0 );
		return buffer;
	}

	/**
	 * Returns the next token from the input.
	 *
	 * @return A token from the input. Null if there are no more tokens available.
	 */
	public Token get()
	{
		if( this.pushed )
		{
			this.pushed = false;
			return lastToken();
		}
		return this.last = get0();
	}

	/**
	 * @return The last token read.
	 */
	public Token lastToken()
	{
		if( this.pushed )
			throw new IllegalStateException( "Token has been pushed back" );
		if( this.last == null )
			throw new IllegalStateException( "There is no last token" );
		return this.last;
	}

	/**
	 * Push the token back.
	 */
	public void push()
	{
		if( this.pushed )
			throw new IllegalStateException( "Token has already been pushed back" );
		this.pushed = true;
	}

	private Token get0()
	{
		StringBuilder result = clearBuffer();
		PushbackReader in = getIn();

		int ch;
		ws: while( true )
		{
			ch = in.read();
			switch( ch )
			{
				case -1:
					this.lastLineNumber = in.getLineNumber();
					return Token.TOK_EOF;

				default:
					break ws;

				// Whitespace
				case ' ':
				case '\t':
				case '\n':
				case '\r':
			}
		}

		this.lastLineNumber = in.getLineNumber();
		switch( ch )
		{
			// Identifier
			case 'a': case 'b': case 'c': case 'd': case 'e':
			case 'f': case 'g': case 'h': case 'i': case 'j':
			case 'k': case 'l': case 'm': case 'n': case 'o':
			case 'p': case 'q': case 'r': case 's': case 't':
			case 'u': case 'v': case 'w': case 'x': case 'y':
			case 'z':
			case 'A': case 'B': case 'C': case 'D': case 'E':
			case 'F': case 'G': case 'H': case 'I': case 'J':
			case 'K': case 'L': case 'M': case 'N': case 'O':
			case 'P': case 'Q': case 'R': case 'S': case 'T':
			case 'U': case 'V': case 'W': case 'X': case 'Y':
			case 'Z':
			case '_': case '$':
				while( ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z' || ch >= '0' && ch <= '9' || ch == '$' || ch == '_' )
				{
					result.append( (char)ch );
					ch = in.read();
				}
				in.push( ch );
				// TODO Internalize identifier tokens
				return new Token( Token.TYPE.IDENTIFIER, result.toString() );

			// String
			case '"':
				while( true )
				{
					ch = in.read();
					switch( ch )
					{
						case -1:
							throw new SourceException( "Missing \"", in.getLocation() );
						case '"':
							return new Token( TYPE.STRING, result.toString() );
						case '\\':
							ch = in.read();
							switch( ch )
							{
								case -1: throw new SourceException( "Incomplete escape sequence", in.getLocation() );
								case '\n': continue;
								case 'b': ch = '\b'; break;
								case 'f': ch = '\f'; break;
								case 'n': ch = '\n'; break;
								case 'r': ch = '\r'; break;
								case 't': ch = '\t'; break;
								case '\"': break;
								case '\\': break;
								case '$': result.append( '\\' ); break; // TODO Remember, not for '' strings
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

			// Number
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
					{
						in.push( ch );
						in.push( '.' );
						return new Token( TYPE.NUMBER, result.toString() );
					}
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
				in.push( ch );
				return new Token( TYPE.NUMBER, result.toString() );

			// Operators
			case '+':
			case '-':
				int ch2 = in.read();
				if( ch2 == ch )
					return new Token( Token.TYPE.UNAOP, String.valueOf( new char[] { (char)ch, (char)ch } ) );
//					if( ch == '-' && ch2 == '>' )
//						return Token.TOK_LAMBDA;
				in.push( ch2 );
				//$FALL-THROUGH$
			case '*':
			case '/':
			case '.':
				return new Token( Token.TYPE.BINOP, String.valueOf( (char)ch ) );
			case ':':
				return Token.TOK_COLON;
			case '!':
				return new Token( Token.TYPE.UNAOP, "!" );
			case '<':
			case '>':
				return new Token( Token.TYPE.BINOP, String.valueOf( (char)ch ) );
			case '=':
				ch = in.read();
				if( ch == '=' )
					return new Token( Token.TYPE.BINOP, "==" );
				in.push( ch );
				return new Token( Token.TYPE.BINOP, "=" ); // TODO Predefine all operator tokens
			case '&':
				ch = in.read();
				if( ch == '&' )
					return new Token( Token.TYPE.BINOP, "&&" );
				in.push( ch );
				throw new SourceException( "Unexpected character '" + (char)ch + "'", in.getLocation() );
			case '|':
				ch = in.read();
				if( ch == '|' )
					return new Token( Token.TYPE.BINOP, "||" );
				in.push( ch );
				throw new SourceException( "Unexpected character '" + (char)ch + "'", in.getLocation() );

			// Others
			case '(':
				return Token.TOK_PAREN_OPEN;
			case ')':
				return Token.TOK_PAREN_CLOSE;
			case ',':
				return Token.TOK_COMMA;
			case ';':
				return Token.TOK_SEMICOLON;
			case '{':
				return Token.TOK_BRACE_OPEN;
			case '}':
				return Token.TOK_BRACE_CLOSE;

			default:
				throw new SourceException( "Unexpected character '" + (char)ch + "'", in.getLocation() );
		}
	}

	/**
	 * Returns the current line number.
	 *
	 * @return The current line number.
	 */
	public int getLineNumber()
	{
		return getIn().getLineNumber();
	}

	/**
	 * Returns the current file location.
	 *
	 * @return The current file location.
	 */
	public SourceLocation getLocation()
	{
		return getIn().getLocation();
	}

	/**
	 * Returns the underlying reader. But only if the back buffer is empty, otherwise an IllegalStateException is thrown.
	 *
	 * @return The underlying reader.
	 */
	public SourceReader getReader()
	{
		return getIn().getReader();
	}


	/**
	 * A CSV token.
	 *
	 * @author Ren� M. de Bloois
	 */
	// TODO Maybe we should remove this token class, and introduce the even mechanism like in JSONParser.
	static public class Token
	{
		/**
		 * Token types.
		 */
		@SuppressWarnings( "javadoc" )
		static public enum TYPE { IDENTIFIER, NUMBER, STRING, BINOP, UNAOP, PAREN_OPEN, PAREN_CLOSE, BRACE_OPEN, BRACE_CLOSE, COMMA, SEMICOLON, COLON, /* LAMBDA, */ EOF }

		@SuppressWarnings( "javadoc" )
		static final protected Token TOK_PAREN_OPEN = new Token( TYPE.PAREN_OPEN, "(" ); @SuppressWarnings( "javadoc" )
		static final protected Token TOK_PAREN_CLOSE = new Token( TYPE.PAREN_CLOSE, ")" ); @SuppressWarnings( "javadoc" )
		static final protected Token TOK_BRACE_OPEN = new Token( TYPE.BRACE_OPEN, "{" ); @SuppressWarnings( "javadoc" )
		static final protected Token TOK_BRACE_CLOSE = new Token( TYPE.BRACE_CLOSE, "}" ); @SuppressWarnings( "javadoc" )
		static final protected Token TOK_COMMA = new Token( TYPE.COMMA, "," ); @SuppressWarnings( "javadoc" )
		static final protected Token TOK_SEMICOLON = new Token( TYPE.SEMICOLON, ";" ); @SuppressWarnings( "javadoc" )
		static final protected Token TOK_COLON = new Token( TYPE.COLON, ":" ); @SuppressWarnings( "javadoc" )
//		static final protected Token TOK_LAMBDA = new Token( TYPE.LAMBDA, "->" ); @SuppressWarnings( "javadoc" )
		static final protected Token TOK_EOF = new Token( TYPE.EOF );

		/**
		 * The type of the token.
		 */
		private TYPE type;

		/**
		 * The value of the token.
		 */
		private String value;

		/**
		 * Constructs a new token.
		 *
		 * @param type The type of the token.
		 */
		private Token( TYPE type )
		{
			this.type = type;
		}

		/**
		 * @param type The type of the token.
		 * @param value The value of the token.
		 */
		private Token( TYPE type, String value )
		{
			this.type = type;
			this.value = value;
		}

		/**
		 * @return The type of the token.
		 */
		public TYPE getType()
		{
			return this.type;
		}

		/**
		 * Returns the value of token.
		 *
		 * @return The value of token.
		 */
		public String getValue()
		{
			if( this.value == null )
				throw new IllegalStateException( "Value is null" );
			return this.value;
		}

		@Override
		public String toString()
		{
			if( getType() == TYPE.STRING )
				return "\"" + this.value + "\""; // TODO Or maybe just the double quote. Actually, we don't know what quote is used.
			// TODO Maybe we should not parse the complete string as a token, especially with super strings
			if( this.value != null )
				return this.value.toString();
			Assert.isTrue( this == TOK_EOF );
			return "EOF";
		}

		/**
		 * @param s The string the compare this token value with.
		 *
		 * @return True if the token value is equal to the given string, false otherwise.
		 */
		public boolean eq( String s )
		{
			if( this.value == null )
				return false;
			return this.value.equals( s );
		}
	}

	/**
	 * Close the underlying reader.
	 */
	public void close()
	{
		getIn().close();
	}
}
