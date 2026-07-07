import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Moneda {
    public int x, y, ancho, alto;
    public boolean recogida;
    public boolean esMaldita;
    static BufferedImage imgMoneda;

    public Moneda(int x, int y, int ancho, int alto, boolean esMaldita) {
        this.x = x; this.y = y; this.ancho = ancho; this.alto = alto;
        this.recogida = false; this.esMaldita = esMaldita;

        if (imgMoneda == null) {
            try {
                java.io.InputStream is = getClass().getResourceAsStream("/moneda.png");
                if(is != null) imgMoneda = ImageIO.read(is);
            } catch(Exception e){}
        }
    }

    public Rectangle obtenerHitbox() { return new Rectangle(x, y, ancho, alto); }

    public void dibujar(Graphics2D g2d) {
        if (!recogida) {
            if (imgMoneda != null) {
                g2d.drawImage(imgMoneda, x + 10, y + 5, ancho - 20, alto - 10, null);
            } else {
                g2d.setColor(new Color(255, 215, 0));
                g2d.fillOval(x + 12, y + 8, ancho - 24, alto - 16);
            }
        }
    }
}