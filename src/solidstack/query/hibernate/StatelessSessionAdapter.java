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

package solidstack.query.hibernate;

import java.io.Serializable;
import java.sql.Connection;

import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.Filter;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.IdentifierLoadAccess;
import org.hibernate.LobHelper;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.NaturalIdLoadAccess;
import org.hibernate.Query;
import org.hibernate.ReplicationMode;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.SharedSessionBuilder;
import org.hibernate.SimpleNaturalIdLoadAccess;
import org.hibernate.StatelessSession;
import org.hibernate.Transaction;
import org.hibernate.TypeHelper;
import org.hibernate.UnknownProfileException;
import org.hibernate.jdbc.ReturningWork;
import org.hibernate.jdbc.Work;
import org.hibernate.stat.SessionStatistics;


/**
 * Adapts a Hibernate {@link StatelessSession} to a normal {@link Session}.
 *
 * @author René de Bloois
 */
@SuppressWarnings( "rawtypes" )
public class StatelessSessionAdapter implements Session
{
	private static final long serialVersionUID = 1L;

	private StatelessSession session;


	/**
	 * @param session The stateless session.
	 */
	public StatelessSessionAdapter( StatelessSession session )
	{
		this.session = session;
	}

	// ---------- Methods from org.hibernate.SharedSessionContract

	@Override
	public Transaction beginTransaction()
	{
		return this.session.beginTransaction();
	}

	@Override
	public Criteria createCriteria( Class arg0 )
	{
		return this.session.createCriteria( arg0 );
	}

	@Override
	public Criteria createCriteria( String arg0 )
	{
		return this.session.createCriteria( arg0 );
	}

	@Override
	public Criteria createCriteria( Class arg0, String arg1 )
	{
		return this.session.createCriteria( arg0, arg1 );
	}

	@Override
	public Criteria createCriteria( String arg0, String arg1 )
	{
		return this.session.createCriteria( arg0, arg1 );
	}

	@Override
	public Query createQuery( String arg0 )
	{
		return this.session.createQuery( arg0 );
	}

	@Override
	public SQLQuery createSQLQuery( String arg0 )
	{
		return this.session.createSQLQuery( arg0 );
	}

	@Override
	public Query getNamedQuery( String arg0 )
	{
		return this.session.getNamedQuery( arg0 );
	}

	@Override
	public String getTenantIdentifier()
	{
		return this.session.getTenantIdentifier();
	}

	@Override
	public Transaction getTransaction()
	{
		return this.session.getTransaction();
	}

	// ---------- Methods that StatelessSession and Session have in common but are not in SharedSessionContract

	@Override
	public Connection close() throws HibernateException
	{
		this.session.close();
		return null;
	}

	@Override
	public void delete( Object arg0 )
	{
		this.session.delete( arg0 );
	}

	@Override
	public void delete( String arg0, Object arg1 )
	{
		this.session.delete( arg0, arg1 );
	}

	@Override
	public Object get( Class arg0, Serializable arg1 )
	{
		return this.session.get( arg0, arg1 );
	}

	@Override
	public Object get( String arg0, Serializable arg1 )
	{
		return this.session.get( arg0, arg1 );
	}

	@Override
	public Object get( Class arg0, Serializable arg1, LockMode arg2 )
	{
		return this.session.get( arg0, arg1, arg2 );
	}

	@Override
	public Object get( String arg0, Serializable arg1, LockMode arg2 )
	{
		return this.session.get( arg0, arg1, arg2 );
	}

	@Override
	public void refresh( Object arg0 )
	{
		this.session.refresh( arg0 );
	}

	@Override
	public void refresh( String arg0, Object arg1 )
	{
		this.session.refresh( arg0, arg1 );
	}

	@Override
	public void refresh( Object arg0, LockMode arg1 )
	{
		this.session.refresh( arg0, arg1 );
	}

	@Override
	public void update( Object arg0 )
	{
		this.session.update( arg0 );
	}

	@Override
	public void update( String arg0, Object arg1 )
	{
		this.session.update( arg0, arg1 );
	}

	// ---------- Unsupported methods (which do not exist in StatelessSession)

	@Override
	public LockRequest buildLockRequest( LockOptions arg0 )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public IdentifierLoadAccess byId( String arg0 )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public IdentifierLoadAccess byId( Class arg0 )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public NaturalIdLoadAccess byNaturalId( String arg0 )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public NaturalIdLoadAccess byNaturalId( Class arg0 )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public SimpleNaturalIdLoadAccess bySimpleNaturalId( String arg0 )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public SimpleNaturalIdLoadAccess bySimpleNaturalId( Class arg0 )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void cancelQuery() throws HibernateException
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean contains( Object arg0 )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Query createFilter( Object arg0, String arg1 )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void disableFetchProfile( String arg0 ) throws UnknownProfileException
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void disableFilter( String arg0 )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Connection disconnect()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T doReturningWork( ReturningWork<T> arg0 ) throws HibernateException
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void doWork( Work arg0 ) throws HibernateException
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void enableFetchProfile( String arg0 ) throws UnknownProfileException
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Filter enableFilter( String arg0 )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void evict( Object arg0 )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void flush() throws HibernateException
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Object get( Class arg0, Serializable arg1, LockOptions arg2 )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Object get( String arg0, Serializable arg1, LockOptions arg2 )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public CacheMode getCacheMode()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public LockMode getCurrentLockMode( Object arg0 )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Filter getEnabledFilter( String arg0 )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getEntityName( Object arg0 )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public FlushMode getFlushMode()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Serializable getIdentifier( Object arg0 )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public LobHelper getLobHelper()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public SessionFactory getSessionFactory()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public SessionStatistics getStatistics()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public TypeHelper getTypeHelper()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isConnected()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isDefaultReadOnly()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isDirty() throws HibernateException
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isFetchProfileEnabled( String arg0 ) throws UnknownProfileException
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isOpen()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isReadOnly( Object arg0 )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Object load( Class arg0, Serializable arg1 )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Object load( String arg0, Serializable arg1 )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void load( Object arg0, Serializable arg1 )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Object load( Class arg0, Serializable arg1, LockMode arg2 )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Object load( Class arg0, Serializable arg1, LockOptions arg2 )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Object load( String arg0, Serializable arg1, LockMode arg2 )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Object load( String arg0, Serializable arg1, LockOptions arg2 )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void lock( Object arg0, LockMode arg1 )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void lock( String arg0, Object arg1, LockMode arg2 )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Object merge( Object arg0 )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Object merge( String arg0, Object arg1 )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void persist( Object arg0 )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void persist( String arg0, Object arg1 )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void reconnect( Connection arg0 )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void refresh( Object arg0, LockOptions arg1 )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void refresh( String arg0, Object arg1, LockOptions arg2 )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void replicate( Object arg0, ReplicationMode arg1 )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void replicate( String arg0, Object arg1, ReplicationMode arg2 )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Serializable save( Object arg0 )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Serializable save( String arg0, Object arg1 )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void saveOrUpdate( Object arg0 )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void saveOrUpdate( String arg0, Object arg1 )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public SharedSessionBuilder sessionWithOptions()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void setCacheMode( CacheMode arg0 )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void setDefaultReadOnly( boolean arg0 )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void setFlushMode( FlushMode arg0 )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void setReadOnly( Object arg0, boolean arg1 )
	{
		throw new UnsupportedOperationException();
	}
}
