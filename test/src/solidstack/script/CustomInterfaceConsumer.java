package solidstack.script;

public class CustomInterfaceConsumer
{
	public void run1( CustomInterface1 interfase )
	{
		interfase.call();
	}

	static public interface CustomInterface1
	{
		void call();
	}

	public void run2( CustomInterface2 interfase, String hello )
	{
		interfase.call( hello );
	}

	static public interface CustomInterface2
	{
		void call( String hello );
	}

	public String run3( CustomInterface3 interfase )
	{
		return interfase.call();
	}

	static public interface CustomInterface3
	{
		String call();
	}

	public Long run4( CustomInterface4 interfase )
	{
		return interfase.call();
	}

	static public interface CustomInterface4
	{
		Long call();
	}

	public long run5( CustomInterface5 interfase )
	{
		return interfase.call();
	}

	static public interface CustomInterface5
	{
		long call();
	}
}
