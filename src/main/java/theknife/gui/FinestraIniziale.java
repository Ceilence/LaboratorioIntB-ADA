/*
- Autori del progetto:

- Alessandro Frigerio, Matricola: 759926, Sede: CO

- Antonio Pardo, Matricola: 760613, Sede: CO

- Davide Moretti, Matricola: 762176, Sede: CO

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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

/**
 * Finestra iniziale di TheKnife.
 * Gestisce l'interfaccia per il login degli utenti registrati e l'accesso rapido come ospite.
 *
 * @author Alessandro Frigerio
 * @author Antonio Pardo
 * @author Davide Moretti
 */
public class FinestraIniziale extends JFrame {

    /** Riferimento al controller principale. */
    private final ControllerTheKnife controller;

    /** Campo di testo per l'inserimento dell'username o email dell'utente. */
    private JTextField txtUsername;

    /** Campo di testo per l'inserimento della password. */
    private JPasswordField txtPassword;

    /** Campo di testo per l'inserimento del luogo di ricerca per l'ospite. */
    private JTextField txtLuogoGuest;

    /**
     * Costruttore della finestra iniziale.
     *
     * @param controller Il controller principale.
     */
    public FinestraIniziale(ControllerTheKnife controller) {
        super("TheKnife");
        this.controller = controller;
        inizializzaGUI();
    }

