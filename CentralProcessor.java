import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public class CentralProcessor extends Thread
{
	private MPSoC mpsoc;
	private Processor pe[][];
	private int processesStarted;
	private ArrayList<ImageObject> objects;

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
		objects = new ArrayList<ImageObject>();

		// Carrega imagem no processador central
		try {
			img = javax.imageio.ImageIO.read(new java.io.File("image-test.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Inicializa todos processadores
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
		// Espera processadores terminarem e mandarem sinal
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

		// Chama processadores para encontrarem figuras em sua parte da imagem
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

		// Pede para processadores verificarem por figuras vizinhas (conectadas)
		for(int i = 0; i < pe.length; i++)
		{
			for(int j = 0; j < pe[0].length; j++)
			{
				checkNeighbors(i, j);
				processesStarted++;

				// Espera aqui para esperar cada processo se comunicar com outros e evitar race condition
				while (processesStarted > 0);
				
			}
		}

		System.out.println(objects.size());
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
		// Faz crop da imagem e envia para cada processador
		// Depois receberá o valor local e transformará para global
		int cropHeight = image.getHeight() / pe.length;
		int cropWidth = image.getWidth() / pe[0].length;
		int cropYBegin = cropHeight * index1;
		int cropXBegin = cropWidth * index2;

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
		// Chama processadores para verificarem seus vizinhos com a lista de objetos total
		objects = pe[index1][index2].checkNeighbors(objects);
	}

	public String toString()
	{
		return "\nCP";
	}
}
