package aleksandar.vuk.pavlovic.model;


public class MailFromServer
{
	public final String from;
	public final String to;
	public final String subject;
	public final String body;
	public final int id;


	public MailFromServer(String from, String to, String subject, String body, int id)
	{
		this.from = from;
		this.to = to;
		this.subject = subject;
		this.body = body;
		this.id = id;
	}


	@Override
	public String toString()
	{
		return "OutgoingMail [from=" + from + ", to=" + to + ", subject=" + subject + ", body=" + body + ", id=" + id
				+ "]";
	}
}
