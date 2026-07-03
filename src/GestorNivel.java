import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;

public class GestorNivel {

    PanelJuego panel;
    public ArrayList<Rectangle> colisionesSuelo = new ArrayList<>();

    // ========================================================================
    // MAPA DEL NIVEL 1-1 COMPLETO (211 columnas de ancho)
    // ========================================================================
    String[] mapaTexto = {
            "                                                                                                                                                                                                                   ",
            "                                                                                                                                                                                                                   ",
            "                                                                                                                                                                                            XX                     ",
            "                                                                                                                                                                                           XXX                     ",
            "                                                                                                                                                                                          XXXX                     ",
            "                                                                                                                                                                                         XXXXX                     ",
            "                                              TT                                                                                         XX  X                                          XXXXXX                     ",
            "                                      TT      TT         TT                                                                             XXX  XX                                        XXXXXXX                     ",
            "                            TT        TT      TT         TT                                                                            XXXX  XXX                                      XXXXXXXX                     ",
            "                            TT        TT      TT         TT                                                                           XXXXX  XXXX                                    XXXXXXXXX                     ",
            "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX  XXXXXXXXXXXXXXX   XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX  XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX",
            "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX  XXXXXXXXXXXXXXX   XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX  XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
    };

    int filas = mapaTexto.length;
    int columnas = mapaTexto[0].length();

    // La matriz numérica se generará automáticamente a partir del texto
    int[][] mapaPrueba = new int[filas][columnas];

    public GestorNivel(PanelJuego panel) {
        this.panel = panel;
        cargarMapaDesdeTexto(); // 1. Convertimos el texto a números
        generarColisiones();    // 2. Creamos los rectángulos físicos
    }

    // Traduce el diseño de texto a la matriz numérica que usa el juego
    public void cargarMapaDesdeTexto() {
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                char letra = mapaTexto[f].charAt(c);

                if (letra == 'X') {
                    mapaPrueba[f][c] = 1; // 1 = Ladrillo sólido
                } else if (letra == 'T') {
                    mapaPrueba[f][c] = 2; // 2 = Tubería sólida
                } else {
                    mapaPrueba[f][c] = 0; // 0 = Aire / Cielo
                }
            }
        }
    }

    public void generarColisiones() {
        colisionesSuelo.clear();
        for (int fila = 0; fila < filas; fila++) {
            for (int col = 0; col < columnas; col++) {
                // Tanto los ladrillos (1) como las tuberías (2) son sólidos
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

                // --- DIBUJAR LADRILLOS ---
                if (mapaPrueba[fila][col] == 1) {
                    Color colorFondoLadrillo = new Color(200, 76, 12);
                    Color colorSombra = new Color(110, 30, 0);
                    Color colorBrillo = new Color(255, 180, 140);

                    g2d.setColor(colorFondoLadrillo);
                    g2d.fillRect(x, y, panel.TAMANO_BLOQUE, panel.TAMANO_BLOQUE);
                    g2d.setColor(colorSombra);
                    g2d.drawRect(x, y, panel.TAMANO_BLOQUE, panel.TAMANO_BLOQUE);

                    g2d.drawLine(x, y + panel.TAMANO_BLOQUE/2, x + panel.TAMANO_BLOQUE, y + panel.TAMANO_BLOQUE/2);
                    g2d.drawLine(x + panel.TAMANO_BLOQUE/2, y, x + panel.TAMANO_BLOQUE/2, y + panel.TAMANO_BLOQUE/2);

                    g2d.setColor(colorBrillo);
                    g2d.drawLine(x + 1, y + 1, x + panel.TAMANO_BLOQUE - 2, y + 1);
                    g2d.drawLine(x + 1, y + 1, x + 1, y + panel.TAMANO_BLOQUE/2 - 1);
                }
                // --- DIBUJAR TUBERÍAS ---
                else if (mapaPrueba[fila][col] == 2) {
                    Color colorTuberia = new Color(0, 180, 0); // Verde clásico
                    Color colorBrilloTuberia = new Color(100, 255, 100);
                    Color colorSombraTuberia = new Color(0, 100, 0);

                    // Cuerpo de la tubería
                    g2d.setColor(colorTuberia);
                    g2d.fillRect(x, y, panel.TAMANO_BLOQUE, panel.TAMANO_BLOQUE);

                    // Contorno
                    g2d.setColor(Color.BLACK);
                    g2d.drawRect(x, y, panel.TAMANO_BLOQUE, panel.TAMANO_BLOQUE);

                    // Detalle de iluminación (Efecto de tubo cilíndrico)
                    g2d.setColor(colorBrilloTuberia);
                    g2d.fillRect(x + 5, y, 10, panel.TAMANO_BLOQUE);

                    // Detalle de sombra lateral
                    g2d.setColor(colorSombraTuberia);
                    g2d.fillRect(x + panel.TAMANO_BLOQUE - 10, y, 10, panel.TAMANO_BLOQUE);
                }
            }
        }
    }
}