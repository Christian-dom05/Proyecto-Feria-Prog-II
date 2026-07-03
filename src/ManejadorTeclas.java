import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class ManejadorTeclas implements KeyListener {

    public boolean arriba, abajo, izquierda, derecha;

    @Override
    public void keyTyped(KeyEvent e) {
        // No se usa en juegos, pero es obligatorio declararlo
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int codigo = e.getKeyCode();

        // Cuando presionas la tecla, la variable se vuelve verdadera
        if (codigo == KeyEvent.VK_W || codigo == KeyEvent.VK_UP) arriba = true;
        if (codigo == KeyEvent.VK_S || codigo == KeyEvent.VK_DOWN) abajo = true;
        if (codigo == KeyEvent.VK_A || codigo == KeyEvent.VK_LEFT) izquierda = true;
        if (codigo == KeyEvent.VK_D || codigo == KeyEvent.VK_RIGHT) derecha = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int codigo = e.getKeyCode();

        // Cuando sueltas la tecla, la variable se vuelve falsa para que deje de moverse
        if (codigo == KeyEvent.VK_W || codigo == KeyEvent.VK_UP) arriba = false;
        if (codigo == KeyEvent.VK_S || codigo == KeyEvent.VK_DOWN) abajo = false;
        if (codigo == KeyEvent.VK_A || codigo == KeyEvent.VK_LEFT) izquierda = false;
        if (codigo == KeyEvent.VK_D || codigo == KeyEvent.VK_RIGHT) derecha = false;
    }
}