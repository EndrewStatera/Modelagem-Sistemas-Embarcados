import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.Queue;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.imageio.spi.IIORegistry;

public class ContourCounter {

    // 8-direções (para considerar diagonais também, se quiser só 4, tire as diagonais)
    private static final int[][] DIRS = {
        {1,0}, {-1,0}, {0,1}, {0,-1},
        {1,1}, {1,-1}, {-1,1}, {-1,-1}
    };

    public static int countContours(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        boolean[][] visited = new boolean[height][width];
        int count = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int color = image.getRGB(x, y) & 0xFF; // pega intensidade (grayscale)
                if (color == 0 && !visited[y][x]) { // pixel preto ainda não visitado
                    bfs(image, visited, x, y);
                    count++;
                }
            }
        }
        return count;
    }

    private static void bfs(BufferedImage image, boolean[][] visited, int startX, int startY) {
        int width = image.getWidth();
        int height = image.getHeight();
        Queue<int[]> q = new ArrayDeque<>();
        q.add(new int[]{startX, startY});
        visited[startY][startX] = true;

        while (!q.isEmpty()) {
            int[] p = q.poll();
            int x = p[0], y = p[1];
            for (int[] d : DIRS) {
                int nx = x + d[0];
                int ny = y + d[1];
                if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                    if (!visited[ny][nx]) {
                        int color = image.getRGB(nx, ny) & 0xFF;
                        if (color == 0) { // preto = contorno
                            visited[ny][nx] = true;
                            q.add(new int[]{nx, ny});
                        }
                    }
                }
            }
        }
    }

    // Exemplo de uso
    public static void main(String[] args) throws Exception {
        //javax.imageio.ImageIO imgIO = IIORegistry.getDefaultInstance().
        BufferedImage img = javax.imageio.ImageIO.read(new java.io.File("image-test.png"));

        int numObjects = countContours(img);
        System.out.println("Número de figuras detectadas = " + numObjects);
    }
}
