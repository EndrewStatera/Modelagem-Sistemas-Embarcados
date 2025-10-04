import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.Queue;

public class ROSMI_Sequential {

    // 8 direções de possíveis pixels vizinhos
    private static final int[][] DIRS = {
        {1,0}, {-1,0}, {0,1}, {0,-1},   // leste, oeste, sul, norte
        {1,1}, {1,-1}, {-1,1}, {-1,-1}  // sudeste, nordeste, sudoeste, noroeste
    };

    public static int countObjects(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        boolean[][] visited = new boolean[height][width];
        int count = 0;

        // Visita cada pixel da imagem
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Recebe valor RGB no ponto (x,y) e avalia por bitmask (Preto = 0 (0x000000), Branco = 16777215 (0xFFFFFF))
                int color = image.getRGB(x, y) & 0xFFFFFF;
                // Se o pixel não for branco
                if (color < 0xFFFFFF && !visited[y][x]) { // pixel não-branco ainda não visitado
                    findNeighborPixels(image, visited, x, y);
                    count++;
                }
            }
        }
        return count;
    }

    // Função que busca pixels vizinhos preenchidos (não-brancos) para formar uma figura fechada
    private static void findNeighborPixels(BufferedImage image, boolean[][] visited, int startX, int startY) {
        int width = image.getWidth();
        int height = image.getHeight();
        Queue<int[]> q = new ArrayDeque<>();    // Queue frontier
        q.add(new int[]{startX, startY});
        visited[startY][startX] = true;         // Marca primeiro pixel não-branco recebido como parâmetro como visitado

        while (!q.isEmpty()) {
            int[] p = q.poll();                 // Recebe e deleta head do queue
            int x = p[0], y = p[1];             // Tupla (X, Y)

            // Procura vizinhos em todas as 8 direções por BFS com um queue frontier
            // Para todo pixel vizinho não-branco, adiciona à figura
            for (int[] dir : DIRS) {
                int nx = x + dir[0];
                int ny = y + dir[1];

                // Se está dentro das coordenadas válidas da figura
                if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                    // Se ainda não foi incluído na lista de visitados
                    if (!visited[ny][nx]) {
                        int color = image.getRGB(nx, ny) & 0xFFFFFF;
                        // E não é branco
                        if (color < 0xFFFFFF) {
                            visited[ny][nx] = true;
                            q.add(new int[]{nx, ny});
                        }
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        BufferedImage img = javax.imageio.ImageIO.read(new java.io.File("image-test.png"));

        int numObjects = countObjects(img);
        System.out.println("Número de figuras detectadas = " + numObjects);
    }
}
