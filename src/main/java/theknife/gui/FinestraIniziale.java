/*
 * Autori del progetto:
 * - Alessandro Frigerio, Matricola: 759926, Sede: CO
 * - Antonio Pardo, Matricola: 760613, Sede: CO
 * - Davide Moretti, Matricola: 762176, Sede: CO
 */
package theknife.gui;

import theknife.essenziali.Utente;
import theknife.gestore.ControllerTheKnife;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;

/**
 * Finestra iniziale dell'applicazione TheKnife (Slide 14).
 * <p>
 * Permette all'utente di autenticarsi (come cliente o ristoratore), di registrare un nuovo
 * profilo oppure di accedere direttamente come utente <code>guest</code> indicando un luogo.
 * @author Alessandro Frigerio
 * @author Antonio Pardo
 * @author Davide Moretti
 */
public class FinestraIniziale extends JFrame {

    /**
     * Controller del client che gestisce la sessione e la comunicazione col server <code>controller</code>.
     */
    private final ControllerTheKnife controller;

    /**
     * Campo di testo per lo username nel pannello di login <code>txtUsername</code>.
     */
    private JTextField txtUsername;

    /**
     * Campo per la password nel pannello di login <code>txtPassword</code>.
     */
    private JPasswordField txtPassword;

    /**
     * Campo di testo per il luogo di riferimento dell'utente Guest <code>txtLuogoGuest</code>.
     */
    private JTextField txtLuogoGuest;

    /**
     * Costruttore che inizializza la schermata iniziale di benvenuto.
     * @param controller il controller attivo del client
     */
    public FinestraIniziale(ControllerTheKnife controller) {
        super("TheKnife - Scopri e Recensisci Ristoranti nel Mondo");
        this.controller = controller;
        inizializzaGUI();
    }

    /**
     * Configura i pannelli e i componenti grafici della finestra con tema bianco e verde.
     */
    private void inizializzaGUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(850, 520);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(StileTheKnife.GRIGIO_SFONDO);
        setLayout(new BorderLayout());

        // 1. BANNER SUPERIORE VERDE
        JPanel panelBanner = new JPanel();
        panelBanner.setBackground(StileTheKnife.VERDE_THEKNIFE);
        panelBanner.setLayout(new BoxLayout(panelBanner, BoxLayout.Y_AXIS));
        panelBanner.setBorder(BorderFactory.createEmptyBorder(22, 30, 22, 30));

