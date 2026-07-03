import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;

public class GestorNivel {

    PanelJuego panel;
    public ArrayList<Rectangle> colisionesSuelo = new ArrayList<>();

    int filas = 12;
    int columnas = 90; // Mapa corto pero intenso

    // Matriz numérica vacía que llenaremos con código
    int[][] mapaPrueba = new int[filas][columnas];

    public GestorNivel(PanelJuego panel) {
        this.panel = panel;
        generarMapa(); // 1. El algoritmo construye el mundo
        generarColisiones();     // 2. Le ponemos físicas a los bloques
    }


    // aquí generamos la lógica para construir el terreno

    public void generarMapa() {
        // Construimos el suelo principal y los pozos
        for (int c = 0; c < columnas; c++) {
            // Definimos usando mateáticas dónde están los pozos
            boolean esPozo = (c >= 12 && c <= 14) || (c >= 35 && c <= 38) || (c >= 60 && c <= 63);

            if (!esPozo) {
                mapaPrueba[10][c] = 1; // superficie del suelo
                mapaPrueba[11][c] = 1; // Suelo profundo
            }
        }

        // creamos tuberías usando nuestro propio método
        crearTuberia(20, 8); // Tubería en la columna 20, empieza en la fila 8 y tiene altura 2
        crearTuberia(45, 7); // Tubería más alta en la columna 45
        crearTuberia(75, 8);

        // construir Escaleras y Plataformas
        //escalera antes del segundo pozo
        mapaPrueba[9][30] = 1;
        mapaPrueba[9][31] = 1;
        mapaPrueba[8][32] = 1;
        mapaPrueba[8][33] = 1;

        // plataforma flotante
        for(int c = 48; c <= 52; c++) {
            mapaPrueba[6][c] = 1;
        }

        //scalera final para atrapar el banderin
        for (int escalon = 0; escalon < 6; escalon++) {
            for (int f = 9; f >= 9 - escalon; f--) {
                mapaPrueba[f][77 + escalon] = 1;
            }
        }


        mapaPrueba[9][85] = 1;
    }

    // Método para apilar bloques de tubería hacia abajo
    private void crearTuberia(int col, int filaInicio) {
        for (int f = filaInicio; f <= 9; f++) {
            mapaPrueba[f][col] = 2; // El 2 representa a la tubería
        }
    }


    //fisicas
    public void generarColisiones() {
        colisionesSuelo.clear();
        for (int fila = 0; fila < filas; fila++) {
            for (int col = 0; col < columnas; col++) {
                if (mapaPrueba[fila][col] == 1 || mapaPrueba[fila][col] == 2) {
                    int x = col * panel.TAMANO_BLOQUE;
                    int y = fila * panel.TAMANO_BLOQUE;
                    colisionesSuelo.add(new Rectangle(x, y, panel.TAMANO_BLOQUE, panel.TAMANO_BLOQUE));
                }
            }
        }
    }

    public void dibujar(Graphics2D g2d) {
        for (int fila = 0; fila < filas; fila++) {
            for (int col = 0; col < columnas; col++) {
                int x = col * panel.TAMANO_BLOQUE;
                int y = fila * panel.TAMANO_BLOQUE;

                // --- LADRILLOS (Figuras limpias) ---
                if (mapaPrueba[fila][col] == 1) {
                    g2d.setColor(new Color(210, 80, 20));
                    g2d.fillRect(x, y, panel.TAMANO_BLOQUE, panel.TAMANO_BLOQUE);

                    g2d.setColor(Color.BLACK);
                    g2d.drawRect(x, y, panel.TAMANO_BLOQUE, panel.TAMANO_BLOQUE);

                    g2d.setColor(new Color(170, 50, 10));
                    g2d.fillRect(x + 8, y + 8, panel.TAMANO_BLOQUE - 16, panel.TAMANO_BLOQUE - 16);
                }
                // --- TUBERÍAS (Superposición de figuras) ---
                else if (mapaPrueba[fila][col] == 2) {
                    g2d.setColor(new Color(0, 160, 0));
                    g2d.fillRect(x + 4, y, panel.TAMANO_BLOQUE - 8, panel.TAMANO_BLOQUE);
                    g2d.setColor(Color.BLACK);
                    g2d.drawRect(x + 4, y, panel.TAMANO_BLOQUE - 8, panel.TAMANO_BLOQUE);

                    // La tapa de la tubería (Solo se dibuja en el bloque superior)
                    if (fila > 0 && mapaPrueba[fila - 1][col] != 2) {
                        g2d.setColor(new Color(0, 180, 0));
                        g2d.fillRect(x, y, panel.TAMANO_BLOQUE, 20);
                        g2d.setColor(Color.BLACK);
                        g2d.drawRect(x, y, panel.TAMANO_BLOQUE, 20);
                    }
                }
            }
        }
    }
}