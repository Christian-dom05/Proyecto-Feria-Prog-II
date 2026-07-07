import java.awt.Graphics2D;

public class BloqueInvisibleTrampa extends Bloque {
    public BloqueInvisibleTrampa(int x, int y, int ancho, int alto) {
        super(x, y, ancho, alto);
        this.activo = true;
    }

    @Override
    public void reaccionarGolpe(PanelJuego panel) {}

    @Override
    public void dibujar(Graphics2D g2d) {
    }
}