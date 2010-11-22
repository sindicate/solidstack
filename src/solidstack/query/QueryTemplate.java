/*--
 * Copyright 2006 Ren� M. de Bloois
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

package solidstack.query;

import groovy.lang.Closure;
import groovy.lang.GString;

import java.sql.Connection;
import java.util.Map;

/**
 * Represents the query template.
 * 
 * @author Ren� M. de Bloois
 */
public class QueryTemplate
{
	private final Closure query;
	private long lastModified;

	/**
	 * Constructor.
	 * 
	 * @param closure The closure that produces the {@link GString} for the query.
	 * @param lastModified The last modified time stamp of the file that contains the query template.
	 */
	public QueryTemplate( Closure closure, long lastModified )
	{
		this.query = closure;
		this.lastModified = lastModified;
	}

	/**
	 * Bind the template with the given parameters. This produces a {@link Query} that can be executed against a {@link Connection}.
	 * 
	 * @param params The parameters for the query.
	 * @return A {@link Query}.
	 */
	public Query bind( Map< String, Object > params )
	{
		Query query = new Query( (Closure)this.query.clone() );
		query.params( params );
		return query;
	}

	/**
	 * Returns the last modification time stamp for the file that contains the query template.
	 * 
	 * @return The last modification time stamp for the file that contains the query template.
	 */
	public long getLastModified()
	{
		return this.lastModified;
	}
}
