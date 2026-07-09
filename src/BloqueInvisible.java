import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import javax.imageio.ImageIO;

/**
 * Esta clase representa un bloque invisible que aparecerá cuando Mario lo golpee
 */
public class BloqueInvisible extends Bloque {
    static BufferedImage imgVacio;

    public BloqueInvisible(int x, int y, int ancho, int alto) {
        super(x, y, ancho, alto);
        this.activo = false;

        try {
            InputStream is = getClass().getResourceAsStream("/bloque_vacio.png");
            if (is != null) imgVacio = ImageIO.read(is);
        } catch(Exception e){}
    }

    // si mario golpea este bloque con la cabeza entonces aparece
    @Override
    public void reaccionarGolpe(PanelJuego panel) {
        if (!activo) {
            activo = true;
        }
    }

    @Override
    public void dibujar(Graphics2D g2d) {
        // Solo dibujamos si Mario ya lo golpeó
        if (activo && imgVacio != null) {
            g2d.drawImage(imgVacio, x, y, ancho, alto, null);
        }
    }
}