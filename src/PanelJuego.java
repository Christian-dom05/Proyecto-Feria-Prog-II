import javax.swing.JPanel;
import java.awt.*;
import java.awt.Font;
public class PanelJuego extends JPanel implements Runnable {

    // configuracion de pantalla
    final int TAMANO_BLOQUE = 48;
    final int COLUMNAS_PANTALLA = 16;
    final int FILAS_PANTALLA = 12;

    // Resolución pantalla 768 x 576 píxeles
    final int ANCHO_PANTALLA = TAMANO_BLOQUE * COLUMNAS_PANTALLA;
    final int ALTO_PANTALLA = TAMANO_BLOQUE * FILAS_PANTALLA;

    // FPS
    int FPS = 60;
    Thread hiloJuego;

    ManejadorTeclas teclas = new ManejadorTeclas();
    Jugador jugador = new Jugador(this, teclas);
    GestorNivel gestorNivel = new GestorNivel(this);
    HongoVenenoso hongoMalo = new HongoVenenoso(this, 0, 0);

    // trampa del bloque oculto original
    BloqueInvisible bloqueOculto = new BloqueInvisible(500, 300, TAMANO_BLOQUE, TAMANO_BLOQUE);

    // trampa dell nuevo bloque sorpresa amarillo
    BloqueSorpresa bloqueSorpresa = new BloqueSorpresa(300, 336, TAMANO_BLOQUE, TAMANO_BLOQUE);
    // aqui vamos creando enemigos
    Enemigo[] goombas = {
            new Enemigo(25 * TAMANO_BLOQUE, 8 * TAMANO_BLOQUE, TAMANO_BLOQUE),
            new Enemigo(42 * TAMANO_BLOQUE, 8 * TAMANO_BLOQUE, TAMANO_BLOQUE)
    };
    // variable que indica la posicion de la camara
    int offsetCamaraX = 0;
    Banderin meta = new Banderin(85 * TAMANO_BLOQUE, 2 * TAMANO_BLOQUE, TAMANO_BLOQUE, 7 * TAMANO_BLOQUE);

    // Estado del juego
    boolean juegoGanado = false;



    public PanelJuego() {
        this.setPreferredSize(new Dimension(ANCHO_PANTALLA, ALTO_PANTALLA));
        this.setBackground(Color.BLACK); // Color de fondo temporal
        this.setDoubleBuffered(true); // Mejora el rendimiento del renderizado en Swing

        this.addKeyListener(teclas);
        this.setFocusable(true); // Esto es importante para que el panel pueda recibir las pulsaciones del teclado
        gestorNivel.colisionesSuelo.add(bloqueSorpresa.obtenerHitbox());
    }

    public void iniciarJuego() {
        hiloJuego = new Thread(this);
        hiloJuego.start(); // Esto llama automáticamente al método run()
    }

