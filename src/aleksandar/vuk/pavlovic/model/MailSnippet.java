package aleksandar.vuk.pavlovic.model;


/**
 * Represents a mail snippet, that is, a mail without the body.
 */
public class MailSnippet
{
	/**
	 * The sender of the mail.
	 */
	public final String from;
	/**
	 * The subject of the mail.
	 */
	public final String subject;
	/**
	 * The id (ordinal number) of the mail.
	 */
	public final int id;


	/**
	 * Constructs a new {@link MailSnippet} instance.
	 * @param from Sender of the mail.
	 * @param subject Subject of the mail.
	 * @param id id (ordinal number) of the mail.
	 */
	public MailSnippet(String from, String subject, int id)
	{
		this.from = from;
		this.subject = subject;
		this.id = id;
	}


	/**
	 * Formats the mail snippet information to a human-readable format.
	 */
	@Override
	public String toString()
	{
		return "MailSnippet [from=" + from + ", subject=" + subject + ", id=" + id + "]";
	}
}
