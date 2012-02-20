/*--
 * Copyright 2011 Ren� M. de Bloois
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

package solidstack.query.hibernate;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import org.hibernate.JDBCException;
import org.hibernate.Session;

import solidstack.query.Query;


/**
 * Adapts the given Query to Hibernate.
 * 
 * @author Ren� M. de Bloois
 */
// FIXME What about a ConnectedHibernateAdapter?
/*
 	Query(Connection,Args)
	Query+Connection(Args)
 */
public class HibernateConnectedQueryAdapter
{
	/**
	 * The query that is adapted to Hibernate.
	 */
	protected Query query;

	protected Session session;

	/**
	 * Constructor.
	 * 
	 * @param query A query to adapt to Hibernate.
	 */
	public HibernateConnectedQueryAdapter( Query query, Session session )
	{
		this.query = query;
		this.session = session;
	}

	/**
	 * Retrieves a {@link ResultSet} from the given Hibernate {@link Session}.
	 * 
	 * @param session The Hibernate {@link Session} to use.
	 * @param args The arguments to the query.
	 * @return a {@link ResultSet}.
	 * @throws JDBCException SQLExceptions are translated to JDBCExceptions by Hibernate.
	 * @see Query#resultSet(Connection, Map)
	 */
	public ResultSet resultSet( Map< String, Object > args )
	{
		return HibernateSupport.resultSet( this.query, this.session, args );
	}

	/**
	 * Retrieves a {@link List} of {@link Object} arrays from the given Hibernate {@link Session}.
	 * 
	 * @param session The Hibernate {@link Session} to use.
	 * @param args The arguments to the query.
	 * @return a {@link List} of {@link Object} arrays.
	 * @throws JDBCException SQLExceptions are translated to JDBCExceptions by Hibernate.
	 * @see Query#listOfArrays(Connection, Map)
	 */
	public List< Object[] > listOfArrays( Map< String, Object > args )
	{
		return HibernateSupport.listOfArrays( this.query, this.session, args );
	}

	/**
	 * Retrieves a {@link List} of {@link Map}s from the given Hibernate {@link Session}.
	 * 
	 * @param session The Hibernate {@link Session} to use.
	 * @param args The arguments to the query.
	 * @return A {@link List} of {@link Map}s.
	 * @throws JDBCException SQLExceptions are translated to JDBCExceptions by Hibernate.
	 * @see Query#listOfMaps(Connection, Map)
	 */
	public List< Map< String, Object > > listOfMaps( Map< String, Object > args )
	{
		return HibernateSupport.listOfMaps( this.query, this.session, args );
	}

	/**
	 * Executes an update (DML) or a DDL query through the given Hibernate {@link Session}.
	 * 
	 * @param session The Hibernate {@link Session} to use.
	 * @param args The arguments to the query.
	 * @return The row count from a DML statement or 0 for SQL that does not return anything.
	 * @throws JDBCException SQLExceptions are translated to JDBCExceptions by Hibernate.
	 * @see Query#updateChecked(Connection, Map)
	 */
	public int update( Map< String, Object > args )
	{
		return HibernateSupport.update( this.query, this.session, args );
	}

	public <T> List< T > list( Map< String, Object > args )
	{
		return HibernateSupport.list( this.query, this.session, args );
	}

	public int executeUpdate( Map< String, Object > args )
	{
		return HibernateSupport.executeUpdate( this.query, this.session, args );
	}

	public <T> T uniqueResult( Map< String, Object > args )
	{
		return HibernateSupport.uniqueResult( this.query, this.session, args );
	}
}
