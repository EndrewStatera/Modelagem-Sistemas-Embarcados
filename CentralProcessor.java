import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public class CentralProcessor extends Thread
{
	private MPSoC mpsoc;
	private Processor pe[];
	private int processesStarted;
	private ArrayList<ImageObject> objects;

	int n, m;

	public CentralProcessor(MPSoC mpsoc, Processor pe[], int n, int m)
	{
		this.mpsoc = mpsoc;
		this.pe = pe;
		this.n = n;
		this.m = m;
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
			try {
				initializeProcessor(i, img);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			processesStarted++;
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
			try {
				findObjects(i);
			} catch (InterruptedException e) {
				notifyAll(); 
			}
			processesStarted++;
		}

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

		// Pede para processadores verificarem por figuras vizinhas (conectadas)
		for(int i = 0; i < pe.length; i++)
		{
			checkNeighbors(i);
			processesStarted++;

			// Espera aqui para esperar cada processo se comunicar com outros e evitar race condition
			while (processesStarted > 0);
		}

		System.out.println("Número de figuras detectadas = " + objects.size());
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

	public synchronized void initializeProcessor(int index, BufferedImage image) throws InterruptedException
	{
		// Faz crop da imagem e envia para cada processador
		// Depois receberá o valor local e transformará para global
		int cropHeight = image.getHeight() / n;
		int cropWidth = image.getWidth() / m;
		int cropYBegin = cropHeight * (index / m);
		int cropXBegin = cropWidth * (index % m);

		BufferedImage crop = image.getSubimage(cropXBegin, cropYBegin, cropWidth, cropHeight);
		
		pe[index].initialize(crop, cropWidth, cropHeight, cropXBegin, cropYBegin);
	}

	public synchronized void findObjects(int index) throws InterruptedException
	{
		// Chama processadores para encontrarem objetos
		pe[index].findObjects();
	}

	public synchronized void checkNeighbors(int index)
	{
		// Chama processadores para verificarem seus vizinhos com a lista de objetos total
		objects = pe[index].checkNeighbors(objects);
	}

	public String toString()
	{
		return "\nCP";
	}
}
