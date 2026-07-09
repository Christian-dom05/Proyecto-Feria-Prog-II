import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import javax.imageio.ImageIO;

/**
 * Clase que representa al enemigo en el juego, en este caso, son los goombas
 */
public class Enemigo {
    public int x;
    public double y;
    int velocidadX;
    double velocidadY;
    double gravedad;
    public boolean vivo;
    public boolean enSuelo;
    int tamano;

    static BufferedImage imgGoomba;

    public Enemigo(int x, int y, int tamano) {
        this.x = x; this.y = y; this.tamano = tamano;
        this.velocidadX = -2; this.velocidadY = 0; this.gravedad = 0.4;
        this.vivo = true; this.enSuelo = false;

        // le asignamos la imagen al goomba si es nulo
        if (imgGoomba == null) {
            try {
                InputStream is = getClass().getResourceAsStream("/goomba.png");
                if(is != null) imgGoomba = ImageIO.read(is);
            } catch(Exception e){}
        }
    }

    // devolvemos el hitbox del enemigo, que sería el espacio que chocará con los demás objetos
    public Rectangle obtenerHitbox() { return new Rectangle(x, (int)y, tamano, tamano); }

    // este método se actualiza 60 veces por segundo
    public void actualizar(GestorNivel nivel, Jugador jugador) {
        if (!vivo) return; // si no está vivo no se dibuja
        x += velocidadX; // desplazamiento del enemigo
        Rectangle hitboxX = obtenerHitbox(); // le damos hitbox al enemigo

        for (Rectangle bloque : nivel.colisionesSuelo) { //itera sobre todos los bloques
            if (hitboxX.intersects(bloque)) { // verifica si el hitbox del enemigo interactúa con el hitbox del bloque actual
                // si el enemigo se mueve a la derecha y choca, significa que chocó con la cara izquierda del bloque
                // entonces ajustamos la posicion del enemigo para que no atraviese el bloque
                if (velocidadX > 0) x = bloque.x - tamano;
                // lo mismo que el anterior, pero ahora si se mueve a la izquierda
                else if (velocidadX < 0) x = bloque.x + bloque.width;
                // si choca con algo, invertimos la direccion en la que se movía, de izquierda a derecha o sino de derecha a izquierda
                velocidadX *= -1; break;
            }
        }
        velocidadY += gravedad; // aumenta la velocidad vertical
        y += velocidadY; // el enemigo salta
        enSuelo = false; // si el enemigo salta, enSuelo es false
        Rectangle hitboxY = obtenerHitbox();
        for (Rectangle bloque : nivel.colisionesSuelo) { // recorremos toda la lista de bloques
            if (hitboxY.intersects(bloque)) { // si el enemigo intersecta un bloque...
                // si el enemigo cae
                if (velocidadY > 0) {
                    // lo ponemos encima del bloque, el cálculo es para no atravesar el bloque
                    y = bloque.y - tamano;
                    velocidadY = 0; // la velocidad de caída ahora es cero
                    enSuelo = true; // si toca el suelo
                }
            }
        }
        // si se cae a un precipicio, muere
        if (y > 800) vivo = false;
        // calcula si el jugador se encuentra a menos de 120 pixeles de distancia del enemigo para saltar
        if (enSuelo && Math.abs(this.x - jugador.x) < 120 && jugador.y < this.y) {
            velocidadY = -9.0; // salto del enemigo
            enSuelo = false; // ya no estamos tocando el suelo al saltar, entonces enSuelo se hace false
        }
    }

    // dibujamos al enemigo
    public void dibujar(Graphics2D g2d) {
        if (!vivo) return; // si esta muuerto no dibujamos
        // asignamos la imagen para que se muestre en la interfaz
        if (imgGoomba != null) {
            g2d.drawImage(imgGoomba, x, (int)y, tamano, tamano, null);
        } else {
            // si no se encuentra la imagen dibujamos al enemigo
            g2d.setColor(new Color(139, 69, 19)); g2d.fillRoundRect(x, (int)y, tamano, tamano, 15, 15);
            g2d.setColor(Color.WHITE); g2d.fillRect(x+10, (int)y+15, 10, 15); g2d.fillRect(x+28, (int)y+15, 10, 15);
        }
    }
}