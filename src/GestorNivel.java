import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class GestorNivel {
    PanelJuego panel;
    public ArrayList<Rectangle> colisionesSuelo = new ArrayList<>();
    public ArrayList<Bloque> bloquesInteractivos = new ArrayList<>();

    // Matriz de 13 filas para soportar el Salto de Fe en el fondo del mapa
    int[][] matrizNivel = new int[13][250];

    BufferedImage imgSuelo, imgTuberia, imgTuberiaCuerpo, imgFondo;

    public GestorNivel(PanelJuego panel) {
        this.panel = panel;

        try {
            if (getClass().getResourceAsStream("/suelo.png") != null) imgSuelo = ImageIO.read(getClass().getResourceAsStream("/suelo.png"));
            if (getClass().getResourceAsStream("/tuberia.png") != null) imgTuberia = ImageIO.read(getClass().getResourceAsStream("/tuberia.png"));
            if (getClass().getResourceAsStream("/tuberia_cuerpo.png") != null) imgTuberiaCuerpo = ImageIO.read(getClass().getResourceAsStream("/tuberia_cuerpo.png"));
            if (getClass().getResourceAsStream("/fondo.png") != null) imgFondo = ImageIO.read(getClass().getResourceAsStream("/fondo.png"));
        } catch (Exception e) { e.printStackTrace(); }

        construirMatrizOriginal();
        generarMundo();
    }

    public void construirMatrizOriginal() {
        for (int c = 0; c < 205; c++) {
            boolean esPozo = (c >= 69 && c <= 70) || (c >= 86 && c <= 88) || (c >= 153 && c <= 154);
            if (!esPozo) { matrizNivel[10][c] = 1; matrizNivel[11][c] = 1; }
        }

        crearTuberia(28, 8); crearTuberia(38, 7); crearTuberia(46, 6);
        crearTuberia(57, 6); crearTuberia(163, 8);

        crearEscaleraSube(134, 4); crearEscaleraBaja(140, 4);
        crearEscaleraSube(148, 5); crearEscaleraBaja(155, 4); crearEscaleraSube(181, 8);


        matrizNivel[5][16] = 3; matrizNivel[5][20] = 4; matrizNivel[5][21] = 3;
        matrizNivel[5][22] = 4; matrizNivel[5][23] = 3; matrizNivel[5][24] = 4;
        matrizNivel[1][22] = 3; matrizNivel[5][77] = 4; matrizNivel[5][78] = 3;
        matrizNivel[5][79] = 4; matrizNivel[5][94] = 4; matrizNivel[5][95] = 4;
        matrizNivel[5][96] = 3; matrizNivel[5][97] = 4; matrizNivel[5][100] = 4;
        matrizNivel[5][101] = 3; matrizNivel[5][102] = 4; matrizNivel[5][106] = 3;
        matrizNivel[5][109] = 4; matrizNivel[5][112] = 4; matrizNivel[5][113] = 3;
        matrizNivel[5][114] = 4; matrizNivel[5][118] = 4; matrizNivel[5][121] = 4;
        matrizNivel[5][122] = 4; matrizNivel[5][129] = 4; matrizNivel[5][130] = 3;
        matrizNivel[5][131] = 4; matrizNivel[5][168] = 4; matrizNivel[5][169] = 4;
        matrizNivel[5][170] = 3; matrizNivel[5][171] = 4;

        // 5. El Subterráneo
        for (int c = 210; c <= 245; c++) {
            matrizNivel[10][c] = 1; matrizNivel[11][c] = 1;
            matrizNivel[0][c] = 1;  matrizNivel[1][c] = 1;
        }
        for (int f = 2; f <= 9; f++) matrizNivel[f][210] = 1;
        for (int f = 2; f <= 9; f++) matrizNivel[f][245] = 1;
        crearEscaleraSube(215, 3); crearEscaleraBaja(219, 3); crearEscaleraSube(226, 5);

        matrizNivel[6][69] = 5; matrizNivel[4][150] = 5; matrizNivel[5][154] = 5;
        matrizNivel[6][138] = 5; matrizNivel[6][139] = 5; // El Softlock

        // Bloques Falsos rotatorios
        matrizNivel[2][81] = 6; matrizNivel[2][82] = 6; matrizNivel[2][83] = 6;
        matrizNivel[2][84] = 6; matrizNivel[2][85] = 6;

        // Los Latigazos del Suelo
        matrizNivel[10][90] = 7; matrizNivel[10][91] = 7; matrizNivel[10][92] = 7;

        // salto de Fe Engañoso
        for(int c = 173; c <= 180; c++) {
            matrizNivel[10][c] = 0; matrizNivel[11][c] = 0;
        }

        matrizNivel[10][175] = 9;
        matrizNivel[10][176] = 9;
    }

    private void crearTuberia(int col, int fila) {
        for (int f = fila; f <= 11; f++) {
            matrizNivel[f][col] = 2; matrizNivel[f][col+1] = 2;
        }
    }

    private void crearEscaleraSube(int c, int h) {
        for (int i=0; i<h; i++) for (int f=9; f>=9-i; f--) matrizNivel[f][c+i] = 1;
    }

    private void crearEscaleraBaja(int c, int h) {
        for (int i=0; i<h; i++) for (int f=9; f>=9-(h-1-i); f--) matrizNivel[f][c+i] = 1;
    }

    public void generarMundo() {
        colisionesSuelo.clear();
        bloquesInteractivos.clear();

        for (int fila = 0; fila < matrizNivel.length; fila++) {
            for (int col = 0; col < matrizNivel[0].length; col++) {
                int tipo = matrizNivel[fila][col];
                int x = col * panel.TAMANO_BLOQUE;
                int y = fila * panel.TAMANO_BLOQUE;

                if (tipo == 1 || tipo == 2) colisionesSuelo.add(new Rectangle(x, y, panel.TAMANO_BLOQUE, panel.TAMANO_BLOQUE));
                else if (tipo == 3) {
                    Bloque b = new BloqueSorpresa(x, y, panel.TAMANO_BLOQUE, panel.TAMANO_BLOQUE);
                    bloquesInteractivos.add(b); colisionesSuelo.add(b.obtenerHitbox());
                }
                else if (tipo == 4) {
                    Bloque b = new BloqueRompible(x, y, panel.TAMANO_BLOQUE, panel.TAMANO_BLOQUE);
                    bloquesInteractivos.add(b); colisionesSuelo.add(b.obtenerHitbox());
                }
                else if (tipo == 5) bloquesInteractivos.add(new BloqueInvisible(x, y, panel.TAMANO_BLOQUE, panel.TAMANO_BLOQUE));
                else if (tipo == 6) {
                    Bloque b = new BloqueFalso(x, y, panel.TAMANO_BLOQUE, panel.TAMANO_BLOQUE);
                    bloquesInteractivos.add(b); colisionesSuelo.add(b.obtenerHitbox());
                }
                else if (tipo == 7) {
                    Bloque b = new BloqueLatigo(x, y, panel.TAMANO_BLOQUE, panel.TAMANO_BLOQUE);
                    bloquesInteractivos.add(b); colisionesSuelo.add(b.obtenerHitbox());
                }
                else if (tipo == 9) {
                    Bloque b = new BloqueInvisibleTrampa(x, y, panel.TAMANO_BLOQUE, panel.TAMANO_BLOQUE);
                    bloquesInteractivos.add(b);
                    colisionesSuelo.add(b.obtenerHitbox());
                }
            }
        }
    }

    public void dibujarFondo(Graphics2D g2d) {
        int limiteExterior = 200 * panel.TAMANO_BLOQUE;

        if (imgFondo != null) {
            int altoDeseado = 10 * panel.TAMANO_BLOQUE + 10;
            int anchoProporcional = (imgFondo.getWidth() * altoDeseado) / imgFondo.getHeight();

            for (int x = 0; x < limiteExterior; x += anchoProporcional) {
                g2d.drawImage(imgFondo, x, 0, anchoProporcional, altoDeseado, null);
            }
        }
    }

    public void dibujar(Graphics2D g2d) {
        dibujarFondo(g2d);

        for (int fila = 0; fila < matrizNivel.length; fila++) {
            for (int col = 0; col < matrizNivel[0].length; col++) {
                int tipo = matrizNivel[fila][col];
                int x = col * panel.TAMANO_BLOQUE;
                int y = fila * panel.TAMANO_BLOQUE;

                if (tipo == 1) {
                    if (imgSuelo != null) {
                        g2d.drawImage(imgSuelo, x, y, panel.TAMANO_BLOQUE, panel.TAMANO_BLOQUE, null);
                    } else {
                        if (col > 205) g2d.setColor(new Color(0, 100, 150));
                        else g2d.setColor(new Color(210, 80, 20));
                        g2d.fillRect(x, y, panel.TAMANO_BLOQUE, panel.TAMANO_BLOQUE);
                        g2d.setColor(Color.BLACK); g2d.drawRect(x, y, panel.TAMANO_BLOQUE, panel.TAMANO_BLOQUE);
                    }
                } else if (tipo == 2) {

                    boolean esLadoIzquierdo = (col < matrizNivel[0].length - 1 && matrizNivel[fila][col+1] == 2 && (col == 0 || matrizNivel[fila][col-1] != 2));

                    if (esLadoIzquierdo) {
                        if (fila > 0 && matrizNivel[fila - 1][col] != 2) {
                            if (imgTuberia != null) {
                                g2d.drawImage(imgTuberia, x, y, panel.TAMANO_BLOQUE * 2, panel.TAMANO_BLOQUE, null);
                            } else {
                                g2d.setColor(new Color(0, 180, 0));
                                g2d.fillRect(x, y, panel.TAMANO_BLOQUE * 2, panel.TAMANO_BLOQUE);
                                g2d.setColor(Color.BLACK); g2d.drawRect(x, y, panel.TAMANO_BLOQUE * 2, panel.TAMANO_BLOQUE);
                            }
                        } else {
                            if (imgTuberiaCuerpo != null) {
                                g2d.drawImage(imgTuberiaCuerpo, x, y, panel.TAMANO_BLOQUE * 2, panel.TAMANO_BLOQUE, null);
                            } else {                           g2d.setColor(new Color(0, 160, 0));
                                g2d.fillRect(x + 6, y, (panel.TAMANO_BLOQUE * 2) - 12, panel.TAMANO_BLOQUE + 1);
                                g2d.setColor(Color.BLACK);
                                g2d.drawLine(x + 6, y, x + 6, y + panel.TAMANO_BLOQUE);
                                g2d.drawLine(x + (panel.TAMANO_BLOQUE * 2) - 6, y, x + (panel.TAMANO_BLOQUE * 2) - 6, y + panel.TAMANO_BLOQUE);
                            }
                        }
                    }
                }
            }
        }
        for (Bloque b : bloquesInteractivos) b.dibujar(g2d);
    }
}