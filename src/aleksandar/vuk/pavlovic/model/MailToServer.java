package aleksandar.vuk.pavlovic.model;


/**
 * Type of mail sent to the server. It is similar to the {@link MailFromServer} class,
 * except that it doesn't contain the {@link MailFromServer#id} field, which is
 * calculated by the server.
 */
public class MailToServer
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
	 * Constructs a new {@link MailToServer} instance.
	 * @param from Sender of the mail.
	 * @param to Recipient of the mail.
	 * @param subject Subject of the mail.
	 * @param body Body of the mail.
	 */
	public MailToServer(String from, String to, String subject, String body)
	{
		this.from = from;
		this.to = to;
		this.subject = subject;
		this.body = body;
	}


	/**
	 * Formats the mail information to a human-readable format.
	 */
	@Override
	public String toString()
	{
		return "IngoingMail [from=" + from + ", to=" + to + ", subject=" + subject + ", body=" + body + "]";
	}
}
