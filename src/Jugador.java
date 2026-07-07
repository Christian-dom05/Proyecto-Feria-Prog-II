import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Jugador {

    PanelJuego panel;
    ManejadorTeclas teclas;

    // Posición y movimiento horizontal
    int x;
    double y;
    int velocidad;

    // esto son las físicas del juego, la gravedad, velocidad de caida, etc
    double velocidadY;
    double gravedad;
    double fuerzaSalto;
    boolean enSuelo;     // variable para saber si está pisando algo para permitirle saltar

    // Sprites o imagenes para que mario salga animado
    BufferedImage marioQuieto;
    BufferedImage marioCamina1;
    BufferedImage marioCamina2;
    BufferedImage marioSalta;

    int contadorAnimacion = 0; // Cuenta los frames que pasaron
    int numeroFotograma = 1;   // Alterna entre el fotograma 1 y 2 al caminar
    boolean mirandoDerecha = true; // Para saber hacia dónde voltear la imagen, si se hace false, entonces voltera a la izquierda
    GestorSonido sonidoMario = new GestorSonido();
    public Jugador(PanelJuego panel, ManejadorTeclas teclas) {
        this.panel = panel;
        this.teclas = teclas; // aquí recibe el teclado que esté presionando el usuario

        // Posición inicial de mario
        x = 100;
        y = 100;
        velocidad = 4;

        // aquí modificamos las físicas del juego
        velocidadY = 0.0;
        gravedad = 0.3;       // la gravedad hace que el personaje "flote"
        fuerzaSalto = -12;
        enSuelo = false;

        cargarImagen(); // carga la imagen de mario
    }

    // con este método cargamos la imagen de Mario
    public void cargarImagen() {
        try {
            // Cargamos la hoja completa de los sprites guardados en la carpeta recursos
            //getResourcesAsStream busca el directorio de la imagen a partir de su nombre
            java.io.InputStream flujoImagen = getClass().getResourceAsStream("/mario_sprites.png");
            if (flujoImagen != null) {
                BufferedImage hojaSprites = ImageIO.read(flujoImagen);

                // Recortamos cada pose de mario y le damos un tamaño de 16x16 pixeles
                marioQuieto = hojaSprites.getSubimage(0, 0, 16, 16);
                marioCamina1 = hojaSprites.getSubimage(16, 0, 16, 16);
                marioCamina2 = hojaSprites.getSubimage(32, 0, 16, 16);
                marioSalta = hojaSprites.getSubimage(48, 0, 16, 16);

            } else {
                System.out.println("No se encontró mario_sprites.png.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // este método es el que se ejecuta 60 veces por segundo, junto al repaint()
    public void actualizar(GestorNivel nivel) {

        // Movimiento y colisión horizontal

        int velocidadActualX = 0;

        // hacer que el personaje se mueva a la izquierda
        if (teclas.izquierda) {
            velocidadActualX = -velocidad;
            mirandoDerecha = false;
        }
        // hacer que el personaje se mueva a la derecha
        if (teclas.derecha) {
            velocidadActualX = velocidad;
            mirandoDerecha = true;
        }

        x += velocidadActualX; //esto es el movimiento horizontal, va sumando y se va moviendo

        //esta parte es para comprobar si al movernos nos topamos con un bloque
        Rectangle hitboxX = obtenerHitbox(); // usamos la clase Rectangle para crear los hitbox, para aprovechar el método intersects
        for (Rectangle bloque : nivel.colisionesSuelo) {
            if (hitboxX.intersects(bloque)) { //intersects verifica si un bloque está chocando con otro
                // Si chocamos mientras íbamos a la derecha
                if (velocidadActualX > 0) {
                    x = bloque.x - panel.TAMANO_BLOQUE; // Nos pegamos al lado izquierdo del bloque
                }
                // Si chocamos mientras íbamos a la izquierda
                else if (velocidadActualX < 0) {
                    x = bloque.x + bloque.width; // Nos pegamos al lado derecho del bloque
                }
            }
        }

        // movimieno y colision vertical

        // Salto
        if (teclas.arriba && enSuelo) {
            velocidadY = fuerzaSalto;
            enSuelo = false;
            sonidoMario.reproducir("/salto.wav");
        }

        // Aplicamos gravedad y movimiento vertical
        velocidadY += gravedad;
        y += velocidadY;
        enSuelo = false;

        // Obtenemos el hitbox
        Rectangle hitboxY = obtenerHitbox();

        // Le sumamos 1 píxel al alto para que no pierda el contacto con el suelo por culpa de la gravedad
        hitboxY.height += 1;

        // aquí nuevamente se comprueban las colisiones
        for (Rectangle bloque : nivel.colisionesSuelo) {
            if (hitboxY.intersects(bloque)) {
                if (velocidadY > 0) { // Si caía
                    if (y + panel.TAMANO_BLOQUE - velocidadY <= bloque.y + 10) {
                        y = (double) (bloque.y - panel.TAMANO_BLOQUE);
                        velocidadY = 0;
                        enSuelo = true;
                    }
                }
                else if (velocidadY < 0) { // Si chocaba con un techo
                    y = (double) (bloque.y + bloque.height);
                    velocidadY = 0;
                }
            }
        }

        //animaciones
        // si se mueve a la izquierda o derecha
        if (teclas.izquierda || teclas.derecha) {
            contadorAnimacion++; // aumenta el contador
            if (contadorAnimacion > 10) {
                if (numeroFotograma == 1) numeroFotograma = 2;
                else numeroFotograma = 1;
                contadorAnimacion = 0;
            }
        } else {
            numeroFotograma = 1;
        }

        // poonemos un limite de muerte por caida
        if (y > panel.ALTO_PANTALLA + panel.TAMANO_BLOQUE) {
            // Solo si realmente cayó al abismo infinito
            panel.activarGameOver();
        }
    }

    // con este método dibujamos al jugador, como son 4 imagenes de mario, aqui vemos que imagen
    // poner en cada escenario
    public void dibujar(Graphics2D g2d) {
        BufferedImage imagenActual = marioQuieto; // Por defecto

        // aqui vemos que imagen colocamos
        if (!enSuelo) {
            imagenActual = marioSalta; // Si está en el aire, se pone la imagen de salto
        } else if (teclas.izquierda || teclas.derecha) {
            // Si camina, alternamos entre los fotogramas, si va a la izquierda, se mueve a la izquierda
            if (numeroFotograma == 1) imagenActual = marioCamina1;
            if (numeroFotograma == 2) imagenActual = marioCamina2;
        }

        // aquí volteamos las imagenes cuando sea necesario
        if (imagenActual != null) {
            if (mirandoDerecha) {
                // Se dibuja normal
                g2d.drawImage(imagenActual, x, (int)y, panel.TAMANO_BLOQUE, panel.TAMANO_BLOQUE, null);
            } else {
                // Para voltear la imagen, la dibujamos desplazada hacia la derecha (x + ancho)
                // y le damos un ancho negativo (-ancho)esto hará que la imagen mire al lado contrario
                g2d.drawImage(imagenActual,
                        x + panel.TAMANO_BLOQUE, (int)y,
                        -panel.TAMANO_BLOQUE, panel.TAMANO_BLOQUE, null);
            }
        } else {
            g2d.setColor(Color.RED);
            g2d.fillRect(x, (int)y, panel.TAMANO_BLOQUE, panel.TAMANO_BLOQUE);
        }
    }
    // con este método obtenemos el hitbox, que nos ayudará para hacer las colisiones
    public Rectangle obtenerHitbox() {
        return new Rectangle(x,(int) y, panel.TAMANO_BLOQUE, panel.TAMANO_BLOQUE);
    }
}