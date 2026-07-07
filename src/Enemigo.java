import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Enemigo {
    public int x;
    public double y;
    int velocidadX;
    double velocidadY;
    double gravedad;
    public boolean vivo;
    public boolean enSuelo;
    int tamano;

    static BufferedImage imgGoomba;

    public Enemigo(int x, int y, int tamano) {
        this.x = x; this.y = y; this.tamano = tamano;
        this.velocidadX = -2; this.velocidadY = 0; this.gravedad = 0.4;
        this.vivo = true; this.enSuelo = false;

        if (imgGoomba == null) {
            try {
                java.io.InputStream is = getClass().getResourceAsStream("/goomba.png");
                if(is != null) imgGoomba = ImageIO.read(is);
            } catch(Exception e){}
        }
    }

    public Rectangle obtenerHitbox() { return new Rectangle(x, (int)y, tamano, tamano); }

    public void actualizar(GestorNivel nivel, Jugador jugador) {
        if (!vivo) return;
        x += velocidadX;
        Rectangle hitboxX = obtenerHitbox();
        for (Rectangle bloque : nivel.colisionesSuelo) {
            if (hitboxX.intersects(bloque)) {
                if (velocidadX > 0) x = bloque.x - tamano;
                else if (velocidadX < 0) x = bloque.x + bloque.width;
                velocidadX *= -1; break;
            }
        }
        velocidadY += gravedad; y += velocidadY; enSuelo = false;
        Rectangle hitboxY = obtenerHitbox();
        for (Rectangle bloque : nivel.colisionesSuelo) {
            if (hitboxY.intersects(bloque)) {
                if (velocidadY > 0) { y = bloque.y - tamano; velocidadY = 0; enSuelo = true; }
            }
        }
        if (y > 800) vivo = false;
        if (enSuelo && Math.abs(this.x - jugador.x) < 120 && jugador.y < this.y) {
            velocidadY = -9.0; enSuelo = false;
        }
    }

    public void dibujar(Graphics2D g2d) {
        if (!vivo) return;
        if (imgGoomba != null) {
            g2d.drawImage(imgGoomba, x, (int)y, tamano, tamano, null);
        } else {
            g2d.setColor(new Color(139, 69, 19)); g2d.fillRoundRect(x, (int)y, tamano, tamano, 15, 15);
            g2d.setColor(Color.WHITE); g2d.fillRect(x+10, (int)y+15, 10, 15); g2d.fillRect(x+28, (int)y+15, 10, 15);
        }
    }
}