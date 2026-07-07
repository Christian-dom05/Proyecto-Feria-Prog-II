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

        JLabel titulo = new JLabel("Mario Bros", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 40));
        titulo.setForeground(Color.WHITE);
        titulo.setBounds(0, 150, 768, 50);
        this.add(titulo);

        // boton de inicio
        JButton botonIniciar = new JButton("Iniciar Juego");
        botonIniciar.setFont(new Font("Arial", Font.BOLD, 20));
        botonIniciar.setBounds(284, 300, 200, 50); // Centrado
        botonIniciar.setFocusPainted(false); // Quita el borde al hacer click

        // Acción del botón
        botonIniciar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ventanaPrincipal.cambiarPantallaJuego();
            }
        });

        this.add(botonIniciar);
    }
}