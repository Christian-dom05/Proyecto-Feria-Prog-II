import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class PanelLobby extends JPanel {

    VentanaJuego ventanaPrincipal;

    public PanelLobby(VentanaJuego ventana) {
        this.ventanaPrincipal = ventana;

        // Configuramos el diseño del panel
        this.setLayout(null); // Usamos null para posicionar el botón libremente con coordenadas
        this.setBackground(new Color(107, 140, 255)); // Mismo azul del cielo de Mario

        // TÍTULO DEL JUEGO
        JLabel titulo = new JLabel("SUPER TROLL BROS", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 40));
        titulo.setForeground(Color.WHITE);
        titulo.setBounds(0, 150, 768, 50); // Ajusta el 768 al ANCHO_PANTALLA de tu juego
        this.add(titulo);

        // BOTÓN DE INICIO
        JButton botonIniciar = new JButton("Iniciar Juego");
        botonIniciar.setFont(new Font("Arial", Font.BOLD, 20));
        botonIniciar.setBounds(284, 300, 200, 50); // Centrado (768/2 - 100)
        botonIniciar.setFocusPainted(false); // Quita el borde feo al hacer clic

        // Acción del botón
        botonIniciar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Al hacer clic, le decimos a la ventana principal que cambie la "carta"
                ventanaPrincipal.cambiarPantallaJuego();
            }
        });

        this.add(botonIniciar);
    }
}