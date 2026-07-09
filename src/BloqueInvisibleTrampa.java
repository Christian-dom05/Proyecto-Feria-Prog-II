import java.awt.Graphics2D;

/**
 * Esta clase representa un bloque especial que nunca se hará visible, solo sirve
 * para que el jugador use para acercarse más y llegar de un salto a la plataforma que, de otra forma
 * nunca llegará
 */
public class BloqueInvisibleTrampa extends Bloque {
    public BloqueInvisibleTrampa(int x, int y, int ancho, int alto) {
        super(x, y, ancho, alto);
        this.activo = true;
    }

    @Override
    public void reaccionarGolpe(PanelJuego panel) {}

    // este bloque no se dibuja, es totalmente invisible
    @Override
    public void dibujar(Graphics2D g2d) {
    }
}