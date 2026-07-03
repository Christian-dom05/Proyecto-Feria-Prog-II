import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class BloqueSorpresa {

    public int x, y, ancho, alto;
    public boolean usado;

    public BloqueSorpresa(int x, int y, int ancho, int alto) {
        this.x = x;
        this.y = y;
        this.ancho = ancho;
        this.alto = alto;
        this.usado = false; // Comienza lleno
    }

    public Rectangle obtenerHitbox() {
        return new Rectangle(x, y, ancho, alto);
    }

    public void dibujar(Graphics2D g2d) {
        if (!usado) {
            // Dibuja el bloque amarillo de interrogación
            g2d.setColor(new Color(255, 204, 0));
            g2d.fillRect(x, y, ancho, alto);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(x, y, ancho, alto);

            // Símbolo "?" temporal (Luego puedes cambiarlo por un sprite si deseas)
            g2d.setFont(new Font("Arial", Font.BOLD, ancho / 2));
            g2d.drawString("?", x + (ancho/3), y + (alto - 12));
        } else {
            // Dibuja el bloque marrón "vacío"
            g2d.setColor(new Color(139, 69, 19));
            g2d.fillRect(x, y, ancho, alto);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(x, y, ancho, alto);
        }
    }
}