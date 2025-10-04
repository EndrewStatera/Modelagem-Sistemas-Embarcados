import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;

public class ROSMI_Parallel {
    ArrayList<ImageObject> objects;
    boolean[][] visited;

    BufferedImage image;
    
    int pictureWidthEnd;
    int pictureHeightEnd;
    int pictureXOrigin;
    int pictureYOrigin;
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

    public void initialize(BufferedImage _image, int _pictureXOrigin, int _pictureYOrigin, int _pictureWidthEnd, int _pictureHeightEnd)
    {
        pictureWidthEnd = _pictureWidthEnd;
        pictureHeightEnd = _pictureHeightEnd;
        pictureXOrigin = _pictureXOrigin;
        pictureYOrigin = _pictureYOrigin;
        pictureCount = 0;
        visited = new boolean[pictureHeightEnd - pictureYOrigin + 1][pictureWidthEnd - pictureXOrigin + 1];
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
        if (startX == pictureXOrigin || startX == pictureWidthEnd || startY == pictureYOrigin || startY == pictureHeightEnd)
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
                if (nx >= pictureXOrigin && nx <= pictureWidthEnd && ny >= pictureYOrigin && ny <= pictureHeightEnd) {
                    // Se ainda não foi incluído na lista de visitados
                    if (!visited[ny][nx]) {
                        int color = image.getRGB(nx, ny) & 0xFFFFFF;

                        // E não é branco
                        if (color < 0xFFFFFF) {
                            // Se está em uma borda, adiciona ao objeto para futura comparação
                            if (nx == pictureXOrigin || nx == pictureWidthEnd || ny == pictureYOrigin || ny == pictureHeightEnd) {
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
    public void findObjects() {
        // Visita cada pixel da imagem
        for (int y = pictureYOrigin; y < pictureHeightEnd; y++) {
            for (int x = pictureXOrigin; x < pictureWidthEnd; x++) {
                // System.out.println(x + " " + y);
                // Recebe valor RGB no ponto (x,y) e avalia por bitmask (Preto = 0 (0x000000), Branco = 16777215 (0xFFFFFF))
                int color = image.getRGB(x, y) & 0xFFFFFF;
                // Se o pixel não for branco
                if (color < 0xFFFFFF && !visited[y][x]) { // pixel não-branco ainda não visitado
                    objects.add(findNeighborPixels(x, y));
                }
            }
        }
        visited = null;
    }

    public boolean isConnected(int pixelX, int pixelY)
    {
        // Verifica se está conectado
        return true;
    }

    public static void main(String[] args) throws Exception {
        BufferedImage img = javax.imageio.ImageIO.read(new java.io.File("dsadsadsa.png"));

        ROSMI_Parallel rosmi = new ROSMI_Parallel();
        rosmi.initialize(img, 0, 0, 3071, 1535);

        rosmi.findObjects();

        System.out.println("Número de figuras detectadas = " + rosmi.objects.size());

        // for (int i = 0; i < rosmi.objects.size(); i++)
        // {
        //     for (int j = 0; j < rosmi.objects.get(i).edgePixels.size(); j++)
        //     {
        //         System.out.println(rosmi.objects.get(i).edgePixels.get(j).x + " " + rosmi.objects.get(i).edgePixels.get(j).y);
        //     }
        // }
    }
}
