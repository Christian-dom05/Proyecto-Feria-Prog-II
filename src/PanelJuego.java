import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Font;
import java.util.ArrayList;

/**
 * Panel en donde se verá el juego corriendo
 * implementa la interfaz Runnable para que el juego se ejecute en un hilo
 */
public class PanelJuego extends JPanel implements Runnable {
    // dimension de la pantalla
    public final int ANCHO_PANTALLA = 800;
    public final int ALTO_PANTALLA = 600;
    public final int TAMANO_BLOQUE = 48;

    Thread hiloJuego;
    public ManejadorTeclas teclas = new ManejadorTeclas();
    public GestorNivel gestorNivel = new GestorNivel(this);
    public Jugador jugador = new Jugador(this, teclas);

    public ArrayList<HongoVenenoso> hongosMalos = new ArrayList<>();
    public Banderin meta = new Banderin(198 * TAMANO_BLOQUE, 2 * TAMANO_BLOQUE, TAMANO_BLOQUE, 7 * TAMANO_BLOQUE);

    // lista de enemigos
    public Enemigo[] goombas = {
            new Enemigo(22 * TAMANO_BLOQUE, 8 * TAMANO_BLOQUE, TAMANO_BLOQUE),
            new Enemigo(40 * TAMANO_BLOQUE, 8 * TAMANO_BLOQUE, TAMANO_BLOQUE),
            new Enemigo(51 * TAMANO_BLOQUE, 8 * TAMANO_BLOQUE, TAMANO_BLOQUE),
            new Enemigo(80 * TAMANO_BLOQUE, 8 * TAMANO_BLOQUE, TAMANO_BLOQUE),
            new Enemigo(97 * TAMANO_BLOQUE, 8 * TAMANO_BLOQUE, TAMANO_BLOQUE),
            new Enemigo(114 * TAMANO_BLOQUE, 8 * TAMANO_BLOQUE, TAMANO_BLOQUE),
            new Enemigo(124 * TAMANO_BLOQUE, 8 * TAMANO_BLOQUE, TAMANO_BLOQUE),
            new Enemigo(174 * TAMANO_BLOQUE, 8 * TAMANO_BLOQUE, TAMANO_BLOQUE)
    };

    // lista de monedas
    public ArrayList<Moneda> monedas = new ArrayList<>();
    public GestorSonido efectosSonido = new GestorSonido();

    public int offsetCamaraX = 0;
    public boolean juegoGanado = false;
    public boolean juegoTerminado = false;
    public int puntaje = 0;

    public PanelJuego() {
        this.setPreferredSize(new java.awt.Dimension(ANCHO_PANTALLA, ALTO_PANTALLA));
        this.setDoubleBuffered(true);
        this.addKeyListener(teclas);
        this.setFocusable(true);

        // 15 Monedas distribuidas en todo el mapa (Se requieren 1000 pts para ganar)
        monedas.add(new Moneda(18 * TAMANO_BLOQUE, 6 * TAMANO_BLOQUE, TAMANO_BLOQUE, TAMANO_BLOQUE, false));
        monedas.add(new Moneda(25 * TAMANO_BLOQUE, 6 * TAMANO_BLOQUE, TAMANO_BLOQUE, TAMANO_BLOQUE, true)); // moneda trampa
        monedas.add(new Moneda(35 * TAMANO_BLOQUE, 5 * TAMANO_BLOQUE, TAMANO_BLOQUE, TAMANO_BLOQUE, false));
        monedas.add(new Moneda(45 * TAMANO_BLOQUE, 3 * TAMANO_BLOQUE, TAMANO_BLOQUE, TAMANO_BLOQUE, false));
        monedas.add(new Moneda(50 * TAMANO_BLOQUE, 3 * TAMANO_BLOQUE, TAMANO_BLOQUE, TAMANO_BLOQUE, false));
        monedas.add(new Moneda(75 * TAMANO_BLOQUE, 6 * TAMANO_BLOQUE, TAMANO_BLOQUE, TAMANO_BLOQUE, false));
        monedas.add(new Moneda(95 * TAMANO_BLOQUE, 3 * TAMANO_BLOQUE, TAMANO_BLOQUE, TAMANO_BLOQUE, true)); // moneda trampaa
        monedas.add(new Moneda(105 * TAMANO_BLOQUE, 3 * TAMANO_BLOQUE, TAMANO_BLOQUE, TAMANO_BLOQUE, false));
        monedas.add(new Moneda(125 * TAMANO_BLOQUE, 6 * TAMANO_BLOQUE, TAMANO_BLOQUE, TAMANO_BLOQUE, false));
        monedas.add(new Moneda(135 * TAMANO_BLOQUE, 2 * TAMANO_BLOQUE, TAMANO_BLOQUE, TAMANO_BLOQUE, false));
        monedas.add(new Moneda(170 * TAMANO_BLOQUE, 3 * TAMANO_BLOQUE, TAMANO_BLOQUE, TAMANO_BLOQUE, false));
        monedas.add(new Moneda(221 * TAMANO_BLOQUE, 5 * TAMANO_BLOQUE, TAMANO_BLOQUE, TAMANO_BLOQUE, false));
        monedas.add(new Moneda(222 * TAMANO_BLOQUE, 5 * TAMANO_BLOQUE, TAMANO_BLOQUE, TAMANO_BLOQUE, false));
        monedas.add(new Moneda(223 * TAMANO_BLOQUE, 5 * TAMANO_BLOQUE, TAMANO_BLOQUE, TAMANO_BLOQUE, false));
        monedas.add(new Moneda(230 * TAMANO_BLOQUE, 4 * TAMANO_BLOQUE, TAMANO_BLOQUE, TAMANO_BLOQUE, false));
    }

