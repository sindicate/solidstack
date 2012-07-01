package solidstack.hyperdb;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import solidstack.httpserver.HttpException;
import solidstack.httpserver.RequestContext;
import solidstack.httpserver.Response;
import solidstack.httpserver.ResponseOutputStream;
import solidstack.httpserver.Servlet;
import solidstack.lang.Assert;
import solidstack.util.Pars;

public class ViewServlet implements Servlet
{
	public Response call( final RequestContext context )
	{
		String schema = context.getRequest().getParameter( "schema" );
		final String view = '\"' + schema + "\".\"" + context.getRequest().getParameter( "view" ) + '\"'; // TODO SQL Escaping

		return new Response()
		{
			@Override
			public void write( ResponseOutputStream out ) throws IOException
			{
				Connection connection = DataSource.getConnection();
				try
				{
					Statement statement = connection.createStatement();
					try
					{
						final ResultSet result1 = statement.executeQuery( "SELECT COUNT(*) FROM " + view );
						Assert.isTrue( result1.next() );
						final Object object = result1.getObject( 1 );

						final ResultSet result2 = statement.executeQuery( "SELECT * FROM " + view );

						context.include( "/slt/view", new Pars( "title", "view " + view, "view", view, "result", result2, "count", object ) );
					}
					finally
					{
						statement.close();
					}
				}
				catch( SQLException e )
				{
					throw new HttpException( e );
				}
				finally
				{
					// TODO What if the connection has been broken?: java.sql.SQLException: Closed Connection
					DataSource.release( connection );
				}
			}
		};
	}
}