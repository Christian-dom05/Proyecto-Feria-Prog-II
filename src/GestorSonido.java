import java.net.URL;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class GestorSonido {

    public void reproducir(String rutaSonido) {
        try {
            // Buscamos el archivo de audio en la carpeta de recursos
            URL url = getClass().getResource(rutaSonido);

            if (url != null) {
                AudioInputStream audioEntrada = AudioSystem.getAudioInputStream(url);
                Clip clip = AudioSystem.getClip();
                clip.open(audioEntrada);
                clip.start(); // Reproduce el sonido una sola vez
            } else {
                System.out.println("No se encontró el audio: " + rutaSonido);
            }
        } catch (Exception e) {
            System.out.println("Error al reproducir el sonido.");
            e.printStackTrace();
        }
    }
}