    // hilo en donde correrá el juego
    public void iniciarJuego() { hiloJuego = new Thread(this); hiloJuego.start(); }

    // método que corre el juego en un intérvalo de 60 veces por segundo
    @Override
    public void run() {
        double intervaloDibujo = 1000000000 / 60;
        double delta = 0;
        long ultimoTiempo = System.nanoTime();
        long tiempoActual;

        while (hiloJuego != null) {
            tiempoActual = System.nanoTime(); //consulta el reloj de la computadora con precisión de nanosegundos
            // divide el tiempo que transcurrió desde la última vez que pasó por esta línea entre el tiempo que debería durar un fotograma
            delta += (tiempoActual - ultimoTiempo) / intervaloDibujo;
            // Le dice al programa que el tiempo que acaba de medir ahora es el "último tiempo" conocido, preparándolo para la siguiente vuelta del bucle
            ultimoTiempo = tiempoActual;
            if (delta >= 1) { // verifica si al menos se puede procesar un fotograma completo
                actualizar();
                repaint();
                delta--;
            }
        }
    }

    // cambiamos la variable booleana cuando se pierde el juego
    public void activarGameOver() { if (!juegoGanado) juegoTerminado = true; }

    // metodo para reiniciar nivel cuando presionamos la tecla R
    public void reiniciarNivel() {
        // coordenadas iniciales del jugador---
        jugador.x = 100;
        jugador.y = 100.0;
        jugador.velocidadY = 0;
        //----------
        // reestablecemos todos los atributos a un valor inicial
        juegoTerminado = false;
        juegoGanado = false;
        puntaje = 0;
        meta.cayendo = false;
        meta.y = 2 * TAMANO_BLOQUE;

        // recorremos todos los enemigos y le damos el estado de vivo
        for (Enemigo e : goombas) e.vivo = true;
        // recorremos todas las monedas y le damos el estado de que no han sido recorridas (false)
        for (Moneda m : monedas) m.recogida = false;
        hongosMalos.clear();
        gestorNivel.generarMundo();
    }

