import java.awt.CardLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class VentanaJuego extends JFrame {

    CardLayout gestorCartas;
    JPanel panelContenedor;

    PanelLobby panelLobby;
    PanelJuego panelJuego;

    public VentanaJuego() {
        this.setTitle("Juego Troll - Programación II");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);

        // 1. Inicializamos el CardLayout y el panel que contendrá a los demás
        gestorCartas = new CardLayout();
        panelContenedor = new JPanel(gestorCartas);

        // 2. Instanciamos nuestras dos pantallas
        panelLobby = new PanelLobby(this); // Le pasamos 'this' para que el botón pueda llamarnos
        panelJuego = new PanelJuego();

        // 3. Añadimos las pantallas a la "baraja" con un nombre clave
        panelContenedor.add(panelLobby, "PantallaLobby");
        panelContenedor.add(panelJuego, "PantallaJuego");

        // Añadimos el contenedor principal a la ventana
        this.add(panelContenedor);

        // El pack ajustará la ventana al tamaño preferido del PanelJuego
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);

        // Por defecto, mostramos el Lobby primero
        gestorCartas.show(panelContenedor, "PantallaLobby");
    }

    // Este método es llamado por el botón "Iniciar Juego" del Lobby
    public void cambiarPantallaJuego() {
        // Cambiamos la vista
        gestorCartas.show(panelContenedor, "PantallaJuego");

        // AHORA SÍ, iniciamos el hilo del juego (los 60 FPS)
        panelJuego.iniciarJuego();

        // ¡CRÍTICO EN SWING! Como hicimos clic en un botón, el foco del teclado se quedó ahí.
        // Debemos devolverle el foco al panel del juego para que Mario pueda moverse.
        panelJuego.requestFocusInWindow();
    }

    public static void main(String[] args) {
        new VentanaJuego();
    }
}