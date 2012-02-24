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

package solidstack.query.jpa;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.hibernate.QueryException;

import solidstack.lang.Assert;
import solidstack.query.Query;
import solidstack.query.Query.PreparedSQL;
import solidstack.query.Query.Type;


/**
 * Adds support for JPA. JPA dependencies must be kept separate from the rest.
 * 
 * @author Ren� M. de Bloois
 */
// TODO What about JPQL query templates? Positional parameters are like this ?1, ?2 etc
// TODO We need a new directive: <%@ query type="SQL|JPQL|HQL" %> or <%@ query lang="native|hibernate|jpa" %>
// TODO What about the <%@ query resultClass="youNameIt" %>
// TODO What about using the script language to build the result?
public class JPASupport
{
	/**
	 * Executes an update (DML) or a DDL query through the given {@link EntityManager}.
	 * 
	 * @param query The query.
	 * @param entityManager The {@link EntityManager} to use.
	 * @param args The arguments to the query.
	 * @return The number of entities updated or deleted.
	 * @see javax.persistence.Query#executeUpdate()
	 */
	static public int executeUpdate( Query query, EntityManager entityManager, Map< String, Object > args )
	{
		return createQuery( query, entityManager, args ).executeUpdate();
	}

	/**
	 * Retrieves a {@link List} of JPA Entities from the given {@link EntityManager}.
	 * 
	 * @param query The query.
	 * @param entityManager The {@link EntityManager} to use.
	 * @param args The arguments to the query.
	 * @return A {@link List} of entities.
	 * @see javax.persistence.Query#getResultList()
	 */
	@SuppressWarnings( "unchecked" )
	static public <T> List< T > getResultList( Query query, EntityManager entityManager, Map< String, Object > args )
	{
		List<T> result = createQuery( query, entityManager, args ).getResultList();
		if( query.getType() == Type.SQL && query.isFlyWeight() )
			if( !result.isEmpty() && result.get( 0 ) instanceof Object[] )
				reduceWeight( (List<Object[]>)result );
		return result;
	}

	/**
	 * Retrieves a {@link List} of JPA Entities from the given {@link EntityManager}.
	 * 
	 * @param query The query.
	 * @param entityManager The {@link EntityManager} to use.
	 * @param resultClass The class to map the results to.
	 * @param args The arguments to the query.
	 * @return A {@link List} of entities.
	 * @see javax.persistence.Query#getResultList()
	 */
	@SuppressWarnings( "unchecked" )
	static public <T> List< T > getResultList( Query query, EntityManager entityManager, Class< T > resultClass, Map< String, Object > args )
	{
		return createQuery( query, entityManager, resultClass, args ).getResultList();
	}

	/**
	 * Retrieves a single JPA Entity from the given {@link EntityManager}.
	 * 
	 * @param query The query.
	 * @param entityManager The {@link EntityManager} to use.
	 * @param args The arguments to the query.
	 * @return An entity.
	 * @see javax.persistence.Query#getSingleResult()
	 */
	@SuppressWarnings( "unchecked" )
	static public <T> T getSingleResult( Query query, EntityManager entityManager, Map< String, Object > args )
	{
		return (T)createQuery( query, entityManager, args ).getSingleResult();
	}

	/**
	 * Retrieves a single JPA Entity from the given {@link EntityManager}.
	 * 
	 * @param query The query.
	 * @param entityManager The {@link EntityManager} to use.
	 * @param resultClass The class to map the results to.
	 * @param args The arguments to the query.
	 * @return An entity.
	 * @see javax.persistence.Query#getSingleResult()
	 */
	@SuppressWarnings( "unchecked" )
	static public <T> T getSingleResult( Query query, EntityManager entityManager, Class< T > resultClass, Map< String, Object > args )
	{
		return (T)createQuery( query, entityManager, resultClass, args ).getSingleResult();
	}

	/**
	 * Creates a JPA query.
	 * 
	 * @param query The query.
	 * @param entityManager The {@link EntityManager} to use.
	 * @param resultClass The class to map the results to.
	 * @param args The arguments to the query.
	 * @return The JPA query.
	 * @see EntityManager#createNativeQuery(String, Class)
	 */
	// TODO And what about the one with the resultmapping?
	// FIXME Rename to getNativeQuery
	static public javax.persistence.Query createQuery( Query query, EntityManager entityManager, Class< ? > resultClass, Map< String, Object > args )
	{
		return createQuery0( query, entityManager, resultClass, args );
	}

	/**
	 * Creates a JPA query.
	 * 
	 * @param query The query.
	 * @param entityManager The {@link EntityManager} to use.
	 * @param args The arguments to the query.
	 * @return The JPA query.
	 * @see EntityManager#createNativeQuery(String, Class)
	 */
	static public javax.persistence.Query createQuery( Query query, EntityManager entityManager, Map< String, Object > args )
	{
		return createQuery0( query, entityManager, null, args );
	}

	/**
	 * Creates a JPA query.
	 * 
	 * @param query The query.
	 * @param entityManager The {@link EntityManager} to use.
	 * @param resultClass The class to map the results to.
	 * @param args The arguments to the query.
	 * @return The JPA query.
	 * @see EntityManager#createNativeQuery(String, Class)
	 */
	// TODO What about non native queries? And should we then rename this to createNativeQuery?
	static private javax.persistence.Query createQuery0( Query query, EntityManager entityManager, Class< ? > resultClass, Map< String, Object > args )
	{
		PreparedSQL preparedSql = query.getPreparedSQL( args );

		// TODO Native query with resultSetMapping
		javax.persistence.Query result;
		if( query.getType() == Type.SQL )
			if( resultClass != null )
				result = entityManager.createNativeQuery( preparedSql.getSQL(), resultClass );
		else
			result = entityManager.createNativeQuery( preparedSql.getSQL() );
		else if( query.getType() == Type.JPQL )
			if( resultClass != null )
				result = entityManager.createQuery( preparedSql.getSQL(), resultClass );
			else
				result = entityManager.createQuery( preparedSql.getSQL() );
		else
			throw new QueryException( "Query type '" + query.getType() + "' not recognized" );

		List< Object > pars = preparedSql.getParameters();
		int i = 0;
		for( Object par : pars )
		{
			if( par != null )
			{
				Assert.isFalse( par instanceof Collection );
				Assert.isFalse( par.getClass().isArray() );
			}
			result.setParameter( ++i, par );
		}
		return result;
	}

	static public void reduceWeight( List<Object[]> list )
	{
		// THIS CAN REDUCE MEMORY USAGE WITH 90 TO 95 PERCENT, PERFORMANCE IMPACT IS ONLY 5 PERCENT

		Map< Object, Object > sharedData = new HashMap< Object, Object >();
		for( Object[] objects : list )
			if( objects != null )
				for( int len = objects.length, i = 0; i < len; i++ )
				{
					Object object = objects[ i ];
					if( object != null )
					{
						Object temp = sharedData.get( object );
						if( temp != null )
							objects[ i ] = temp;
						else
							sharedData.put( object, object );
					}
				}
	}
}
