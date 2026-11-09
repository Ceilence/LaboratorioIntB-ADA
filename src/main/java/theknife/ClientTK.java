/*
 * Autori del progetto:
 * - Alessandro Frigerio, Matricola: 759926, Sede: CO
 * - Antonio Pardo, Matricola: 760613, Sede: CO
 * - Davide Moretti, Matricola: 762176, Sede: CO
 */
package theknife;

import theknife.gestore.ControllerTheKnife;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Classe principale di avvio del modulo <code>clientTK</code> dell'applicazione TheKnife.
 * <p>
 * Gestisce il collegamento iniziale in rete con il modulo <code>serverTK</code> tramite il
 * <code>ControllerTheKnife</code> e avvia l'interfaccia grafica utente (GUI).
 * @author Alessandro Frigerio
 * @author Antonio Pardo
 * @author Davide Moretti
 */
public class ClientTK {

    /**
     * Costruttore vuoto della classe <code>ClientTK</code>.
     */
    public ClientTK() {
    }

    /**
     * Metodo principale che avvia il client, verifica la connessione col server e apre la GUI.
     * @param args argomenti da riga di comando (opzionale: indirizzo IP del server in <code>args[0]</code>)
     */
    public static void main(String[] args) {
        // Imposta lo stile grafico del sistema operativo
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        String hostServer = (args.length > 0 && !args[0].trim().isEmpty()) ? args[0].trim() : "localhost";
        ControllerTheKnife controller = new ControllerTheKnife(hostServer);

        // Se il server non è raggiungibile, chiede l'ip tramite finestra di dialogo
        while (!controller.connettiAlServer(hostServer)) {
            String nuovoHost = (String) JOptionPane.showInputDialog(
                    null,
                    "Impossibile connettersi a serverTK su '" + hostServer + "'.\n"
                            + "Assicurati che ServerTK sia avviato e inserisci l'indirizzo IP del server:",
                    "Connessione al Server TheKnife",
                    JOptionPane.WARNING_MESSAGE,
                    null,
                    null,
                    hostServer
            );

            if (nuovoHost == null) {
                System.out.println("Avvio di clientTK annullato dall'utente.");
                System.exit(0);
            }
            hostServer = nuovoHost.trim().isEmpty() ? "localhost" : nuovoHost.trim();
        }

        System.out.println("clientTK connesso con successo a serverTK (" + hostServer + ")");

        // Avvio dell'interfaccia grafica
        SwingUtilities.invokeLater(() -> {
            new theknife.gui.FinestraIniziale(controller).setVisible(true);
        });
    }
}