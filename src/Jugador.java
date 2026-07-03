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

    // FÍSICAS (Gravedad y Salto)
    double velocidadY;  // <--- DOUBLE
    double gravedad;    // <--- DOUBLE
    double fuerzaSalto; // <--- DOUBLE
    boolean enSuelo;     // Saber si está pisando algo para permitirle saltar

    // SPRITES Y ANIMACIÓN
    BufferedImage marioQuieto;
    BufferedImage marioCamina1;
    BufferedImage marioCamina2;
    BufferedImage marioSalta;

    int contadorAnimacion = 0; // Cuenta los frames que han pasado
    int numeroFotograma = 1;   // Alterna entre el fotograma 1 y 2 al caminar
    boolean mirandoDerecha = true; // Para saber hacia dónde voltear la imagen

    public Jugador(PanelJuego panel, ManejadorTeclas teclas) {
        this.panel = panel;
        this.teclas = teclas;

        // Posición inicial
        x = 100;
        y = 100;
        velocidad = 4;

        // NUEVAS FÍSICAS MÁS SUAVES
        velocidadY = 0.0;
        gravedad = 0.3;       // Antes era 1. Al ser 0.4, flota mucho más.
        fuerzaSalto = -12;  // Un impulso menor, equilibrado con la nueva gravedad
        enSuelo = false;

        cargarImagen();
    }

    public void cargarImagen() {
        try {
            // Cargamos la hoja completa
            java.io.InputStream flujoImagen = getClass().getResourceAsStream("/mario_sprites.png");
            if (flujoImagen != null) {
                BufferedImage hojaSprites = ImageIO.read(flujoImagen);

                // Recortamos cada pose (Asumiendo que cada pose mide 16x16 en tu archivo de imagen)
                marioQuieto = hojaSprites.getSubimage(0, 0, 16, 16);     // Columna 1
                marioCamina1 = hojaSprites.getSubimage(16, 0, 16, 16);   // Columna 2
                marioCamina2 = hojaSprites.getSubimage(32, 0, 16, 16);   // Columna 3
                marioSalta = hojaSprites.getSubimage(48, 0, 16, 16);     // Columna 4

            } else {
                System.out.println("ADVERTENCIA: No se encontró mario_sprites.png.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void actualizar(GestorNivel nivel) {

        // ==========================================
        // FASE 1: MOVIMIENTO Y COLISIÓN HORIZONTAL (Eje X)
        // ==========================================

        int velocidadActualX = 0;

        if (teclas.izquierda) {
            velocidadActualX = -velocidad;
            mirandoDerecha = false;
        }
        if (teclas.derecha) {
            velocidadActualX = velocidad;
            mirandoDerecha = true;
        }

        x += velocidadActualX; // Aplicamos el movimiento horizontal primero

        // Comprobamos si, al movernos en X, nos metimos dentro de un bloque
        Rectangle hitboxX = obtenerHitbox();
        for (Rectangle bloque : nivel.colisionesSuelo) {
            if (hitboxX.intersects(bloque)) {
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


        // ==========================================
        // FASE 2: MOVIMIENTO Y COLISIÓN VERTICAL (Eje Y)
        // ==========================================

        // Salto
        if (teclas.arriba && enSuelo) {
            velocidadY = fuerzaSalto;
            enSuelo = false;
        }

        // Aplicamos gravedad y movimiento vertical
        velocidadY += gravedad;
        y += velocidadY;
        enSuelo = false;

        // Obtenemos el hitbox
        Rectangle hitboxY = obtenerHitbox();

        // ---> EL ARREGLO ESTÁ AQUÍ <---
        // Le sumamos 1 píxel al alto para que no pierda el contacto con el suelo por culpa de la gravedad
        hitboxY.height += 1;

        // Comprobamos colisiones
        for (Rectangle bloque : nivel.colisionesSuelo) {
            if (hitboxY.intersects(bloque)) {
                if (velocidadY > 0) { // Si caía (o la gravedad lo empujaba)
                    y = (double) (bloque.y - panel.TAMANO_BLOQUE);
                    velocidadY = 0;
                    enSuelo = true;
                }
                else if (velocidadY < 0) { // Si chocaba con un techo
                    y = (double) (bloque.y + bloque.height);
                    velocidadY = 0;
                }
            }
        }


        // ==========================================
        // FASE 3: LÓGICA DE ANIMACIÓN Y LIMITES
        // ==========================================

        if (teclas.izquierda || teclas.derecha) {
            contadorAnimacion++;
            if (contadorAnimacion > 10) {
                if (numeroFotograma == 1) numeroFotograma = 2;
                else numeroFotograma = 1;
                contadorAnimacion = 0;
            }
        } else {
            numeroFotograma = 1;
        }

        // Límite de muerte por caída (Pozo)
        if (y > panel.ALTO_PANTALLA + panel.TAMANO_BLOQUE) { // Le sumamos un bloque extra de margen
            System.out.println("¡Mario ha muerto! Cayó por el pozo.");
            x = 100;
            y = 100.0;
        }
    }

    public void dibujar(Graphics2D g2d) {
        BufferedImage imagenActual = marioQuieto; // Por defecto

        // 1. ¿Qué imagen toca mostrar?
        if (!enSuelo) {
            imagenActual = marioSalta; // Si está en el aire, siempre es la pose de salto
        } else if (teclas.izquierda || teclas.derecha) {
            // Si camina, alternamos entre los fotogramas
            if (numeroFotograma == 1) imagenActual = marioCamina1;
            if (numeroFotograma == 2) imagenActual = marioCamina2;
        }

        // 2. Dibujar y Voltear (Espejar) la imagen si es necesario
        if (imagenActual != null) {
            if (mirandoDerecha) {
                // Se dibuja normal
                g2d.drawImage(imagenActual, x, (int)y, panel.TAMANO_BLOQUE, panel.TAMANO_BLOQUE, null);
            } else {
                // TRUCO: Para voltear la imagen, la dibujamos desplazada hacia la derecha (x + ancho)
                // y le damos un ancho negativo (-ancho). Swing la dibujará en espejo.
                g2d.drawImage(imagenActual,
                        x + panel.TAMANO_BLOQUE, (int)y,
                        -panel.TAMANO_BLOQUE, panel.TAMANO_BLOQUE, null);
            }
        } else {
            g2d.setColor(Color.RED);
            g2d.fillRect(x, (int)y, panel.TAMANO_BLOQUE, panel.TAMANO_BLOQUE);
        }
    }
    // Añade este método:
    public Rectangle obtenerHitbox() {
        return new Rectangle(x,(int) y, panel.TAMANO_BLOQUE, panel.TAMANO_BLOQUE);
    }
}