    /**
     * Inizializza le componenti grafiche della finestra e imposta il layout principale.
     * Costruisce il pannello superiore e i pannelli per l'autenticazione.
     */
    private void inizializzaGUI() {
        StileTheKnife.applicaIconaFinestra(this);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(StileTheKnife.VERDE_THEKNIFE);
        setLayout(new BorderLayout());

        // Pannello superiore
        JPanel panelBanner = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panelBanner.setBackground(StileTheKnife.VERDE_THEKNIFE);
        panelBanner.setBorder(BorderFactory.createEmptyBorder(18, 16, 18, 16));

        JLabel lblLogo = StileTheKnife.creaLabelLogo(64);
        panelBanner.add(lblLogo);
        panelBanner.add(Box.createHorizontalStrut(16));

        JPanel testiBanner = new JPanel();
        testiBanner.setOpaque(false);
        testiBanner.setLayout(new BoxLayout(testiBanner, BoxLayout.Y_AXIS));

        JLabel lblTitolo = new JLabel("THE KNIFE");
        lblTitolo.setFont(StileTheKnife.FONT_BRAND_GRANDE);
        lblTitolo.setForeground(StileTheKnife.BIANCO);
        lblTitolo.setAlignmentX(Component.LEFT_ALIGNMENT);

        testiBanner.add(lblTitolo);
        testiBanner.add(Box.createVerticalStrut(4));
        panelBanner.add(testiBanner);

        add(panelBanner, BorderLayout.NORTH);

        // Pannello centrale
        JPanel panelCentrale = new JPanel(new GridLayout(1, 2, 16, 0));
        panelCentrale.setBackground(StileTheKnife.VERDE_THEKNIFE);
        panelCentrale.setBorder(BorderFactory.createEmptyBorder(0, 16, 16, 16));

        panelCentrale.add(creaPannelloLogin());
        panelCentrale.add(creaPannelloGuest());

        add(panelCentrale, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }

    /**
     * Crea e configura il pannello di login per gli utenti registrati (clienti e ristoratori).
     *
     * @return JPanel contenente il form per l'autenticazione.
     */
    private JPanel creaPannelloLogin() {
        JPanel card = StileTheKnife.creaCardBianca();
        card.setPreferredSize(new Dimension(420, 415));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel lblIntestazione = new JLabel("Accedi al tuo account");
        lblIntestazione.setFont(StileTheKnife.FONT_SOTTOTITOLO);
        lblIntestazione.setForeground(StileTheKnife.VERDE_THEKNIFE);
        lblIntestazione.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblDesc = StileTheKnife.creaEtichetta("Per clienti e ristoratori registrati");
        lblDesc.setForeground(StileTheKnife.TESTO_GRIGIO);
        lblDesc.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblUser = StileTheKnife.creaEtichetta("Username / Email:");
        lblUser.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtUsername = StileTheKnife.creaCampoTesto(20);
        txtUsername.setAlignmentX(Component.LEFT_ALIGNMENT);
        StileTheKnife.fissaDimensioneInput(txtUsername, 360);

        JLabel lblPass = StileTheKnife.creaEtichetta("Password:");
        lblPass.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtPassword = StileTheKnife.creaCampoPassword(16);
        JPanel panelPassConOcchio = StileTheKnife.creaPannelloPasswordConOcchio(txtPassword, 360);
        panelPassConOcchio.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnLogin = StileTheKnife.creaBottonePrimario("Accedi alla piattaforma");
        btnLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLogin.setPreferredSize(new Dimension(360, 44));
        btnLogin.setMinimumSize(new Dimension(360, 44));
        btnLogin.setMaximumSize(new Dimension(360, 44));

        JButton btnRegistrati = StileTheKnife.creaBottoneSecondario("Non hai un account? Registrati ora");
        btnRegistrati.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnRegistrati.setPreferredSize(new Dimension(360, 48));
        btnRegistrati.setMinimumSize(new Dimension(360, 48));
        btnRegistrati.setMaximumSize(new Dimension(360, 48));

        btnLogin.addActionListener(e -> gestisciLogin());
        txtPassword.addActionListener(e -> gestisciLogin());
        btnRegistrati.addActionListener(e -> new FinestraRegistrazione(this, controller).setVisible(true));

        card.add(lblIntestazione);
        card.add(Box.createVerticalStrut(4));
        card.add(lblDesc);
        card.add(Box.createVerticalStrut(22));
        card.add(lblUser);
        card.add(Box.createVerticalStrut(6));
        card.add(txtUsername);
        card.add(Box.createVerticalStrut(16));
        card.add(lblPass);
        card.add(Box.createVerticalStrut(6));
        card.add(panelPassConOcchio);
        card.add(Box.createVerticalGlue());
        card.add(btnLogin);
        card.add(Box.createVerticalStrut(12));
        card.add(btnRegistrati);

        return card;
    }

    /**
     * Crea e configura il pannello per l'accesso anonimo in modalità Guest.
     *
     * @return JPanel contenente il form per la ricerca come ospite.
     */
    private JPanel creaPannelloGuest() {
        JPanel card = StileTheKnife.creaCardBianca();
        card.setPreferredSize(new Dimension(420, 415));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel lblIntestazione = new JLabel("Esplora come ospite");
        lblIntestazione.setFont(StileTheKnife.FONT_SOTTOTITOLO);
        lblIntestazione.setForeground(StileTheKnife.VERDE_THEKNIFE);
        lblIntestazione.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea txtInfo = new JTextArea(
                "Non vuoi registrarti subito? Inserisci il nome di una città o nazione " +
                        "per consultare liberamente i ristoranti vicini, usare i filtri di ricerca " +
                        "e leggere le recensioni in forma anonima."
        );
        txtInfo.setFont(StileTheKnife.FONT_NORMALE);
        txtInfo.setForeground(StileTheKnife.TESTO_SCURO);
        txtInfo.setBackground(StileTheKnife.BIANCO);
        txtInfo.setLineWrap(true);
        txtInfo.setWrapStyleWord(true);
        txtInfo.setEditable(false);
        txtInfo.setFocusable(false);
        txtInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtInfo.setMaximumSize(new Dimension(360, 75));

        JLabel lblLuogo = StileTheKnife.creaEtichetta("Inserisci un luogo (es. Vienna, Como, Italy):");
        lblLuogo.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtLuogoGuest = StileTheKnife.creaCampoTesto(20);
        txtLuogoGuest.setAlignmentX(Component.LEFT_ALIGNMENT);
        StileTheKnife.fissaDimensioneInput(txtLuogoGuest, 360);

        JButton btnGuest = StileTheKnife.creaBottonePrimario("Continua come guest");
        btnGuest.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnGuest.setPreferredSize(new Dimension(360, 44));
        btnGuest.setMinimumSize(new Dimension(360, 44));
        btnGuest.setMaximumSize(new Dimension(360, 44));

        JTextArea txtNota = new JTextArea("Nota: Potrai registrarti o fare il login in qualsiasi momento dal menù in alto a destra.");
        txtNota.setFont(new Font("SansSerif", Font.PLAIN, 12));
        txtNota.setForeground(StileTheKnife.VERDE_THEKNIFE);
        txtNota.setBackground(StileTheKnife.VERDE_CHIARO_SFONDO);
        txtNota.setLineWrap(true);
        txtNota.setWrapStyleWord(true);
        txtNota.setEditable(false);
        txtNota.setFocusable(false);
        txtNota.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        txtNota.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtNota.setPreferredSize(new Dimension(360, 48));
        txtNota.setMinimumSize(new Dimension(360, 48));
        txtNota.setMaximumSize(new Dimension(360, 48));

        btnGuest.addActionListener(e -> gestisciAccessoGuest());
        txtLuogoGuest.addActionListener(e -> gestisciAccessoGuest());

        card.add(lblIntestazione);
        card.add(Box.createVerticalStrut(12));
        card.add(txtInfo);
        card.add(Box.createVerticalStrut(18));
        card.add(lblLuogo);
        card.add(Box.createVerticalStrut(6));
        card.add(txtLuogoGuest);
        card.add(Box.createVerticalGlue());
        card.add(btnGuest);
        card.add(Box.createVerticalStrut(12));
        card.add(txtNota);

        return card;
    }

    /**
     * Raccoglie i dati inseriti nei campi di login e richiede l'autenticazione al controller.
     * In caso di successo, reindirizza l'utente alla schermata principale,
     * altrimenti mostra un avviso di errore.
     */
    private void gestisciLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Inserisci sia lo username che la password.", "Dati mancanti", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (controller.login(username, password)) {
            apriSchermataPrincipale(controller.getSessione().getUtenteCorrente());
        } else {
            JOptionPane.showMessageDialog(this, "Username o password non corretti. Riprova.", "Accesso negato", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Prende il luogo inserito nel campo di accesso rapido e avvia la sessione come ospite.
     * Mostra un messaggio di errore se il campo è vuoto.
     */
    private void gestisciAccessoGuest() {
        String luogo = txtLuogoGuest.getText().trim();
        if (luogo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Indica il nome di un luogo (es. Como, Vienna).", "Luogo obbligatorio", JOptionPane.WARNING_MESSAGE);
            return;
        }
        controller.accediComeGuest(luogo);
        apriSchermataPrincipale(controller.getSessione().getUtenteCorrente());
    }

    /**
     * Chiude la finestra iniziale e istanzia la schermata principale corretta
     * a seconda del tipo di utente o della modalità di accesso.
     *
     * @param utenteAttivo L'istanza dell'utente autenticato o dell'ospite.
     */
    private void apriSchermataPrincipale(Utente utenteAttivo) {
        dispose();
        if (utenteAttivo.isRistoratore()) {
            new FinestraRistoratore(controller).setVisible(true);
        } else {
            new FinestraClienteGuest(controller).setVisible(true);
        }
    }
}