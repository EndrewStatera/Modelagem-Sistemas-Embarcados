import java.awt.*;
import java.awt.image.BufferedImage;

public class Processor extends Thread
{
	private ROSMI_Parallel rosmi;
	private CentralProcessor cp;
	private int index;
	private int imageWidthStart, imageWidthEnd;
	private boolean startImageComputation = false;
	private boolean startCheckNeighbors = false;

	public Processor(int index, CentralProcessor cp)
	{
		this.cp = cp;
		this.index = index;
		rosmi = new ROSMI_Parallel();
	}
	public void run()
	{
		System.out.println("Start PE" + index);
		Thread.yield();
		while(!startImageComputation);
		System.out.println("ReceiveAll PE" + index);	
		
		// Envia para o CentralProcessor que terminou
		cp.receiveObjects(null);

		checkNeighbors();
		while(!startCheckNeighbors);
		// Envia para o CentralProcessor que terminou
		cp.receiveObjects(null);

		sendObjectCount();
		
		System.out.println("Ending PE" + index);
		Thread.yield();
	}

	public synchronized void initialize(BufferedImage image, int pictureXOrigin, int pictureYOrigin, int pictureWidthEnd, int pictureHeightEnd) throws InterruptedException
	{
		startImageComputation = true;
		rosmi.initialize(image, pictureXOrigin, pictureYOrigin, pictureWidthEnd, pictureHeightEnd);
		startImageComputation = false;
	}

	public synchronized void sendObjectCount()
	{
		// Envia contagem de objetos da tela para central processor
	}	

	public synchronized void checkNeighbors()
	{
		startCheckNeighbors = true;
		// Compara telas vizinhas
		startCheckNeighbors = false;
	}
}
