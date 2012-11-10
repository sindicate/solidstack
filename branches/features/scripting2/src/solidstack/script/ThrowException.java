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

import java.io.PrintStream;
import java.io.PrintWriter;

import solidstack.io.SourceLocation;

public class ThrowException extends RuntimeException
{
	private Object object;
	private SourceLocation[] stack;

	public ThrowException( Object object, SourceLocation[] stack )
	{
		this.object = object;
		this.stack = stack;
	}

	@Override
	public synchronized Throwable fillInStackTrace()
	{
		return null;
	}

	@Override
	public String getMessage()
	{
		return this.object.toString();
	}

	@Override
	public String toString()
	{
		return getMessage();
	}

	@Override
	public void printStackTrace( PrintStream out )
	{
		PrintWriter writer = new PrintWriter( out );
		printStackTrace( writer );
		writer.flush();
	}

	@Override
	public void printStackTrace( PrintWriter out )
	{
		out.println( toString() );
		for( SourceLocation location : this.stack )
			out.append( "\tat " ).append( location.toString() ).println();
	}
}
