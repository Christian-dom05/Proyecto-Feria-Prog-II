import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.InputStream;

/**
 * Clase que representa al bloque que le dará una pequeña sorpresa al jugador al ser golpeado
 */
public class BloqueSorpresa extends Bloque {
    static BufferedImage imgSorpresa;
    static BufferedImage imgVacio;

    public BloqueSorpresa(int x, int y, int ancho, int alto) {
        super(x, y, ancho, alto);
        try {
            if (imgSorpresa == null) {
                // cargamos la imagen del bloque
                InputStream is1 = getClass().getResourceAsStream("/sorpresa.png");
                if(is1 != null) imgSorpresa = ImageIO.read(is1);
            }
            if (imgVacio == null) {
                InputStream is2 = getClass().getResourceAsStream("/bloque_vacio.png");
                if(is2 != null) imgVacio = ImageIO.read(is2);
            }
        } catch(Exception e){}
    }

    // Si Mario golpea el bloque, entonces aparece un hongo venenoso
    @Override
    public void reaccionarGolpe(PanelJuego panel) {
        if (activo) {
            activo = false;
            // cuando el bloque se rompe, aparece un hongo venenoso
            HongoVenenoso nuevoHongo = new HongoVenenoso(panel, this.x, this.y);
            nuevoHongo.aparecer(this.x, this.y - this.alto);
            panel.hongosMalos.add(nuevoHongo);
        }
    }

    // método para dibujar el bloque sorpresa
    @Override
    public void dibujar(Graphics2D g2d) {
        if (activo) {
            // le asignamos la imagen al bloque
            if (imgSorpresa != null) g2d.drawImage(imgSorpresa, x, y, ancho, alto, null);
            else { g2d.setColor(new Color(255, 204, 0)); g2d.fillRect(x, y, ancho, alto); g2d.setColor(Color.BLACK); g2d.drawRect(x, y, ancho, alto); }
        } else {
            // si se golpea el bloque, asignamos la otra imagen del bloque que ha sido golpeado
            if (imgVacio != null) g2d.drawImage(imgVacio, x, y, ancho, alto, null);
            else {
                // si no hay imágenes, dibujamos el bloque
                g2d.setColor(new Color(139, 69, 19)); g2d.fillRect(x, y, ancho, alto);
                g2d.setColor(Color.BLACK); g2d.drawRect(x, y, ancho, alto);
            }
        }
    }
}