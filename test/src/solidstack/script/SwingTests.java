package solidstack.script;

import java.io.IOException;

import org.testng.annotations.Test;

public class SwingTests extends Util
{
	@Test
	public void test1() throws IOException
	{
		String script = readFile( "Swing.funny" );
		eval( script );
	}
}
