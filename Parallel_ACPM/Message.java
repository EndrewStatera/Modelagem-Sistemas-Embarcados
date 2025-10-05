package Parallel_ACPM;

public class Message
{
	private String source;
	private String target;
	private long bits;

	public Message(String source, String target, long bits)
	{
		this.source = source;
		this.target = target;
		this.bits = bits;
	}
	public String getSource() 
	{
		return source;
	}
	public void setSource(String source) 
	{
		this.source = source;
	}
	public String getTarget() 
	{
		return target;
	}
	public void setTarget(String target) 
	{
		this.target = target;
	}
	public long getBits() 
	{
		return bits;
	}
	public void setBits(long bits) 
	{
		this.bits = bits;
	}
	public String toString() 
	{
		return source + " - " + target + " " + bits;
	}
}
