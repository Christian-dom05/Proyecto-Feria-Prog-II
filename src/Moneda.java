import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Moneda {
    public int x, y, ancho, alto;
    public boolean recogida;

    public Moneda(int x, int y, int ancho, int alto) {
        this.x = x;
        this.y = y;
        this.ancho = ancho;
        this.alto = alto;
        this.recogida = false;
    }

    public Rectangle obtenerHitbox() {
        return new Rectangle(x, y, ancho, alto);
    }

    public void dibujar(Graphics2D g2d) {
        if (!recogida) {
            g2d.setColor(new Color(255, 215, 0));
            g2d.fillOval(x + 12, y + 8, ancho - 24, alto - 16);

            g2d.setColor(Color.BLACK);
            g2d.drawOval(x + 12, y + 8, ancho - 24, alto - 16);

            g2d.setColor(new Color(218, 165, 32));
            g2d.drawOval(x + 18, y + 12, ancho - 36, alto - 24);
        }
    }
}