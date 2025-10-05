package Parallel_ACPM;

public class Event
{
	private long time;
	private Message message;
	
	public Event(long time, Message message)
	{
		this.time = time;
		this.message = message;
	}
	public long getTime()
	{
		return time;
	}
	public void setTime(long time)
	{
		this.time = time;
	}
	public Message getMessage()
	{
		return message;
	}
	public void setMessage(Message message) 
	{
		this.message = message;
	}
	public String toString()
	{
		return message + " (" + time + ")";
	}
}
