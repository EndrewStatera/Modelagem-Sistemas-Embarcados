package Parallel_ACPM;
import java.util.*;

public class DiscreteEvent
{
	public static ArrayList<Event> eventList = new ArrayList<Event>();
	public static long startTime = System.currentTimeMillis();

	public static boolean add(String source, String target, long bits)
	{
		return addMessage(new Message(source, target, bits));
	}
	public static boolean addMessage(Message message)
	{
		long time = System.currentTimeMillis() - startTime;
		return addEvent(new Event(time, message));
	}
	public static boolean addEvent(Event event)
	{
		return eventList.add(event);
	}
	public static String showEventList()
	{
		int count = 0;
		String str = "";
		for(Event e: eventList)
		{
			str = str + "\n"+ count + " " + e;
			count++;
		}
		return str;
	}
}