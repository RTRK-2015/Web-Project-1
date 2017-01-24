package aleksandar.vuk.pavlovic.model;


/**
 * Represents a user in the database, as much as a single field class can represent anything.
 */
public class User
{
	/**
	 * The name of the user.
	 */
	public final String name;


	/**
	 * Constructs a new {@link User} instance.
	 * @param name Name of the user.
	 */
	public User(String name)
	{
		this.name = name;
	}


	/**
	 * Formats the mail information to a human-readable format.
	 */
	@Override
	public String toString()
	{
		return "User [name=" + name + "]";
	}
}
