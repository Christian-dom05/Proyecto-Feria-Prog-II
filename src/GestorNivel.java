import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.imageio.ImageIO;

/**
 * Clase que se encarga de traducir la matriz bidimensional de enteros (matrizNivel) que representa el mapa
 * a objetos físicos en el juego como los bloques y tuberias y fondo,
 * instanciando los hitboxes invisibles para las paredes y agregando los bloques
 * especiales a una lista.
 * Cada número representa un objeto:
 * 1: suelo/pared
 * 2: Tubería
 * 3: Bloque Sorpresa
 * 4: Bloque Rompible
 * 5: Bloque Invisible
 * 6: Bloque Falso (que cae)
 * 7: Bloque Látigo (trampa)
 * 9: Bloque Invisible
 */
public class GestorNivel {
    PanelJuego panel;
    public ArrayList<Rectangle> colisionesSuelo = new ArrayList<>();
    public ArrayList<Bloque> bloquesInteractivos = new ArrayList<>();

    // Matriz de 13 filas para soportar el Salto de Fe en el fondo del mapa
    int[][] matrizNivel = new int[13][250];

    BufferedImage imgSuelo, imgTuberia, imgTuberiaCuerpo, imgFondo;

    public GestorNivel(PanelJuego panel) {
        this.panel = panel;

        // aquí le asignamos las imágenes a cada objeto del juego
        try {
            if (getClass().getResourceAsStream("/suelo.png") != null) {
                imgSuelo = ImageIO.read(getClass().getResourceAsStream("/suelo.png"));
            }
            if (getClass().getResourceAsStream("/tuberia.png") != null) {
                imgTuberia = ImageIO.read(getClass().getResourceAsStream("/tuberia.png"));
            }
            if (getClass().getResourceAsStream("/tuberia_cuerpo.png") != null) {
                imgTuberiaCuerpo = ImageIO.read(getClass().getResourceAsStream("/tuberia_cuerpo.png"));
            }
            if (getClass().getResourceAsStream("/fondo.png") != null) {
                imgFondo = ImageIO.read(getClass().getResourceAsStream("/fondo.png"));
            }
        } catch (Exception e) { e.printStackTrace(); }

        construirMatrizOriginal(); // método para generar la estructura del mapa
        generarMundo(); // método para crear el mundo
    }

    // en esta matriz llenamos de números. Cada número representa un objeto, así creamos el mapa
    public void construirMatrizOriginal() {
        // recorremos todo el ancho del mapa
        for (int c = 0; c < 205; c++) {
            // hacemos que la variable esPozo sea true en estos rangos de valores. o sea, colocamos pozos
            boolean esPozo = (c >= 69 && c <= 70) || (c >= 86 && c <= 88) || (c >= 153 && c <= 154);
            // si esPozo es true, entonces no dibujamos nada, o sea que generamos pozos
            if (!esPozo) {
                // dibujamos 2 filas de bloques, 1 es el valor que representa a los bloques
                matrizNivel[10][c] = 1;
                matrizNivel[11][c] = 1;
            }
        }

        // creamos tuberías en esas coordenadas de la matriz
        crearTuberia(28, 8);
        crearTuberia(38, 7);
        crearTuberia(46, 6);
        crearTuberia(57, 6);
        crearTuberia(163, 8);

        // creamos escaleras de subidas y bajas en esas coordenadas de la matriz
        crearEscaleraSube(134, 4);
        crearEscaleraBaja(140, 4);
        crearEscaleraSube(148, 5);
        crearEscaleraBaja(155, 4);
        crearEscaleraSube(181, 8);

        // ponemos diversos objetos en varias coordenadas de la matriz
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

        // ----nivel subterráneo----
        for (int c = 210; c <= 245; c++) {
            // creamos bloques
            matrizNivel[10][c] = 1;
            matrizNivel[11][c] = 1;
            matrizNivel[0][c] = 1;
            matrizNivel[1][c] = 1;
        }

        for (int f = 2; f <= 9; f++) matrizNivel[f][210] = 1;
        for (int f = 2; f <= 9; f++) matrizNivel[f][245] = 1;
        crearEscaleraSube(215, 3);
        crearEscaleraBaja(219, 3);
        crearEscaleraSube(226, 5);
        // ----terminamos de generar el nivel subterráneo----

        // agregamos bloques invisibles
        matrizNivel[6][69] = 5;
        matrizNivel[4][150] = 5;
        matrizNivel[5][154] = 5;
        matrizNivel[6][138] = 5;
        matrizNivel[6][139] = 5;

        // Bloques Falsos rotatorios
        matrizNivel[2][81] = 6;
        matrizNivel[2][82] = 6;
        matrizNivel[2][83] = 6;
        matrizNivel[2][84] = 6;
        matrizNivel[2][85] = 6;

        // bloques Latigazos del Suelo
        matrizNivel[10][90] = 7;
        matrizNivel[10][91] = 7;
        matrizNivel[10][92] = 7;

        // salto de Fe Engañoso
        for(int c = 173; c <= 180; c++) {
            matrizNivel[10][c] = 0;
            matrizNivel[11][c] = 0;
        }

        // bloques invisibles para pasar el nivel de salto de fe
        matrizNivel[10][175] = 9;
        matrizNivel[10][176] = 9;
    }

