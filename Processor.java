import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Processor extends Thread
{
	private ROSMI_Parallel rosmi;
	private CentralProcessor cp;
	private int index;
	private int imageXStart, imageYStart;
	private ArrayList<ImageObject> objects;
	// private boolean endFindingObjects = false;
	// private boolean endCheckingNeighbors = false;

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
	}

	public synchronized void initialize(BufferedImage image, int pictureWidth, int pictureHeight, int xStart, int yStart) throws InterruptedException
	{
		imageXStart = xStart;
		imageYStart = yStart;
		System.out.println("ReceiveAll PE" + index);		
		rosmi.initialize(image, pictureWidth, pictureHeight);
		cp.endComputationSignal();
	}

	public synchronized void findObjects() throws InterruptedException
	{
		System.out.println("PE " + index + " finding objects");
		objects = rosmi.findObjects();
		for (int i = 0; i < objects.size(); i++)
		{
			for (int j = 0; j < objects.get(i).edgePixels.size(); j++)
			{
				objects.get(i).edgePixels.get(j).x = objects.get(i).edgePixels.get(j).x + imageXStart;
				objects.get(i).edgePixels.get(j).y = objects.get(i).edgePixels.get(j).y + imageYStart;
			}
		}
		System.out.println(objects.size());
		cp.endComputationSignal();
	}

	public synchronized ArrayList<ImageObject> checkNeighbors(ArrayList<ImageObject> receivedObjects)
	{
		System.out.println("PE " + index + " checking neighbors");

		// Compara telas vizinhas
		for (int i = 0; i < objects.size(); i++)
		{
			ImageObject object = objects.get(i);
			for (int j = 0; j < receivedObjects.size(); j++)
			{
				ImageObject receivedObject = receivedObjects.get(j);
				
				if (rosmi.isConnected(object, receivedObject))
				{
					for (int k = 0; k < receivedObject.edgePixels.size(); k++)
					{
						object.edgePixels.add(receivedObject.edgePixels.get(k));
					}
					receivedObjects.remove(j);
				}
			}
		}

		System.out.println("Ending PE" + index);
		cp.endComputationSignal();
		objects.addAll(receivedObjects);

		return objects;
	}
}
