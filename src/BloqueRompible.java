import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class BloqueRompible extends Bloque {
    static BufferedImage imgLadrillo;

    public BloqueRompible(int x, int y, int ancho, int alto) {
        super(x, y, ancho, alto);
        if (imgLadrillo == null) {
            try {
                java.io.InputStream is = getClass().getResourceAsStream("/ladrillo.png");
                if(is != null) imgLadrillo = ImageIO.read(is);
            } catch(Exception e){}
        }
    }

    @Override
    public void reaccionarGolpe(PanelJuego panel) {
        if (activo) {
            activo = false;
            panel.gestorNivel.colisionesSuelo.remove(this.hitbox);
        }
    }

    @Override
    public void dibujar(Graphics2D g2d) {
        if (!activo) return;

        if (imgLadrillo != null) {
            g2d.drawImage(imgLadrillo, x, y, ancho, alto, null);
        } else {
            g2d.setColor(new Color(210, 80, 20));
            g2d.fillRect(x, y, ancho, alto);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(x, y, ancho, alto);
        }
    }
}