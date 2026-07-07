import java.awt.Graphics2D;
import java.awt.Rectangle;

public abstract class Bloque {
    public int x, y, ancho, alto;
    public Rectangle hitbox;
    public boolean activo;

    public Bloque(int x, int y, int ancho, int alto) {
        this.x = x;
        this.y = y;
        this.ancho = ancho;
        this.alto = alto;
        this.hitbox = new Rectangle(x, y, ancho, alto);
        this.activo = true;
    }

    public Rectangle obtenerHitbox() {
        return hitbox;
    }

    public abstract void reaccionarGolpe(PanelJuego panel);

    public void actualizar(PanelJuego panel) {}

    public abstract void dibujar(Graphics2D g2d);
}