    public void actualizar() {
        if ((juegoGanado || juegoTerminado) && teclas.reiniciar) reiniciarNivel();
        if (juegoGanado || juegoTerminado) return;

        jugador.actualizar(gestorNivel);
        meta.actualizar(); // Para que el banderín caiga si se rompe la meta

         // jugador intenta ingresar a la tubería trampa
        if (teclas.abajo && jugador.x > 27 * TAMANO_BLOQUE && jugador.x < 29 * TAMANO_BLOQUE && jugador.y < 8 * TAMANO_BLOQUE) {
            jugador.velocidadY = -35.0; // ¡Lanzamiento a la estratosfera!
        }

        // Tubería Real que lleva a la parte Subterránea
        if (teclas.abajo && jugador.x > 56 * TAMANO_BLOQUE && jugador.x < 58 * TAMANO_BLOQUE && jugador.y < 6 * TAMANO_BLOQUE) {
            jugador.x = 212 * TAMANO_BLOQUE; jugador.y = 2 * TAMANO_BLOQUE;
        }
        // Salir del subterráneo
        if (teclas.derecha && jugador.x > 240 * TAMANO_BLOQUE) {
            jugador.x = 163 * TAMANO_BLOQUE; jugador.y = 5 * TAMANO_BLOQUE;
        }

        // hitbox a la cabeza de Mario
        Rectangle cabezaMario = new Rectangle(jugador.x, (int)jugador.y - 1, TAMANO_BLOQUE, 1);

        //recorre todos los bloques interactivos (bloques trampa)
        for (int i = 0; i < gestorNivel.bloquesInteractivos.size(); i++) {
            Bloque bloque = gestorNivel.bloquesInteractivos.get(i);
            bloque.actualizar(this);
            boolean golpeoDesdeAbajo = false;

            if (bloque instanceof BloqueInvisible) { // si bloque es una instancia de BloqueInvisible
                // si la cabeza de mario choca con el bloque invisible
                if (jugador.velocidadY < 0 && cabezaMario.intersects(bloque.obtenerHitbox())) golpeoDesdeAbajo = true;
            } else {
                if (cabezaMario.intersects(bloque.obtenerHitbox()) && jugador.velocidadY == 0) golpeoDesdeAbajo = true;
            }

            // si Mario intenta golpear un bloque desde abajo
            if (golpeoDesdeAbajo) {
                bloque.reaccionarGolpe(this);
                if (bloque instanceof BloqueInvisible) {
                    jugador.velocidadY = 0;
                    jugador.y = bloque.y + bloque.alto;
                } else jugador.velocidadY = 2.0;
            }
        }

        // ciclo que recorre la lista de hongos venenozos
        for (int i = 0; i < hongosMalos.size(); i++) {
            HongoVenenoso hongo = hongosMalos.get(i);
            hongo.actualizar(gestorNivel);
            // si Mario toca un hongo, activaGameOver()
            if (hongo.activo && jugador.obtenerHitbox().intersects(hongo.obtenerHitbox())) activarGameOver();
        }

        // recorre toda la lista de enemigos (goombas)
        for (Enemigo goomba : goombas) {
            if (goomba.vivo) { // si el goomba está vivo
                if (Math.abs(goomba.x - jugador.x) < ANCHO_PANTALLA + 100) goomba.actualizar(gestorNivel, jugador);
                if (jugador.obtenerHitbox().intersects(goomba.obtenerHitbox())) {
                    // si el jugador choca con un goomba entonces...
                    if (jugador.velocidadY > 0 && jugador.y + TAMANO_BLOQUE - 15 <= goomba.y) {
                        // si lo pisa, entonces el goomba muere
                        goomba.vivo = false;
                        jugador.velocidadY = -8;
                        // si el jugador lo toca  sin pisarlo, o sea, lo toca de costado, entonces muere
                    } else activarGameOver();
                }
            }
        }

        // la Moneda Maldita y monedas normales
        for (Moneda moneda : monedas) {
            // si la moneda no fué recorrida y el jugador la toca
            if (!moneda.recogida && jugador.obtenerHitbox().intersects(moneda.obtenerHitbox())) {
                moneda.recogida = true; // entoonces recogida cambia a true
                if (moneda.esMaldita) {// si la moneda recogida es maldita o es la moneda trampa entonces...
                    HongoVenenoso letal = new HongoVenenoso(this, moneda.x, moneda.y);
                    letal.aparecer(moneda.x, moneda.y); // aparece un hongo venenoso
                    hongosMalos.add(letal);
                } else puntaje += 100; // si es una moneda normal entonces te aumenta 100 de puntos
            }
        }

        // el Banderín Traicionero (1000 Puntos Requeridos para pasar el juego)
        if (jugador.obtenerHitbox().intersects(meta.obtenerHitbox())) { // si el jugador toca el banderín
            if (puntaje >= 1000) juegoGanado = true;// y el puntaje es igual o mayor a 1000 entonces gana
            else if (!meta.cayendo) { // si el puntaje no es igual o mayor a 1000, entonces el banderin se cae
                meta.cayendo = true;
                for (Bloque b : gestorNivel.bloquesInteractivos) {  // recorremos los bloques interactivos
                    if (b instanceof BloqueFalso && b.x == meta.x && b.y == meta.y + meta.alto) {
                        ((BloqueFalso)b).cayendo = true; // Se rompe el piso de la meta
                    }
                }
            }
        }

        // centramos la pantalla
        int centroPantallaX = ANCHO_PANTALLA / 2;
        // movemos la cámara
        offsetCamaraX = jugador.x - centroPantallaX;
        if (offsetCamaraX < 0) offsetCamaraX = 0;
        // cuando Mario lleva a un determinado punto del mapa, la cámara se mueve
        if (offsetCamaraX > 190 * TAMANO_BLOQUE && jugador.x < 205 * TAMANO_BLOQUE) offsetCamaraX = 190 * TAMANO_BLOQUE;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // si el jugador pasa el bloque 205, entonces verá el fondo negro
        if (jugador.x > 205 * TAMANO_BLOQUE) this.setBackground(Color.BLACK);
        else this.setBackground(new Color(107, 140, 255));

        g2d.translate(-offsetCamaraX, 0); // movemos la cámara

        gestorNivel.dibujar(g2d);
        // dibujamos los hongos, monedas y goombas con ciclos for
        for (HongoVenenoso hongo : hongosMalos) hongo.dibujar(g2d);
        for (Moneda moneda : monedas) moneda.dibujar(g2d);
        for (Enemigo goomba : goombas) goomba.dibujar(g2d);

        // dibujamos la meta y el jugador
        meta.dibujar(g2d);
        jugador.dibujar(g2d);

        g2d.translate(offsetCamaraX, 0);

        g2d.setColor(Color.WHITE); g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.drawString("MARIO", 50, 30); g2d.drawString(String.format("%06d", puntaje), 50, 55);

        // Mensaje del Troleo Final si no se consiguieron la suficientes monedas
        if (meta.cayendo && !juegoTerminado && !juegoGanado) {
            g2d.setColor(Color.WHITE); g2d.setFont(new Font("Arial", Font.BOLD, 30));
            g2d.drawString("¡INSUFICIENTES MONEDAS!", ANCHO_PANTALLA / 2 - 200, ALTO_PANTALLA / 2);
        }

        // si se ganó el juego...
        if (juegoGanado) {
            g2d.setColor(new Color(0, 0, 0, 180)); g2d.fillRect(0, 0, ANCHO_PANTALLA, ALTO_PANTALLA);
            g2d.setColor(new Color(255, 215, 0)); g2d.setFont(new Font("Arial", Font.BOLD, 60));
            g2d.drawString("¡NIVEL SUPERADO!", ANCHO_PANTALLA / 2 - 280, ALTO_PANTALLA / 2);
        }

        // si el jugador murió entonces...
        if (juegoTerminado) {
            g2d.setColor(new Color(50, 0, 0, 180)); g2d.fillRect(0, 0, ANCHO_PANTALLA, ALTO_PANTALLA);
            g2d.setColor(Color.RED); g2d.setFont(new Font("Arial", Font.BOLD, 70));
            g2d.drawString("GAME OVER", ANCHO_PANTALLA / 2 - 210, ALTO_PANTALLA / 2 - 20);
            if (System.currentTimeMillis() % 1000 > 500) {
                g2d.setColor(Color.WHITE); g2d.setFont(new Font("Arial", Font.PLAIN, 24));
                g2d.drawString("Presiona 'R' o 'ENTER' para reiniciar", ANCHO_PANTALLA / 2 - 210, (ALTO_PANTALLA / 2) + 40);
            }
        }
        // limpiamos la pantalla
        g2d.dispose();
    }
}