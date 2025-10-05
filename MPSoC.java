public class MPSoC
{
	int n = 2;
	int m = 3;

	private Processor pe[] = new Processor[n*m];
	private CentralProcessor cp = new CentralProcessor(this, pe, n, m);

	public MPSoC()
	{
		for(int i = 0; i < pe.length; i++)
		{
			pe[i] = new Processor(i, cp);
		}
		cp.start();
		for(int i = 0; i < pe.length; i++)
			pe[i].start();
	}
	public String toString()
	{
		String str = "" + cp;
		for(int index = 0; index < pe.length; index++)
			str = str + pe[index];
		return str;
	}
	public static void main(String[] args)
	{
		new MPSoC();
	}
}