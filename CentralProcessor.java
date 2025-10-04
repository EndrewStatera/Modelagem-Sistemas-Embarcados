import java.awt.image.BufferedImage;
import java.io.IOException;

public class CentralProcessor extends Thread
{
	private ROSMI_Parallel rosmi;	// Talvez não seja necessário
	private MPSoC mpsoc;
	private Processor pe[][];
	private int processesStarted;

	public CentralProcessor(MPSoC mpsoc, Processor pe[][])
	{
		this.mpsoc = mpsoc;
		this.pe = pe;
		processesStarted = 0;
		rosmi = new ROSMI_Parallel();
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
				} catch (InterruptedException e) {}
				processesStarted++;
			}
		}

		// Executa processos

		// Espera processos inicializarem
		while(processesStarted > 0)

		for(int i = 0; i < pe.length; i++)
		{
			for(int j = 0; j < pe[0].length; j++)
			{
				checkNeighbors();
				processesStarted++;
			}
		}

		while(processesStarted > 0)

		System.out.println(mpsoc);
		System.out.println("Ending CP");
		Thread.yield();
	}

	// Recebe de outros processadores quando terminam
	public synchronized void receiveObjects(ImageObject objects)
	{
		processesStarted--;
	}

	public synchronized void initializeProcessor(int index1, int index2, BufferedImage image) throws InterruptedException
	{
		// Dividir corretamente entre processadores, talvez usando BufferedImage crop = image.getSubimage(XStart,YStart, XEnd, YEnd)
		// Caso use esse crop, verificar que os pixels locais (de cada processador) sejam transformados em globais (imagem inteira)
		
		// pe[index1][index2].initialize(image, 0, 0, pictureWidthEnd, pictureHeightEnd);
	}
	public synchronized void checkNeighbors()
	{
		// Chama processadores para verificarem seus vizinhos
	}

	public String toString()
	{
		return "\nCP";
	}
}
