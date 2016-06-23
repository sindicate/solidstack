/*--
 * Copyright 2016 René M. de Bloois
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

package solidstack.classgen.bytecode;

import solidstack.classgen.Bytes;

public class For implements Statement
{
	private Statement initialization;
	private Conditional termination;
	private Statement increment;
	private Statements statements;


	public For( Statement initialization, Conditional termination, Statement increment, Statement... statements )
	{
		this.initialization = initialization;
		this.termination = termination;
		this.increment = increment;
		this.statements = new Statements( statements );
	}

	@Override
	public void toByteCode( Bytes bytes )
	{
		// Initialization
		this.initialization.toByteCode( bytes );

		// Conditional jump to below
		int continu = bytes.size();
		Jump jump = this.termination.getJump( bytes );

		// All the statements
		this.statements.toByteCode( bytes );

		// The iteration
		this.increment.toByteCode( bytes );

		// Jump to the 'continu' above
		ByteCode.jump( bytes, continu - bytes.size() );

		// Link the conditional jump above with the current position
		jump.link( bytes.size() );
	}
}
