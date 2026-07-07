import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Banderin {
    public int x, y, ancho, alto;
    public boolean cayendo = false;
    double velY = 0;

    public Banderin(int x, int y, int ancho, int alto) {
        this.x = x;
        this.y = y;
        this.ancho = ancho;
        this.alto = alto;
    }

    public Rectangle obtenerHitbox() {
        return new Rectangle(x + (ancho / 2) - 4, y, 8, alto);
    }

    public void actualizar() {
        if (cayendo) {
            velY += 0.4;
            y += velY; // El banderín se precipita al vacío
        }
    }

    public void dibujar(Graphics2D g2d) {
        g2d.setColor(new Color(200, 200, 200));
        g2d.fillRect(x + (ancho / 2) - 4, y, 8, alto);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(x + (ancho / 2) - 4, y, 8, alto);
        g2d.setColor(new Color(255, 215, 0));
        g2d.fillOval(x + (ancho / 2) - 12, y - 10, 24, 24);
        g2d.setColor(Color.BLACK);
        g2d.drawOval(x + (ancho / 2) - 12, y - 10, 24, 24);
        g2d.setColor(new Color(0, 180, 0));
        g2d.fillRect(x - 24, y + 10, 30, 20);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(x - 24, y + 10, 30, 20);
    }
}