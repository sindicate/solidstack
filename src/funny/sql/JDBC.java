/*--
 * Copyright 2015 René M. de Bloois
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

package funny.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import solidstack.query.Query;
import solidstack.query.ResultList;
import solidstack.script.java.Function;
import solidstack.script.objects.FunctionObject;
import solidstack.script.objects.PString;
import solidstack.script.objects.Tuple;


// TODO Integrate with the query template package
public class JDBC
{
	private String url;
	private Connection connection;

	static public JDBC apply( String driver, String url ) throws ClassNotFoundException, SQLException
	{
		Class.forName( driver );
		return new JDBC( url, null, null );
	}

	static public JDBC apply( String driver, String url, String username, String password ) throws ClassNotFoundException, SQLException
	{
		Class.forName( driver );
		return new JDBC( url, username, password );
	}

	// TODO Expression by name, so that there is no added stackframe
	static public JDBCMonad doWith( String driver, String url ) throws ClassNotFoundException, SQLException
	{
		Class.forName( driver );
		return new JDBCMonad( url, null, null );
	}

	static public JDBCMonad doWith( String driver, String url, String username, String password ) throws ClassNotFoundException, SQLException
	{
		Class.forName( driver );
		return new JDBCMonad( url, username, password );
	}

	private JDBC( String url, String username, String password ) throws ClassNotFoundException, SQLException
	{
		connect( url, username, password );
	}

	private void connect( String url, String username, String password ) throws SQLException
	{
		this.url = url;
		this.connection = DriverManager.getConnection( this.url, username, password );
	}

	public boolean getAutoCommit() throws SQLException
	{
		return this.connection.getAutoCommit();
	}

	public void setAutoCommit( boolean autoCommit ) throws SQLException
	{
		this.connection.setAutoCommit( autoCommit );
	}

	public void commit() throws SQLException
	{
		this.connection.commit();
	}

	public void rollback() throws SQLException
	{
		this.connection.rollback();
	}

	public void close() throws SQLException
	{
		this.connection.close();
	}

	public void eachRow( String query, Function function ) throws SQLException
	{
		ResultSet result = query( query );
		while( result.next() )
			function.call( result );
	}

	public void eachRow( PString query, Function function ) throws SQLException
	{
		ResultSet result = query( query );
		while( result.next() )
			function.call( result );
	}

	public ResultSet query( String query ) throws SQLException
	{
		return this.connection.createStatement().executeQuery( query );
	}

	// TODO Do this with the query template package
	public ResultSet query( PString query ) throws SQLException
	{
		return prepare( query ).executeQuery();
	}

	public List list( String query ) throws SQLException
	{
		ResultSet resultSet = query( query );
		return new ResultList( Query.listOfArrays( resultSet, true ), Query.getColumnLabelMap( resultSet.getMetaData() ) );
	}

	public List list( PString query ) throws SQLException
	{
		ResultSet resultSet = query( query );
		return new ResultList( Query.listOfArrays( resultSet, true ), Query.getColumnLabelMap( resultSet.getMetaData() ) );
	}

	public Object first( String query ) throws SQLException
	{
		ResultSet resultSet = query( query );
		boolean record = resultSet.next();
		int count = resultSet.getMetaData().getColumnCount();

		if( count == 1 )
			return record ? resultSet.getObject( 1 ) : null;

		Tuple tuple = new Tuple(); // TODO This is not language independent
		for( int i = 1; i <= count; i++ )
			tuple.append( record ? resultSet.getObject( i ) : null );
		return tuple;
	}

	public boolean execute( String query ) throws SQLException
	{
		return this.connection.createStatement().execute( query );
	}

	public boolean execute( PString query ) throws SQLException
	{
		return prepare( query ).execute();
	}

	public int update( String query ) throws SQLException
	{
		return this.connection.createStatement().executeUpdate( query );
	}

	public int update( PString query ) throws SQLException
	{
		return prepare( query ).executeUpdate();
	}

	// TODO Do this with the query template package
	private PreparedStatement prepare( PString query ) throws SQLException
	{
		Object[] values = query.getValues(); // Get the values
		int len = values.length;
		Object[] parameters = Arrays.copyOf( values, len ); // Copy to parameter array
		Arrays.fill( values, "?" ); // Replace values with ?

		PreparedStatement statement = this.connection.prepareStatement( query.toString() );
		for( int i = 0; i < len; i++ )
			statement.setObject( i + 1, parameters[ i ] );

		return statement;
	}

	static class JDBCMonad
	{
		private String url;
		private String username;
		private String password;

//		public JDBCMonad( String url )
//		{
//			this.url = url;
//		}

		public JDBCMonad( String url, String username, String password )
		{
			this.url = url;
			this.username = username;
			this.password = password;
		}

		public Object apply( FunctionObject function ) throws ClassNotFoundException, SQLException
		{
			JDBC jdbc = new JDBC( this.url, this.username, this.password );
			try
			{
				return function.call( jdbc );
			}
			finally
			{
				jdbc.close();
			}
		}
	}
}