    // método para crear tuberías, desde el parámetro recibido en la variable fila hasta el fondo del mapa
    private void crearTuberia(int col, int fila) {
        for (int f = fila; f <= 11; f++) {
            matrizNivel[f][col] = 2;
            matrizNivel[f][col+1] = 2; // le agregamos grosor al mapa con
        }
    }

    // método para crear escaleras de subida
    private void crearEscaleraSube(int c, int h) {
        for (int i=0; i<h; i++) {
            for (int f=9; f>=9-i; f--) {
                matrizNivel[f][c+i] = 1;
            }
        }
    }

    // método para crear escaleras de bajada
    private void crearEscaleraBaja(int c, int h) {
        for (int i=0; i<h; i++) {
            for (int f=9; f>=9-(h-1-i); f--) {
                matrizNivel[f][c+i] = 1;
            }
        }
    }

    // método para generar el mundo
    public void generarMundo() {
        // vacíamos todos los objetos del mapa cada que generamos el mapa, para evitar duplicar el mapa
        colisionesSuelo.clear();
        bloquesInteractivos.clear();

        // recorremos todas las filas de la matriz
        for (int fila = 0; fila < matrizNivel.length; fila++) {
            // recoremos todas las columnas de la matriz
            for (int col = 0; col < matrizNivel[0].length; col++) {
                // obtenemos el número guardado en la matriz y la pasamos a la variable "tipo", si es 1 representa suelo, 2 tuberias...etc
                int tipo = matrizNivel[fila][col];
                // le asignamos una coordenada a cada objeto tomando en cuenta sus dimensiones
                int x = col * panel.TAMANO_BLOQUE;
                int y = fila * panel.TAMANO_BLOQUE;

                // les damos hitboxes usando un rectángulo para colisionar con los objetos a las tuberias y suelos (que son de tipo 1 y 2)
                if (tipo == 1 || tipo == 2) colisionesSuelo.add(new Rectangle(x, y, panel.TAMANO_BLOQUE, panel.TAMANO_BLOQUE));
                // los bloques sorpresas son de tipo 3, instanciamos objetos de Bloque
                // hacemos estos mismos pasos para los siguientes tipos, menos el 5
                else if (tipo == 3) {
                    Bloque b = new BloqueSorpresa(x, y, panel.TAMANO_BLOQUE, panel.TAMANO_BLOQUE);
                    bloquesInteractivos.add(b); // añadimos a esta lista que se modificará en cada "fotograma"
                    colisionesSuelo.add(b.obtenerHitbox()); // añadimos a esta lista que significa que colisionará con los objetos
                }
                // bloques rompibles son de tipo 4
                else if (tipo == 4) {
                    Bloque b = new BloqueRompible(x, y, panel.TAMANO_BLOQUE, panel.TAMANO_BLOQUE);
                    bloquesInteractivos.add(b);
                    colisionesSuelo.add(b.obtenerHitbox());
                }
                // este bloque no se añade a la lista colisionesSuelo, para que mario pueda traspasarlo
                // hasta que lo golpee
                else if (tipo == 5) bloquesInteractivos.add(new BloqueInvisible(x, y, panel.TAMANO_BLOQUE, panel.TAMANO_BLOQUE));
                // bloque falso es de tipo 6
                else if (tipo == 6) {
                    Bloque b = new BloqueFalso(x, y, panel.TAMANO_BLOQUE, panel.TAMANO_BLOQUE);
                    bloquesInteractivos.add(b);
                    colisionesSuelo.add(b.obtenerHitbox());
                }
                // bloque latigo son de tipo 7
                else if (tipo == 7) {
                    Bloque b = new BloqueLatigo(x, y, panel.TAMANO_BLOQUE, panel.TAMANO_BLOQUE);
                    bloquesInteractivos.add(b);
                    colisionesSuelo.add(b.obtenerHitbox());
                }
                // bloque Invisible para trampa, es de tipo 9
                else if (tipo == 9) {
                    Bloque b = new BloqueInvisibleTrampa(x, y, panel.TAMANO_BLOQUE, panel.TAMANO_BLOQUE);
                    bloquesInteractivos.add(b);
                    colisionesSuelo.add(b.obtenerHitbox());
                }
            }
        }
    }

