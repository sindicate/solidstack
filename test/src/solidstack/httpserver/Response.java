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

package solidstack.httpserver;

import java.io.OutputStream;


/*
 * Types of responses:
 * 1. Ready, does not need input anymore ->
 * 2. Ready, needs input still
 * 3. Not ready, does not need input anymore
 */
abstract public class Response
{
	private boolean ready;
	private ResponseListener listener;

	synchronized public void setListener( ResponseListener listener )
	{
		this.listener = listener;
	}

	abstract public void write( OutputStream out );

	public boolean needsInput()
	{
		return false;
	}

	public boolean isReady()
	{
		return this.ready;
	}

	synchronized public void ready()
	{
		if( this.listener != null )
			this.listener.responseIsReady( this );
		else
			setReady();
	}

	public void setReady()
	{
		this.ready = true;
	}
}
