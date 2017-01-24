package aleksandar.vuk.pavlovic.model;


/**
 * Type of mail that the server sends to client. It is very similar to
 * {@link MailToServer}, except that it also includes the id (ordinal number).
 */
public class MailFromServer
{
	/**
	 * The sender of the mail.
	 */
	public final String from;
	/**
	 * The recipient of the mail.
	 */
	public final String to;
	/**
	 * The subject of the mail.
	 */
	public final String subject;
	/**
	 * The body of the mail.
	 */
	public final String body;
	/**
	 * The id (ordinal number) of the mail.
	 */
	public final int id;


	/**
	 * Constructs a new {@link MailFromServer} instance.
	 * @param from Sender of the mail.
	 * @param to Recipient of the mail.
	 * @param subject Subject of the mail.
	 * @param body Body of the mail.
	 * @param id id (ordinal number) of the mail.
	 */
	public MailFromServer(String from, String to, String subject, String body, int id)
	{
		this.from = from;
		this.to = to;
		this.subject = subject;
		this.body = body;
		this.id = id;
	}


	/**
	 * Formats the mail information to a human-readable format.
	 */
	@Override
	public String toString()
	{
		return "OutgoingMail [from=" + from + ", to=" + to + ", subject=" + subject + ", body=" + body + ", id=" + id
				+ "]";
	}
}