        JLabel lblTitolo = new JLabel("THE KNIFE");
        lblTitolo.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 28));
        lblTitolo.setForeground(StileTheKnife.BIANCO);
        lblTitolo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSottotitolo = new JLabel("Trova i migliori ristoranti, consulta le recensioni e condividi la tua esperienza");
        lblSottotitolo.setFont(StileTheKnife.FONT_NORMALE);
        lblSottotitolo.setForeground(StileTheKnife.VERDE_CHIARO_SFONDO);
        lblSottotitolo.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelBanner.add(lblTitolo);
        panelBanner.add(Box.createVerticalStrut(6));
        panelBanner.add(lblSottotitolo);
        add(panelBanner, BorderLayout.NORTH);

        // 2. CORPO CENTRALE CON DUE CARD AFFIANCATE (LOGIN vs GUEST)
        JPanel panelCentrale = new JPanel(new GridLayout(1, 2, 25, 0));
        panelCentrale.setBackground(StileTheKnife.GRIGIO_SFONDO);
        panelCentrale.setBorder(BorderFactory.createEmptyBorder(30, 35, 30, 35));

        panelCentrale.add(creaCardLogin());
        panelCentrale.add(creaCardGuest());

        add(panelCentrale, BorderLayout.CENTER);
    }

    /**
     * Costruisce la scheda di sinistra dedicata al Login e alla Registrazione.
     * @return il pannello <code>JPanel</code> configurato
     */
    private JPanel creaCardLogin() {
        JPanel card = StileTheKnife.creaCardBianca();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel lblIntestazione = new JLabel("Accedi al tuo Account");
        lblIntestazione.setFont(StileTheKnife.FONT_SOTTOTITOLO);
        lblIntestazione.setForeground(StileTheKnife.VERDE_THEKNIFE);
        lblIntestazione.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblDesc = StileTheKnife.creaEtichetta("Per clienti e ristoratori registrati");
        lblDesc.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblUser = StileTheKnife.creaEtichetta("Username / Email:");
        lblUser.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtUsername = StileTheKnife.creaCampoTesto(20);
        txtUsername.setAlignmentX(Component.LEFT_ALIGNMENT);
        StileTheKnife.fissaDimensioneInput(txtUsername, 320);

        JLabel lblPass = StileTheKnife.creaEtichetta("Password:");
        lblPass.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtPassword = StileTheKnife.creaCampoPassword(20);
        txtPassword.setAlignmentX(Component.LEFT_ALIGNMENT);
        StileTheKnife.fissaDimensioneInput(txtPassword, 320);

        JButton btnLogin = StileTheKnife.creaBottonePrimario("Accedi alla Piattaforma");
        btnLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLogin.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 42));

        JButton btnRegistrati = StileTheKnife.creaBottoneSecondario("Non hai un account? Registrati ora");
        btnRegistrati.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnRegistrati.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 40));

        // Azioni
        btnLogin.addActionListener(e -> gestisciLogin());
        txtPassword.addActionListener(e -> gestisciLogin());
        btnRegistrati.addActionListener(e -> {
            DialogRegistrazione dialog = new DialogRegistrazione(this, controller);
            dialog.setVisible(true);
        });

        card.add(lblIntestazione);
        card.add(Box.createVerticalStrut(4));
        card.add(lblDesc);
        card.add(Box.createVerticalStrut(20));
        card.add(lblUser);
        card.add(Box.createVerticalStrut(5));
        card.add(txtUsername);
        card.add(Box.createVerticalStrut(15));
        card.add(lblPass);
        card.add(Box.createVerticalStrut(5));
        card.add(txtPassword);
        card.add(Box.createVerticalStrut(25));
        card.add(btnLogin);
        card.add(Box.createVerticalStrut(12));
        card.add(btnRegistrati);

        return card;
    }

    /**
     * Costruisce la scheda di destra dedicata all'accesso rapido come utente Guest.
     * @return il pannello <code>JPanel</code> configurato
     */
    private JPanel creaCardGuest() {
        JPanel card = StileTheKnife.creaCardBianca();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel lblIntestazione = new JLabel("Esplora come Ospite (Guest)");
        lblIntestazione.setFont(StileTheKnife.FONT_SOTTOTITOLO);
        lblIntestazione.setForeground(StileTheKnife.VERDE_THEKNIFE);
        lblIntestazione.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblInfo = new JLabel("<html>Non vuoi registrarti subito? Inserisci il nome di una città o nazione "
                + "per consultare liberamente i ristoranti vicini, usare i filtri di ricerca e leggere "
                + "le recensioni in forma anonima.</html>");
        lblInfo.setFont(StileTheKnife.FONT_NORMALE);
        lblInfo.setForeground(StileTheKnife.TESTO_SCURO);
        lblInfo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblLuogo = StileTheKnife.creaEtichetta("Inserisci un luogo (es. Vienna, Como, Italy):");
        lblLuogo.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtLuogoGuest = StileTheKnife.creaCampoTesto(20);
        txtLuogoGuest.setAlignmentX(Component.LEFT_ALIGNMENT);
        StileTheKnife.fissaDimensioneInput(txtLuogoGuest, 320);

        JButton btnGuest = StileTheKnife.creaBottonePrimario("Continua come Guest");
        btnGuest.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnGuest.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 42));

        // Riquadro informativo verde chiaro in basso
        JPanel boxNote = new JPanel(new BorderLayout());
        boxNote.setBackground(StileTheKnife.VERDE_CHIARO_SFONDO);
        boxNote.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        boxNote.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lblNota = new JLabel("<html><small><b>Nota:</b> Potrai registrarti o fare il login in qualsiasi momento dalla barra superiore.</small></html>", SwingConstants.LEFT);
        lblNota.setForeground(StileTheKnife.VERDE_THEKNIFE);
        boxNote.add(lblNota, BorderLayout.CENTER);

        // Azioni
        btnGuest.addActionListener(e -> gestisciAccessoGuest());
        txtLuogoGuest.addActionListener(e -> gestisciAccessoGuest());

        card.add(lblIntestazione);
        card.add(Box.createVerticalStrut(12));
        card.add(lblInfo);
        card.add(Box.createVerticalStrut(25));
        card.add(lblLuogo);
        card.add(Box.createVerticalStrut(6));
        card.add(txtLuogoGuest);
        card.add(Box.createVerticalStrut(25));
        card.add(btnGuest);
        card.add(Box.createVerticalGlue());
        card.add(boxNote);

        return card;
    }

    /**
     * Esegue il login tramite il controller e apre la schermata corrispondente al ruolo (Cliente o Ristoratore).
     */
    private void gestisciLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Inserisci sia lo username che la password per accedere.",
                    "Dati Mancanti", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (controller.login(username, password)) {
            Utente loggato = controller.getSessione().getUtenteCorrente();
            apriSchermataPrincipale(loggato);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Username o password non corretti. Riprova.",
                    "Accesso Negato", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Avvia la sessione Guest con il luogo inserito e apre la schermata di esplorazione ristoranti.
     */
    private void gestisciAccessoGuest() {
        String luogo = txtLuogoGuest.getText().trim();
        if (luogo.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Per proseguire come utente Guest devi indicare il nome di un luogo (es. Vienna).",
                    "Luogo Obbligatorio", JOptionPane.WARNING_MESSAGE);
            return;
        }

        controller.accediComeGuest(luogo);
        apriSchermataPrincipale(controller.getSessione().getUtenteCorrente());
    }

    /**
     * Smista l'utente verso la dashboard Cliente/Guest oppure verso la dashboard Ristoratore.
     * @param utenteAttivo l'utente attualmente presente nella sessione
     */
    private void apriSchermataPrincipale(Utente utenteAttivo) {
        dispose(); // Chiude il menù iniziale
        if (utenteAttivo.isRistoratore()) {
            //new FinestraRistoratore(controller).setVisible(true);
        } else {
          //  new FinestraClienteGuest(controller).setVisible(true);
        }
    }
}