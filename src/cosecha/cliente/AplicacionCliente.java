package cosecha.cliente;

import cosecha.cliente.gui.VentanaPrincipal;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class AplicacionCliente {

    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        int puerto = args.length > 1 ? Integer.parseInt(args[1]) : 5050;

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception e) {
                System.err.println("No se pudo configurar el estilo visual: " + e.getMessage());
            }
            VentanaPrincipal ventana = new VentanaPrincipal(new ClienteConexion(host, puerto));
            ventana.setVisible(true);
        });
    }
}
