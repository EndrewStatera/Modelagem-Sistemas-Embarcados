import java.awt.image.BufferedImage;
import java.io.IOException;

public class CentralProcessor extends Thread
{
	private MPSoC mpsoc;
	private Processor pe[][];
	private int processesStarted;

	public CentralProcessor(MPSoC mpsoc, Processor pe[][])
	{
		this.mpsoc = mpsoc;
		this.pe = pe;
		processesStarted = 0;
	}
	public void run()
	{
		System.out.println("Start CP");

		BufferedImage img = null;

		try {
			img = javax.imageio.ImageIO.read(new java.io.File("image-test.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		for(int i = 0; i < pe.length; i++)
		{
			for(int j = 0; j < pe[0].length; j++)
			{
				try {
					initializeProcessor(i, j, img);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				processesStarted++;
			}
		}
		// System.out.println("Waiting: " + processesStarted);

		// Executa processos

		// Espera processos terminarem e mandarem sinal
		try
		{
			while(processesStarted > 0) 
			{
				Thread.sleep(500);
			}
		}	
		catch(InterruptedException e) 
		{ 
			notifyAll(); 
		}

		for(int i = 0; i < pe.length; i++)
		{
			for(int j = 0; j < pe[0].length; j++)
			{
				try {
					findObjects(i, j);
				} catch (InterruptedException e) {
					notifyAll(); 
				}
				processesStarted++;
			}
		}

		// Espera processos terminarem e mandarem sinal
		try
		{
			while(processesStarted > 0)
			{
				Thread.sleep(500);
				System.out.println(processesStarted);
			}
		}	
		catch(InterruptedException e) 
		{ 
			notifyAll(); 
		}

		for(int i = 0; i < pe.length; i++)
		{
			for(int j = 0; j < pe[0].length; j++)
			{
				checkNeighbors(i, j);
				processesStarted++;
			}
		}

		// Espera processos terminarem e mandarem sinal
		try
		{
			while(processesStarted > 0)
			{
				Thread.sleep(500);
				System.out.println(processesStarted);
			}
		}	
		catch(InterruptedException e) 
		{ 
			notifyAll(); 
		}

		System.out.println(mpsoc);
		System.out.println("Ending CP");
		Thread.yield();
	}

	// Recebe de outros processadores quando terminam
	public synchronized void endComputationSignal()
	{
		System.out.println("Ended");
		processesStarted--;
	}

	public synchronized void initializeProcessor(int index1, int index2, BufferedImage image) throws InterruptedException
	{
		// Dividir corretamente entre processadores, talvez usando BufferedImage crop = image.getSubimage(XStart,YStart, XEnd, YEnd)
		// Caso use esse crop, verificar que os pixels locais (de cada processador) sejam transformados em globais (imagem inteira)

		int cropHeight = image.getHeight() / pe.length;
		int cropWidth = image.getWidth() / pe[0].length;
		int cropYBegin = cropHeight * index1;
		int cropXBegin = cropWidth * index2;
		// int cropYEnd = cropYBegin + cropHeight - 1;
		// int cropXEnd = cropXBegin + cropWidth - 1;

		BufferedImage crop = image.getSubimage(cropXBegin, cropYBegin, cropWidth, cropHeight);
		
		pe[index1][index2].initialize(crop, cropWidth, cropHeight, cropXBegin, cropYBegin);
	}

	public synchronized void findObjects(int index1, int index2) throws InterruptedException
	{
		// Chama processadores para encontrarem objetos
		pe[index1][index2].findObjects();
	}

	public synchronized void checkNeighbors(int index1, int index2)
	{
		// Chama processadores para verificarem seus vizinhos
		pe[index1][index2].checkNeighbors();
	}

	public String toString()
	{
		return "\nCP";
	}
}
