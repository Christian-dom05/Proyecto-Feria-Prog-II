import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.InputStream;

/**
 * Clase hija de Bloque
 * Esta clase representa el bloque trampa que hará daño a Mario
 * pero que tendrá la misma apariencia que un bloque normal de ladrillo
 */
public class BloqueFalso extends Bloque {
    boolean cayendo = false;
    double velY = 0;
    double angulo = 0;
    double yReal;
    static BufferedImage imgLadrillo;

    /**
     * la variable imgLadrillo es static para que exista solo una vez en el programa y no se creen
     * varias copias por cada instancia de clase creada, sinó una sola para todas las instancias
     */
    public BloqueFalso(int x, int y, int ancho, int alto) {
        super(x, y, ancho, alto);
        this.yReal = y;

        if (imgLadrillo == null) { // si la variable global está vacía entonces la inicializamos
            try {
                // cargamos la imagen en la variable is
                InputStream is = getClass().getResourceAsStream("/ladrillo.png");
                // convierte la imagen en un Buffered y la guarda en la variable imgLadrillo de tipo BufferedImage
                if(is != null) imgLadrillo = ImageIO.read(is);
            } catch(Exception e){}
        }
    }

    // no usamos método
    @Override
    public void reaccionarGolpe(PanelJuego panel) { }

    // 60 veces por segundo se va actualizando
    @Override
    public void actualizar(PanelJuego panel) {
        if (!cayendo) {
            if (Math.abs(panel.jugador.x - this.x) < panel.TAMANO_BLOQUE && panel.jugador.y > this.y) {
                cayendo = true;
                panel.gestorNivel.colisionesSuelo.remove(this.hitbox); // cuando el bloque cae se quita el hitbox para que traspase todo
            }
        } else {
            // lógica del bloque cayendo
            velY += 0.4;
            yReal += velY;
            this.y = (int) yReal;
            this.hitbox.y = this.y;
            angulo += 0.15;
            // si algún bloque toca a Mario, el jugador pierde
            if (this.hitbox.intersects(panel.jugador.obtenerHitbox())) panel.activarGameOver();
        }
    }

    // método para dibujar al bloque falso
    @Override
    public void dibujar(Graphics2D g2d) {
        // AffineTransform hace toda la matemática para rotar los bloques
        AffineTransform lienzoOriginal = g2d.getTransform();
        // condicional. Si el bloque cae, le damos rotación
        if (cayendo) g2d.rotate(angulo, x + ancho / 2.0, y + alto / 2.0);

        // le asignamos la imagen del ladrillo a estos ladrillos falsos
        // si la imagen no se carga, dibujamos un rectángulo naranja con bordes negros
        if (imgLadrillo != null) {
            g2d.drawImage(imgLadrillo, x, y, ancho, alto, null);
        } else {
            g2d.setColor(new Color(210, 80, 20));
            g2d.fillRect(x, y, ancho, alto);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(x, y, ancho, alto);
        }
        g2d.setTransform(lienzoOriginal);
    }
}