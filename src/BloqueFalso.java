import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class BloqueFalso extends Bloque {
    boolean cayendo = false;
    double velY = 0;
    double angulo = 0;
    double yReal;
    static BufferedImage imgLadrillo;

    public BloqueFalso(int x, int y, int ancho, int alto) {
        super(x, y, ancho, alto);
        this.yReal = y;

        if (imgLadrillo == null) {
            try {
                java.io.InputStream is = getClass().getResourceAsStream("/ladrillo.png");
                if(is != null) imgLadrillo = ImageIO.read(is);
            } catch(Exception e){}
        }
    }

    @Override
    public void reaccionarGolpe(PanelJuego panel) { }

    @Override
    public void actualizar(PanelJuego panel) {
        if (!cayendo) {
            if (Math.abs(panel.jugador.x - this.x) < panel.TAMANO_BLOQUE && panel.jugador.y > this.y) {
                cayendo = true;
                panel.gestorNivel.colisionesSuelo.remove(this.hitbox);
            }
        } else {
            velY += 0.4;
            yReal += velY;
            this.y = (int) yReal;
            this.hitbox.y = this.y;
            angulo += 0.15;
            if (this.hitbox.intersects(panel.jugador.obtenerHitbox())) panel.activarGameOver();
        }
    }

    @Override
    public void dibujar(Graphics2D g2d) {
        AffineTransform lienzoOriginal = g2d.getTransform();
        if (cayendo) g2d.rotate(angulo, x + ancho / 2.0, y + alto / 2.0);

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