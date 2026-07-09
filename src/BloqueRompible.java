import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.InputStream;

/**
 * Clase que representa los bloques que se pueden romper
 */
public class BloqueRompible extends Bloque {
    static BufferedImage imgLadrillo;

    public BloqueRompible(int x, int y, int ancho, int alto) {
        super(x, y, ancho, alto);
        if (imgLadrillo == null) {
            try {
                InputStream is = getClass().getResourceAsStream("/ladrillo.png");
                if(is != null) imgLadrillo = ImageIO.read(is);
            } catch(Exception e){}
        }
    }

    //método que ve si Mario golpea el bloque, si lo golpea, le quita el hitbox
    @Override
    public void reaccionarGolpe(PanelJuego panel) {
        if (activo) {
            activo = false; // si activo es false, Mario golpeó el bloque
            panel.gestorNivel.colisionesSuelo.remove(this.hitbox);
        }
    }

    //método para dibujar el bloque rompible
    @Override
    public void dibujar(Graphics2D g2d) {
        if (!activo) return; // si mario golpea el bloque entonces no se dibuja nada

        if (imgLadrillo != null) {
            g2d.drawImage(imgLadrillo, x, y, ancho, alto, null);
        } else {
            // si no hay imagen para asignarle al bloque, entonces dibujamos y pintamos
            g2d.setColor(new Color(210, 80, 20));
            g2d.fillRect(x, y, ancho, alto);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(x, y, ancho, alto);
        }
    }
}