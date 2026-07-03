import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class BloqueInvisible {

    int x, y, ancho, alto;
    public boolean descubierto;

    public BloqueInvisible(int x, int y, int ancho, int alto) {
        this.x = x;
        this.y = y;
        this.ancho = ancho;
        this.alto = alto;
        this.descubierto = false; // El jugador no lo ve al inicio
    }

    // El Hitbox es una caja matemática del tamaño exacto del bloque
    public Rectangle obtenerHitbox() {
        return new Rectangle(x, y, ancho, alto);
    }

    public void dibujar(Graphics2D g2d) {
        // Solo dibujamos el bloque si Mario ya chocó con él
        if (descubierto) {
            g2d.setColor(Color.ORANGE); // Simulamos el color de un bloque sorpresa
            g2d.fillRect(x, y, ancho, alto);

            // Le ponemos un borde negro para que se vea como un bloque de Mario
            g2d.setColor(Color.BLACK);
            g2d.drawRect(x, y, ancho, alto);
        }
    }
}