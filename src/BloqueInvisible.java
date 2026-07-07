import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class BloqueInvisible extends Bloque {
    static BufferedImage imgVacio;

    public BloqueInvisible(int x, int y, int ancho, int alto) {
        super(x, y, ancho, alto);
        this.activo = false;

        try {
            java.io.InputStream is = getClass().getResourceAsStream("/bloque_vacio.png");
            if (is != null) imgVacio = ImageIO.read(is);
        } catch(Exception e){}
    }

    @Override
    public void reaccionarGolpe(PanelJuego panel) {
        if (!activo) {
            activo = true;
        }
    }

    public void bloqueTrampa(GestorNivel gestorNivel){

    }

    @Override
    public void dibujar(Graphics2D g2d) {
        // Solo dibujamos si Mario ya lo golpeó
        if (activo && imgVacio != null) {
            g2d.drawImage(imgVacio, x, y, ancho, alto, null);
        }
    }
}