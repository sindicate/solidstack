package solidstack.script;

public class CustomInterfaceConsumer
{
	public void run( CustomInterface interfase )
	{
		interfase.call();
	}

	static public interface CustomInterface
	{
		void call();
	}
}