    @Override
    public void run() {
        // Calculamos cuánto tiempo debe durar cada "frame" en nanosegundos
        double intervaloDibujo = 1000000000.0 / FPS; // 0.01666 segundos
        double siguienteTiempoDibujo = System.nanoTime() + intervaloDibujo;

        // Bucle principal del juego
        while (hiloJuego != null) {

            // actualizar osiciones, físicas, colisiones, trampas
            actualizar();

            // dibujar, repintar la pantalla con los nuevos datos
            repaint(); // Esto llama automáticamente al método paintComponent()

            // esperar, detener el hilo los milisegundos restantes para clavar los 60 FPS
            try {
                double tiempoRestante = siguienteTiempoDibujo - System.nanoTime();
                tiempoRestante = tiempoRestante / 1000000; // Convertimos de nanosegundos a milisegundos

                // Si actualizó y dibujó muy rápido
                if (tiempoRestante < 0) {
                    tiempoRestante = 0;
                }

                Thread.sleep((long) tiempoRestante);

                siguienteTiempoDibujo += intervaloDibujo;

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void actualizar() {
        // Si ya ganamos, congelamos todo y no calculamos físicas
        if (juegoGanado) {
            return;
        }
        jugador.actualizar(gestorNivel);

        if (!bloqueSorpresa.usado) {
            // hitbox sobre mario
            Rectangle cabezaMario = new Rectangle(jugador.x, (int)jugador.y - 1, TAMANO_BLOQUE, 1);

            if (cabezaMario.intersects(bloqueSorpresa.obtenerHitbox())) {
                bloqueSorpresa.usado = true;
                hongoMalo.aparecer(bloqueSorpresa.x, bloqueSorpresa.y - TAMANO_BLOQUE);
            }
        }

        if (jugador.obtenerHitbox().intersects(bloqueOculto.obtenerHitbox())) {
            if (jugador.velocidadY < 0 && !bloqueOculto.descubierto) {
                bloqueOculto.descubierto = true;
                jugador.y = bloqueOculto.y + bloqueOculto.alto;
                jugador.velocidadY = 0;
                gestorNivel.colisionesSuelo.add(bloqueOculto.obtenerHitbox()); // Se vuelve sólido
            }
        }

        // actualia fisivas del hongo
        hongoMalo.actualizar(gestorNivel);

        if (hongoMalo.activo && jugador.obtenerHitbox().intersects(hongoMalo.obtenerHitbox())) {
            System.out.println("te comiste el hongo venenoso.");

            // Regresamos a Mario al inicio
            jugador.x = 100;
            jugador.y = 100.0;

            // Reseteamos la trampa para que vuelva a caer si lo intenta de nuevo
            hongoMalo.activo = false;
            bloqueSorpresa.usado = false;
        }

        if (jugador.obtenerHitbox().intersects(meta.obtenerHitbox())) {
            juegoGanado = true;
        }

        for (Enemigo goomba : goombas) {
            if (goomba.vivo) {
                // solo actualizamos al enemigo si Mario está en la misma pantalla
                if (Math.abs(goomba.x - jugador.x) < ANCHO_PANTALLA + 100) {
                    goomba.actualizar(gestorNivel);
                }
                // Si Mario choca con el enemigo
                if (jugador.obtenerHitbox().intersects(goomba.obtenerHitbox())) {

                    // mario debe estar cayendo Y su pie debe estar arriba del enemigo
                    if (jugador.velocidadY > 0 && jugador.y + TAMANO_BLOQUE - 15 <= goomba.y) {
                        goomba.vivo = false; // El enemigo muere
                        jugador.velocidadY = -8; // Mario da un pequeño rebote en el aire
                    } else {
                        // Si no lo aplastó, Mario muere
                        System.out.println("¡Un enemigo te ha atrapado!");
                        jugador.x = 100;
                        jugador.y = 100.0;

                        // Resucitamos a los enemigos al morir Mario para que no sea trampa
                        for (Enemigo e : goombas) {
                            e.vivo = true;
                        }
                    }
                }
            }
        }

        int centroPantallaX = ANCHO_PANTALLA / 2;
        offsetCamaraX = jugador.x - centroPantallaX;

        // Evitamos que la cámara muestre áreas a la izquierda del nivel
        if (offsetCamaraX < 0) {
            offsetCamaraX = 0;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // FONDO cielo de Super Mario - Color azul claro
        this.setBackground(new Color(107, 140, 255));

        // Todo lo que se dibuje después de esta línea, se desplazará
        g2d.translate(-offsetCamaraX, 0);


        gestorNivel.dibujar(g2d);
        bloqueOculto.dibujar(g2d);
        bloqueSorpresa.dibujar(g2d);
        hongoMalo.dibujar(g2d);

        // Dibujamos los enemigos
        for (Enemigo goomba : goombas) {
            goomba.dibujar(g2d);
        }

        meta.dibujar(g2d);
        jugador.dibujar(g2d);

        g2d.translate(offsetCamaraX, 0);

        if (juegoGanado) {
            g2d.setColor(new Color(0, 0, 0, 180));
            g2d.fillRect(0, 0, ANCHO_PANTALLA, ALTO_PANTALLA);

            g2d.setColor(new Color(255, 215, 0));
            g2d.setFont(new Font("Arial", Font.BOLD, 60));
            g2d.drawString("¡nivel terminado!", ANCHO_PANTALLA / 2 - 280, ALTO_PANTALLA / 2);

            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.PLAIN, 20));
            g2d.drawString("gracias", ANCHO_PANTALLA / 2 - 140, (ALTO_PANTALLA / 2) + 50);
        }

        g2d.dispose();
    }
}