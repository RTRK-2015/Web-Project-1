package aleksandar.vuk.pavlovic.model;

public class MailSnippet
{
	public final String from;
	public final String subject;
	public final int id;
	
	
	public MailSnippet(String from, String subject, int id)
	{
		this.from = from;
		this.subject = subject;
		this.id = id;
	}


	@Override
	public String toString()
	{
		return "MailSnippet [from=" + from + ", subject=" + subject + ", id=" + id + "]";
	}
}
