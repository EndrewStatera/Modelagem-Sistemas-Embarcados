package Parallel_ACPM;

import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;

public class ROSMI_Parallel {
    ArrayList<ImageObject> objects;
    boolean[][] visited;

    BufferedImage image;
    
    int pictureXEnd;
    int pictureYEnd;
    int pictureCount;

    // 8 direções de possíveis pixels vizinhos
    private static final int[][] DIRS = {
        {1,0}, {-1,0}, {0,1}, {0,-1},   // leste, oeste, sul, norte
        {1,1}, {1,-1}, {-1,1}, {-1,-1}  // sudeste, nordeste, sudoeste, noroeste
    };

    public ROSMI_Parallel()
    {
        objects = new ArrayList<ImageObject>();
    }

    public void initialize(BufferedImage _image, int _pictureWidth, int _pictureHeight)
    {
        pictureXEnd = _pictureWidth - 1;
        pictureYEnd = _pictureHeight - 1;
        pictureCount = 0;
        visited = new boolean[_pictureHeight][_pictureWidth];
        image = _image;

        objects = new ArrayList<ImageObject>();
    }

    // Função que busca pixels vizinhos preenchidos (não-brancos) para formar uma figura fechada
    private ImageObject findNeighborPixels(int startX, int startY) {
        Queue<int[]> q = new ArrayDeque<>();    // Queue frontier
        q.add(new int[]{startX, startY});
        visited[startY][startX] = true;         // Marca primeiro pixel não-branco recebido como parâmetro como visitado

        ImageObject object = new ImageObject();

        // Se está em uma borda, adiciona ao objeto para futura comparação
        if (startX == 0 || startX == pictureXEnd || startY == 0 || startY == pictureYEnd)
        {
            object.edgePixels.add(new Pair(startX, startY));
        }

        while (!q.isEmpty()) {
            int[] p = q.poll();                 // Recebe e deleta head do queue
            int x = p[0], y = p[1];             // Tupla (X, Y)

            // Procura vizinhos em todas as 8 direções por BFS com um queue frontier
            // Para todo pixel vizinho não-branco, adiciona à figura
            for (int[] dir : DIRS) {
                int nx = x + dir[0];
                int ny = y + dir[1];

                // Se está dentro das coordenadas válidas da figura
                if (nx >= 0 && nx <= pictureXEnd && ny >= 0 && ny <= pictureYEnd) {
                    // Se ainda não foi incluído na lista de visitados
                    if (!visited[ny][nx]) {
                        int color = image.getRGB(nx, ny) & 0xFFFFFF;

                        // E não é branco
                        if (color < 0xFFFFFF) {
                            // Se está em uma borda, adiciona ao objeto para futura comparação
                            if (nx == 0 || nx == pictureXEnd || ny == 0 || ny == pictureYEnd) {
                                object.edgePixels.add(new Pair(nx, ny));
                            }

                            visited[ny][nx] = true;
                            q.add(new int[]{nx, ny});
                        }
                    }
                }
            }
        }

        return object;
    }

    // Procura por figuras (objetos) na imagem e retorna um TAD que inclui pixels que se encontram na borda (caso existam)
    public ArrayList<ImageObject> findObjects() {
        // Visita cada pixel da imagem
        for (int y = 0; y < pictureYEnd; y++) {
            for (int x = 0; x < pictureXEnd; x++) {
                // Recebe valor RGB no ponto (x,y) e avalia por bitmask (Preto = 0 (0x000000), Branco = 16777215 (0xFFFFFF))
                int color = image.getRGB(x, y) & 0xFFFFFF;
                // Se o pixel não for branco
                if (color < 0xFFFFFF && !visited[y][x]) { // pixel não-branco ainda não visitado
                    objects.add(findNeighborPixels(x, y));
                }
            }
        }
        visited = null;
        return objects;
    }

    public boolean isConnected(ImageObject object1, ImageObject object2)
    {
        // Verifica se duas figuras estão conectadas pelos seus pixels de borda
        for (int i = 0; i < object1.edgePixels.size(); i++)
        {
            Pair object1EdgePixels = object1.edgePixels.get(i);
            for (int j = 0; j < object2.edgePixels.size(); j++)
            {
                Pair object2EdgePixels = object2.edgePixels.get(j);

                for (int[] dir : DIRS)
                {
                    if (object1EdgePixels.x == object2EdgePixels.x + dir[0] &&
                        object1EdgePixels.y == object2EdgePixels.y + dir[1])
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void main(String[] args) throws Exception {
        BufferedImage img = javax.imageio.ImageIO.read(new java.io.File("image-test.png"));

        ROSMI_Parallel rosmi = new ROSMI_Parallel();
        rosmi.initialize(img, 3072, 1536);

        rosmi.findObjects();

        System.out.println("Número de figuras detectadas = " + rosmi.objects.size());
    }
}
