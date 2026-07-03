import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class HongoVenenoso {

    PanelJuego panel;
    public int x;
    public double y;

    int velocidadX;
    double velocidadY;
    double gravedad;

    public boolean activo;

    // Nueva variable para la imagen
    BufferedImage imagenHongo;

    public HongoVenenoso(PanelJuego panel, int x, int y) {
        this.panel = panel;
        this.x = x;
        this.y = y;

        this.velocidadX = -2;
        this.velocidadY = 0;
        this.gravedad = 0.4;

        // inicia como falso
        this.activo = false;

        cargarImagen();
    }
    // Método para hacer que el hongo nazca desde el bloque sorpresa
    public void aparecer(int xAparicion, int yAparicion) {
        this.x = xAparicion;
        this.y = (double) yAparicion;
        this.activo = true;
        this.velocidadY = -6.0; // Le damos un impulso negativo en Y para que dé un pequeño salto hacia arriba
    }

    public void cargarImagen() {
        try {
            java.io.InputStream flujoImagen = getClass().getResourceAsStream("/hongo.png");
            if (flujoImagen != null) {
                imagenHongo = ImageIO.read(flujoImagen);
            } else {
                System.out.println("No se encontró hongo.png");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Rectangle obtenerHitbox() {
        return new Rectangle(x, (int)y, panel.TAMANO_BLOQUE, panel.TAMANO_BLOQUE);
    }

    public void actualizar(GestorNivel nivel) {
        if (!activo) return;

        // Movimiento Horizontal
        x += velocidadX;

        Rectangle hitboxX = obtenerHitbox();
        for (Rectangle bloque : nivel.colisionesSuelo) {
            if (hitboxX.intersects(bloque)) {
                velocidadX *= -1; // Rebota contra la pared
                break;
            }
        }

        // Gravedad y Suelo
        velocidadY += gravedad;
        y += velocidadY;

        Rectangle hitboxY = obtenerHitbox();
        for (Rectangle bloque : nivel.colisionesSuelo) {
            if (hitboxY.intersects(bloque)) {
                if (velocidadY > 0) {
                    y = bloque.y - panel.TAMANO_BLOQUE;
                    velocidadY = 0;
                }
            }
        }

        if (y > panel.ALTO_PANTALLA) {
            activo = false;
        }
    }

    public void dibujar(Graphics2D g2d) {
        if (!activo) return;

        // Dibujamos la imagen si cargó bien, si no, usamos el cuadro morado de respaldo
        if (imagenHongo != null) {
            g2d.drawImage(imagenHongo, x, (int)y, panel.TAMANO_BLOQUE, panel.TAMANO_BLOQUE, null);
        } else {
            g2d.setColor(new Color(138, 43, 226));
            g2d.fillRect(x, (int)y, panel.TAMANO_BLOQUE, panel.TAMANO_BLOQUE);
        }
    }
}