    // método para dibujar el fondo del juego
    public void dibujarFondo(Graphics2D g2d) {
        int limiteExterior = 200 * panel.TAMANO_BLOQUE;

        if (imgFondo != null) {
            //Define qué tan alto debe ser el fondo en pantalla.
            // Aquí le dices que ocupe 10 bloques de altura
            // (más un margen de 10 píxeles para cubrir imperfecciones en el borde superior).
            int altoDeseado = 10 * panel.TAMANO_BLOQUE + 10;
            // modificamos el ancho para que sea acorde a lo que se necesita
            int anchoProporcional = (imgFondo.getWidth() * altoDeseado) / imgFondo.getHeight();

            // pega la imagen una y otra vez hasta llegar al limite del nivel
            for (int x = 0; x < limiteExterior; x += anchoProporcional) {
                g2d.drawImage(imgFondo, x, 0, anchoProporcional, altoDeseado, null);
            }
        }
    }

    // método para dibujar el mapa
    public void dibujar(Graphics2D g2d) {
        dibujarFondo(g2d);

        // recoremos cada fila de la matriz
        for (int fila = 0; fila < matrizNivel.length; fila++) {
            // recorremos cada columna de la matriz
            for (int col = 0; col < matrizNivel[0].length; col++) {
                // extraemos el numero (tipo) guardado en la matriz
                int tipo = matrizNivel[fila][col];
                int x = col * panel.TAMANO_BLOQUE;
                int y = fila * panel.TAMANO_BLOQUE;

                // dibujamos el suelo y muro en el mapa(tipo 1)
                if (tipo == 1) {
                    // ponemos la imagen en la interfaz
                    if (imgSuelo != null) {
                        g2d.drawImage(imgSuelo, x, y, panel.TAMANO_BLOQUE, panel.TAMANO_BLOQUE, null);
                    } else {
                        // si no hay imagen, dibujamos y pintamos
                        if (col > 205) g2d.setColor(new Color(0, 100, 150));
                        else g2d.setColor(new Color(210, 80, 20));
                        g2d.fillRect(x, y, panel.TAMANO_BLOQUE, panel.TAMANO_BLOQUE);
                        g2d.setColor(Color.BLACK); g2d.drawRect(x, y, panel.TAMANO_BLOQUE, panel.TAMANO_BLOQUE);
                    }
                } else if (tipo == 2) { // dibujamos tuberias en el mapa que se dividen en 2 partes: la cabeza y el cuerpo
                    boolean esLadoIzquierdo = (col < matrizNivel[0].length - 1 && matrizNivel[fila][col+1] == 2 && (col == 0 || matrizNivel[fila][col-1] != 2));
                    if (esLadoIzquierdo) { // empezamos a dibujar desde el lado izquierdo
                        if (fila > 0 && matrizNivel[fila - 1][col] != 2) {
                            // dibuja la imagen de la cabeza de la tubería
                            if (imgTuberia != null) {
                                g2d.drawImage(imgTuberia, x, y, panel.TAMANO_BLOQUE * 2, panel.TAMANO_BLOQUE, null);
                            } else {
                                // si no hay imagen, la dibuja
                                g2d.setColor(new Color(0, 180, 0));
                                g2d.fillRect(x, y, panel.TAMANO_BLOQUE * 2, panel.TAMANO_BLOQUE);
                                g2d.setColor(Color.BLACK); g2d.drawRect(x, y, panel.TAMANO_BLOQUE * 2, panel.TAMANO_BLOQUE);
                            }
                        } else {
                            // dibujamos la imagen de la textura del cuerpo
                            if (imgTuberiaCuerpo != null) {
                                g2d.drawImage(imgTuberiaCuerpo, x, y, panel.TAMANO_BLOQUE * 2, panel.TAMANO_BLOQUE, null);
                            } else {
                                // si no hay imagen, la dibujamos
                                g2d.setColor(new Color(0, 160, 0));
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
        // dibujamos los bloques interactivos, que son las trampas o "bloques especiales"
        for (Bloque b : bloquesInteractivos) b.dibujar(g2d);
    }
}