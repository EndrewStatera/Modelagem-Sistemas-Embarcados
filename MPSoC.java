public class MPSoC
{
	int n = 2;
	int m = 3;

	private Processor pe[][] = new Processor[n][m];
	private CentralProcessor cp = new CentralProcessor(this, pe);

	public MPSoC()
	{
		int count = 0;
		for(int i = 0; i < pe.length; i++)
			for(int j = 0; j < pe[0].length; j++)
			{
				pe[i][j] = new Processor(count, cp);
				System.out.println(count);
				count++;
			}
		cp.start();
		count = 0;
		for(int i = 0; i < pe.length; i++)
			for(int j = 0; j < pe[0].length; j++)
				pe[i][j].start();
				count++;
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