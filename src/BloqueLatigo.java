import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.InputStream;

/**
 * Clase que representa los bloques que saldrán del suelo para dañar a Mario
 */
public class BloqueLatigo extends Bloque {
    boolean activado = false;
    double angulo = 0;
    static BufferedImage imgSuelo;

    public BloqueLatigo(int x, int y, int ancho, int alto) {
        super(x, y, ancho, alto);
        if (imgSuelo == null) {
            try {
                InputStream is = getClass().getResourceAsStream("/suelo.png");
                if (is != null) imgSuelo = ImageIO.read(is);
            } catch(Exception e){}
        }
    }

    @Override
    public void reaccionarGolpe(PanelJuego panel) {}

    @Override
    public void actualizar(PanelJuego panel) {
        // si la trampa no está activada, sigue quieta, siendo parte del suelo
        if (!activado) {
            // verifica si el jugador está cerca de la trampa a una distancia de 1.5 el tamaño del bloque
            if (Math.abs(panel.jugador.x - this.x) < panel.TAMANO_BLOQUE * 1.5) {
                activado = true;
                panel.gestorNivel.colisionesSuelo.remove(this.hitbox);
            }
        } else {
            // la trampa se moverá hasta llegar a PI, o sea, completar 180 grados
            if (angulo < Math.PI) {
                angulo += 0.25;
                // el bloque se mueve usando la funcion seno, en un movimiento oscilatorio suave
                hitbox.y = this.y - (int)(this.alto * Math.sin(angulo));
                // si el bloque toca a Mario, pierde
                if (hitbox.intersects(panel.jugador.obtenerHitbox())) panel.activarGameOver();
            }
        }
    }

    // método para dibujar el bloque látigo
    @Override
    public void dibujar(Graphics2D g2d) {
        AffineTransform old = g2d.getTransform();
        if (activado) g2d.rotate(-angulo, x + ancho, y + alto);

        if (imgSuelo != null) {
            g2d.drawImage(imgSuelo, x, y, ancho, alto, null);
        } else {
            // si no hay la imagen, entonces se dibujan y colorean los bloques
            g2d.setColor(new Color(210, 80, 20));
            g2d.fillRect(x, y, ancho, alto);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(x, y, ancho, alto);
        }
        g2d.setTransform(old);
    }
}