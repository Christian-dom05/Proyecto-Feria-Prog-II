import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * esta clase representa el banderín para terminar la partida
 */
public class Banderin {

    public int x, y, ancho, alto;

    public Banderin(int x, int y, int ancho, int alto) {
        this.x = x;
        this.y = y;
        this.ancho = ancho;
        this.alto = alto;
    }

    public Rectangle obtenerHitbox() {
        // El hitbox cubrirá todo el poste para que Mario lo pueda tocar a cualquier altura
        return new Rectangle(x + (ancho / 2) - 4, y, 8, alto);
    }

    // con esta clase fibujamos el poste
    public void dibujar(Graphics2D g2d) {
        // el poste
        g2d.setColor(new Color(200, 200, 200)); // Gris metálico
        g2d.fillRect(x + (ancho / 2) - 4, y, 8, alto);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(x + (ancho / 2) - 4, y, 8, alto);

        // el tope dorado
        g2d.setColor(new Color(255, 215, 0)); // Color Oro
        g2d.fillOval(x + (ancho / 2) - 12, y - 10, 24, 24);
        g2d.setColor(Color.BLACK);
        g2d.drawOval(x + (ancho / 2) - 12, y - 10, 24, 24);

        // la bandera
        g2d.setColor(new Color(0, 180, 0)); // Verde clásico
        g2d.fillRect(x - 24, y + 10, 30, 20); // Dibuja la bandera hacia la izquierda
        g2d.setColor(Color.BLACK);
        g2d.drawRect(x - 24, y + 10, 30, 20);
    }
}