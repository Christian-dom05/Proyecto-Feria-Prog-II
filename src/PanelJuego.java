import javax.swing.JPanel;
import java.awt.*;

public class PanelJuego extends JPanel implements Runnable {

    // 1. CONFIGURACIONES DE PANTALLA
    final int TAMANO_BLOQUE = 48; // Escala base para los bloques (ej. 48x48 píxeles)
    final int COLUMNAS_PANTALLA = 16;
    final int FILAS_PANTALLA = 12;

    // Resolución final: 768 x 576 píxeles
    final int ANCHO_PANTALLA = TAMANO_BLOQUE * COLUMNAS_PANTALLA;
    final int ALTO_PANTALLA = TAMANO_BLOQUE * FILAS_PANTALLA;

    // 2. SISTEMA DE HILOS Y FPS
    int FPS = 60;
    Thread hiloJuego;

    ManejadorTeclas teclas = new ManejadorTeclas();
    Jugador jugador = new Jugador(this, teclas);
    GestorNivel gestorNivel = new GestorNivel(this);
    HongoVenenoso hongoMalo = new HongoVenenoso(this, 0, 0);

    // TRAMPA 1: El bloque oculto original
    BloqueInvisible bloqueOculto = new BloqueInvisible(500, 300, TAMANO_BLOQUE, TAMANO_BLOQUE);

    // TRAMPA 2: El nuevo bloque sorpresa amarillo
    BloqueSorpresa bloqueSorpresa = new BloqueSorpresa(300, 336, TAMANO_BLOQUE, TAMANO_BLOQUE);
    // NUEVA VARIABLE: La posición de la cámara
    int offsetCamaraX = 0;




    public PanelJuego() {
        this.setPreferredSize(new Dimension(ANCHO_PANTALLA, ALTO_PANTALLA));
        this.setBackground(Color.BLACK); // Color de fondo temporal
        this.setDoubleBuffered(true); // Mejora el rendimiento del renderizado en Swing

        this.addKeyListener(teclas);
        this.setFocusable(true); // Esto es VITAL para que el panel pueda recibir las pulsaciones del teclado
        // TRUCO: Añadimos la caja del bloque a la lista de colisiones del mapa.
        // Añadimos el bloque SORPRESA para que Mario pueda pararse sobre él
        gestorNivel.colisionesSuelo.add(bloqueSorpresa.obtenerHitbox());
    }

    public void iniciarJuego() {
        hiloJuego = new Thread(this);
        hiloJuego.start(); // Esto llama automáticamente al método run()
    }

    @Override
    public void run() {
        // Calculamos cuánto tiempo debe durar cada "frame" en nanosegundos
        double intervaloDibujo = 1000000000.0 / FPS; // 0.01666 segundos por frame
        double siguienteTiempoDibujo = System.nanoTime() + intervaloDibujo;

        // Bucle principal del juego (Game Loop)
        while (hiloJuego != null) {

            // 1. ACTUALIZAR: posiciones, físicas, colisiones, trampas
            actualizar();

            // 2. DIBUJAR: repintar la pantalla con los nuevos datos
            repaint(); // Esto llama automáticamente al método paintComponent()

            // 3. ESPERAR: detener el hilo los milisegundos restantes para clavar los 60 FPS
            try {
                double tiempoRestante = siguienteTiempoDibujo - System.nanoTime();
                tiempoRestante = tiempoRestante / 1000000; // Convertimos de nanosegundos a milisegundos

                // Si actualizó y dibujó muy rápido, esperamos
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
        // 1. ACTUALIZAR FÍSICAS DE MARIO
        jugador.actualizar(gestorNivel);

        // 2. LÓGICA DEL BLOQUE SORPRESA (La trampa del hongo)
        if (!bloqueSorpresa.usado) {
            // Hitbox minúsculo sobre la cabeza de Mario
            Rectangle cabezaMario = new Rectangle(jugador.x, (int)jugador.y - 1, TAMANO_BLOQUE, 1);

            if (cabezaMario.intersects(bloqueSorpresa.obtenerHitbox())) {
                bloqueSorpresa.usado = true;
                hongoMalo.aparecer(bloqueSorpresa.x, bloqueSorpresa.y - TAMANO_BLOQUE);
            }
        }

        // 3. LÓGICA DEL BLOQUE INVISIBLE (La trampa del pozo)
        if (jugador.obtenerHitbox().intersects(bloqueOculto.obtenerHitbox())) {
            if (jugador.velocidadY < 0 && !bloqueOculto.descubierto) {
                bloqueOculto.descubierto = true;
                jugador.y = bloqueOculto.y + bloqueOculto.alto;
                jugador.velocidadY = 0;
                gestorNivel.colisionesSuelo.add(bloqueOculto.obtenerHitbox()); // Se vuelve sólido
            }
        }

        // 4. ACTUALIZAR FÍSICAS DEL HONGO
        hongoMalo.actualizar(gestorNivel);

        // 5. LÓGICA DE MUERTE (Si Mario toca el hongo)
        if (hongoMalo.activo && jugador.obtenerHitbox().intersects(hongoMalo.obtenerHitbox())) {
            System.out.println("¡Trolleado! Te comiste el hongo venenoso.");

            // Regresamos a Mario al inicio
            jugador.x = 100;
            jugador.y = 100.0;

            // Reseteamos la trampa para que vuelva a caer si lo intenta de nuevo
            hongoMalo.activo = false;
            bloqueSorpresa.usado = false;
        }

        // 6. LÓGICA DE LA CÁMARA (El Scrolling)
        // Esto siempre debe ir al final para que la cámara persiga a Mario después de que se movió
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

        // FONDO (Cielo de Super Mario - Color azul claro)
        this.setBackground(new Color(107, 140, 255));

        // INICIO DE LA CÁMARA
        // Todo lo que se dibuje después de esta línea, se desplazará
        g2d.translate(-offsetCamaraX, 0);

        // DIBUJAMOS EL MUNDO (El orden importa: Mapa -> Trampas -> Entidades -> Jugador)
        gestorNivel.dibujar(g2d);
        bloqueOculto.dibujar(g2d);
        bloqueSorpresa.dibujar(g2d);
        hongoMalo.dibujar(g2d);
        jugador.dibujar(g2d);

        // FIN DE LA CÁMARA (Opcional, pero buena práctica si luego quieres dibujar UI estática como el puntaje)
        g2d.translate(offsetCamaraX, 0);

        g2d.dispose();
    }
}