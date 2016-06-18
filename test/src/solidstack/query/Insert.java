package solidstack.query;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.testng.annotations.Test;

import solidstack.util.Pars;

public class Insert
{
	@Test
	public void testInsertByteArray() throws ClassNotFoundException, SQLException
	{
		Class.forName( "org.apache.derby.jdbc.EmbeddedDriver" );
		Connection connection = DriverManager.getConnection( "jdbc:derby:memory:test;create=true", "app", null );

		connection.createStatement().executeUpdate( "CREATE TABLE PICTABLE ( PIC BLOB )" );

		QueryLoader queries = new QueryLoader();
		queries.setTemplatePath( "classpath:/solidstack/query" );

		Query query = queries.getQuery( "insertBlob.sql" );
		byte[] bytes = new byte[ 1000 ];
		query.update( connection, new Pars( "pic", bytes ) );
		query.update( connection, new Pars( "pic", new ByteArrayInputStream( bytes ) ) );

		ResultSet result = connection.createStatement().executeQuery( "SELECT * FROM PICTABLE" );
		assertThat( result.next() ).isTrue();

		bytes = result.getBytes( 1 );
		assertThat( bytes ).hasSize( 1000 );

		assertThat( result.next() ).isTrue();

		bytes = result.getBytes( 1 );
		assertThat( bytes ).hasSize( 1000 );

		assertThat( result.next() ).isFalse();
	}

	@Test
	public void testInsertReader() throws ClassNotFoundException, SQLException
	{
		Class.forName( "org.apache.derby.jdbc.EmbeddedDriver" );
		Connection connection = DriverManager.getConnection( "jdbc:derby:memory:test;create=true", "app", null );

		connection.createStatement().executeUpdate( "CREATE TABLE TEXTTABLE ( TEXT CLOB )" );

		QueryLoader queries = new QueryLoader();
		queries.setTemplatePath( "classpath:/solidstack/query" );

		Query query = queries.getQuery( "insertClob.sql" );
		String text = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		query.update( connection, new Pars( "text", text ) );
		query.update( connection, new Pars( "text", new StringReader( text ) ) );

		ResultSet result = connection.createStatement().executeQuery( "SELECT * FROM TEXTTABLE" );
		assertThat( result.next() ).isTrue();

		text = result.getString( 1 );
		assertThat( text ).hasSize( 26 );

		assertThat( result.next() ).isTrue();

		text = result.getString( 1 );
		assertThat( text ).hasSize( 26 );

		assertThat( result.next() ).isFalse();
	}
}
