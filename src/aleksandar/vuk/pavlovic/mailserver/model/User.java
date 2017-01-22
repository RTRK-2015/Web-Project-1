package aleksandar.vuk.pavlovic.mailserver.model;


public class User
{
	public final String name;


	public User(String name)
	{
		this.name = name;
	}


	@Override
	public String toString()
	{
		return "User [name=" + name + "]";
	}
}
