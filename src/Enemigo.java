import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Enemigo {
    public int x;
    public double y;
    int velocidadX;
    double velocidadY;
    double gravedad;
    public boolean vivo;

    int tamano;

    public Enemigo(int x, int y, int tamano) {
        this.x = x;
        this.y = y;
        this.tamano = tamano;

        this.velocidadX = -2; // Comienza caminando hacia la izquierda
        this.velocidadY = 0;
        this.gravedad = 0.4;
        this.vivo = true;
    }

    public Rectangle obtenerHitbox() {
        return new Rectangle(x, (int)y, tamano, tamano);
    }

    public void actualizar(GestorNivel nivel) {
        if (!vivo) return;

        // Movimiento en X y rebote con paredes
        x += velocidadX;
        Rectangle hitboxX = obtenerHitbox();
        for (Rectangle bloque : nivel.colisionesSuelo) {
            if (hitboxX.intersects(bloque)) {
                velocidadX *= -1; // Invierte la dirección al chocar
                break;
            }
        }

        // movimiento en Y (Gravedad)
        // movimiento en X y rebote con paredes
        x += velocidadX;
        hitboxX = obtenerHitbox();
        for (Rectangle bloque : nivel.colisionesSuelo) {
            if (hitboxX.intersects(bloque)) {
                // nos empujamos fuera de la pared para no atascarnos
                if (velocidadX > 0) x = bloque.x - tamano;
                else if (velocidadX < 0) x = bloque.x + bloque.width;

                velocidadX *= -1; // Invierte la dirección al chocar
                break;
            }
        }

        // Si cae por un pozo, desaparece
        if (y > 800) vivo = false;
    }

    // dibuja al enemigo
    public void dibujar(Graphics2D g2d) {
        if (!vivo) return;

        // cuerpo
        g2d.setColor(new Color(139, 69, 19));
        g2d.fillRoundRect(x, (int)y, tamano, tamano, 15, 15);
        g2d.setColor(Color.BLACK);
        g2d.drawRoundRect(x, (int)y, tamano, tamano, 15, 15);

        // ojos blancos
        g2d.setColor(Color.WHITE);
        g2d.fillRect(x + 10, (int)y + 15, 10, 15);
        g2d.fillRect(x + 28, (int)y + 15, 10, 15);

        // Pupilas
        g2d.setColor(Color.BLACK);
        g2d.fillRect(x + 14, (int)y + 20, 4, 6);
        g2d.fillRect(x + 30, (int)y + 20, 4, 6);

        // Cejas enojadas usando líneas
        g2d.drawLine(x + 8, (int)y + 12, x + 20, (int)y + 18);
        g2d.drawLine(x + 40, (int)y + 12, x + 28, (int)y + 18);
    }
}