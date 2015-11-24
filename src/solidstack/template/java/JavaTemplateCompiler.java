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

package solidstack.template.java;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import solidstack.javac.CompilerClassLoader;
import solidstack.lang.Assert;
import solidstack.lang.SystemException;
import solidstack.template.JSPLikeTemplateParser.ParseEvent;
import solidstack.template.TemplateCompilerContext;


/**
 * Compiles the given parser events, directives and imports to a {@link JavaTemplate}.
 *
 * @author René de Bloois
 */
public class JavaTemplateCompiler
{
	private static final String TEMPLATE_PKG = "solidstack.template.tmp";
	static private final Pattern PATH_PATTERN = Pattern.compile( "/*(?:(.+?)/+)?([^/]+)" );

	/**
	 * Generates the Java.
	 *
	 * @param context The compilation context.
	 */
	public void generateScript( TemplateCompilerContext context )
	{
		// TODO This may give conflicts when more than one TemplateLoader is used. This must be the complete path.
		Matcher matcher = PATH_PATTERN.matcher( context.getPath() );
		Assert.isTrue( matcher.matches() );
		String path = matcher.group( 1 );
		String name = matcher.group( 2 ).replaceAll( "[\\.-]", "_" );

		String pkg = TEMPLATE_PKG;
		if( path != null )
			pkg += "." + path.replaceAll( "/+", "." );

		context.setPackage( pkg );
		context.setClassName( name );

		StringBuilder buffer = new StringBuilder( 1024 );

		buffer.append( "package " ).append( pkg ).append( ";" );
		if( context.getImports() != null )
			for( String imprt : context.getImports() )
				buffer.append( "import " ).append( imprt ).append( ';' );
		buffer.append( "public class " ).append( name ).append( " extends solidstack.template.java.JavaTemplate" );
		buffer.append( "{ public void execute(solidstack.template.EncodingWriter out,java.util.Map args){" );

		boolean text = false;
		boolean plus = false;
		for( ParseEvent event : context.getEvents() )
			switch( event.getEvent() )
			{
				case TEXT:
				case WHITESPACE:
					if( !text )
					{
						buffer.append( "out.write(\"" );
						text = true;
					}
					else if( plus )
					{
						buffer.append( "+\"" );
						plus = false;
					}
					writeJavaString( buffer, event.getData() );
					break;

				case NEWLINE:
					if( !text )
					{
						buffer.append( "out.write(\"" );
						text = true;
					}
					else if( plus )
						buffer.append( "+\"" );
					buffer.append( "\\n\"\n" );
					plus = true;
					break;

				case SCRIPT:
					if( text )
					{
						if( !plus )
							buffer.append( '"' );
						buffer.append( ");" );
						text = plus = false;
					}
					buffer.append( event.getData() ).append( ';' );
					break;

				case EXPRESSION:
					if( text )
					{
						if( !plus )
							buffer.append( '"' );
						buffer.append( ");" );
						text = plus = false;
					}
					buffer.append( "out.write(" ).append( event.getData() ).append( ");" );
					break;

				case EXPRESSION2:
					if( text )
					{
						if( !plus )
							buffer.append( '"' );
						buffer.append( ");" );
						text = plus = false;
					}
					buffer.append( "out.writeEncoded(" ).append( event.getData() ).append( ");" );
					break;

				case DIRECTIVE:
				case COMMENT:
					if( event.getData().length() == 0 )
						break;
					if( text )
					{
						if( !plus )
							buffer.append( '"' );
						buffer.append( ");" );
						text = plus = false;
					}
					buffer.append( event.getData() );
					break;

				default:
					Assert.fail( "Unexpected event " + event.getEvent() );
			}

		if( text )
			buffer.append( "\");" );
		buffer.append( "}}" );

		context.setScript( buffer );
	}

	public void compileScript( TemplateCompilerContext context )
	{
		CompilerClassLoader compiler = new CompilerClassLoader( Thread.currentThread().getContextClassLoader() );
		Class<?> cls = compiler.compile( context.getPackage() + "." + context.getClassName(), context.getScript() );
		try
		{
			context.setTemplate( (JavaTemplate)cls.newInstance() );
		}
		catch( InstantiationException e )
		{
			throw new SystemException( e ); // TODO Is this the correct exception?
		}
		catch( IllegalAccessException e )
		{
			throw new SystemException( e ); // TODO Is this the correct exception?
		}
	}

	// TODO Any other characters?
	static private void writeJavaString( StringBuilder buffer, String s )
	{
		char[] chars = s.toCharArray();
		int len = chars.length;
		char c;
		for( int i = 0; i < len; i++ )
			switch( c = chars[ i ] )
			{
				case '"':
				case '\\':
					buffer.append( '\\' ); //$FALL-THROUGH$
				default:
					buffer.append( c );
			}
	}
}
