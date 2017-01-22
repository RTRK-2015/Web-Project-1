package aleksandar.vuk.pavlovic.model;


public class MailToServer
{
	public final String from;
	public final String to;
	public final String subject;
	public final String body;


	public MailToServer(String from, String to, String subject, String body)
	{
		this.from = from;
		this.to = to;
		this.subject = subject;
		this.body = body;
	}


	@Override
	public String toString()
	{
		return "IngoingMail [from=" + from + ", to=" + to + ", subject=" + subject + ", body=" + body + "]";
	}
}
