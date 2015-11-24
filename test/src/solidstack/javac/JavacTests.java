package solidstack.javac;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.fest.assertions.api.Assertions;
import org.testng.annotations.Test;


public class JavacTests
{
	@Test
	public void basicTest() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException
	{
		String source =
				"package solidstack.javac;" +
				"public class HelloWorld {" +
				"   public String sayHello() { " +
				"		return \"Hello World!\"; " +
				"	}" +
				"}";

		Class<?> cls = InMemJavaCompiler.compile( "solidstack.javac.HelloWorld", source );

		Assertions.assertThat( cls ).isNotNull();
		Assertions.assertThat( cls.getDeclaredMethods() ).hasSize( 1 );

		Object instance = cls.newInstance();
		Method sayHello = cls.getMethod( "sayHello" );
		Object result = sayHello.invoke( instance );

		Assertions.assertThat( result ).isExactlyInstanceOf( String.class );
		Assertions.assertThat( (String)result ).isEqualTo( "Hello World!" );
	}
}
