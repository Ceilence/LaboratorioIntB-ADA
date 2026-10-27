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
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.Date;

/**
 * Finestra di dialogo modale per la registrazione di un nuovo utente (Cliente o Ristoratore).
 * <p>
 * Implementa a livello grafico la funzionalità <code>registrazione()</code> richiesta dalle specifiche.
 * @author Alessandro Frigerio
 * @author Antonio Pardo
 * @author Davide Moretti
 */
public class DialogRegistrazione extends JDialog {

    /**
     * Riferimento al controller del client <code>controller</code>.
     */
    private final ControllerTheKnife controller;

    /**
     * Campo per il nome dell'utente <code>txtNome</code>.
     */
    private JTextField txtNome;

    /**
     * Campo per il cognome dell'utente <code>txtCognome</code>.
     */
    private JTextField txtCognome;

    /**
     * Campo per lo username o email <code>txtUsername</code>.
     */
    private JTextField txtUsername;

    /**
     * Campo per la password <code>txtPassword</code>.
     */
    private JPasswordField txtPassword;

    /**
     * Campo per la data di nascita facoltativa <code>txtDataNascita</code>.
     */
    private JTextField txtDataNascita;

    /**
     * Campo per la città di domicilio <code>txtDomicilio</code>.
     */
    private JTextField txtDomicilio;

    /**
     * Menu a tendina per selezionare il ruolo (cliente o ristoratore) <code>cmbRuolo</code>.
     */
    private JComboBox<String> cmbRuolo;

    /**
     * Costruttore che crea e mostra il form di registrazione.
     * @param parent la finestra principale da cui viene aperta la finestra di dialogo
     * @param controller il controller collegato al server
     */
    public DialogRegistrazione(JFrame parent, ControllerTheKnife controller) {
        super(parent, "Registrazione Nuovo Utente - TheKnife", true);
        this.controller = controller;
        inizializzaGUI();
    }

    /**
     * Costruisce il layout e posiziona tutti i campi di input con il tema bianco/verde.
     */
    private void inizializzaGUI() {
        setSize(460, 520);
        setLocationRelativeTo(getParent());
        setResizable(false);
        setLayout(new BorderLayout());

        // Intestazione verde in alto
        JPanel panelHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        panelHeader.setBackground(StileTheKnife.VERDE_THEKNIFE);
        JLabel lblTitolo = new JLabel("Crea il tuo account TheKnife");
        lblTitolo.setFont(StileTheKnife.FONT_SOTTOTITOLO);
        lblTitolo.setForeground(StileTheKnife.BIANCO);
        panelHeader.add(lblTitolo);
        add(panelHeader, BorderLayout.NORTH);

        // Pannello centrale bianco con i campi del form
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBackground(StileTheKnife.BIANCO);
        panelForm.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        txtNome = StileTheKnife.creaCampoTesto(18);
        txtCognome = StileTheKnife.creaCampoTesto(18);
        txtUsername = StileTheKnife.creaCampoTesto(18);
        txtPassword = StileTheKnife.creaCampoPassword(18);
        txtDataNascita = StileTheKnife.creaCampoTesto(18);
        txtDataNascita.setToolTipText("Formato AAAA-MM-GG (es. 2002-05-15) oppure lascia vuoto");
        txtDomicilio = StileTheKnife.creaCampoTesto(18);

        cmbRuolo = new JComboBox<>(new String[]{"cliente", "ristoratore"});
        cmbRuolo.setFont(StileTheKnife.FONT_NORMALE);
        cmbRuolo.setBackground(StileTheKnife.BIANCO);

        aggiungiRigaForm(panelForm, gbc, 0, "Nome *:", txtNome);
        aggiungiRigaForm(panelForm, gbc, 1, "Cognome *:", txtCognome);
        aggiungiRigaForm(panelForm, gbc, 2, "Username / Email *:", txtUsername);
        aggiungiRigaForm(panelForm, gbc, 3, "Password *:", txtPassword);
        aggiungiRigaForm(panelForm, gbc, 4, "Data nascita (AAAA-MM-GG):", txtDataNascita);
        aggiungiRigaForm(panelForm, gbc, 5, "Città di domicilio *:", txtDomicilio);
        aggiungiRigaForm(panelForm, gbc, 6, "Tipo di profilo *:", cmbRuolo);

        add(panelForm, BorderLayout.CENTER);

        // Barra pulsanti in basso
        JPanel panelBottoni = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 12));
        panelBottoni.setBackground(StileTheKnife.GRIGIO_SFONDO);
        panelBottoni.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, StileTheKnife.GRIGIO_BORDO));

        JButton btnAnnulla = StileTheKnife.creaBottoneSecondario("Annulla");
        JButton btnConferma = StileTheKnife.creaBottonePrimario("Completa Registrazione");

        btnAnnulla.addActionListener(e -> dispose());
        btnConferma.addActionListener(e -> eseguiRegistrazione());

        panelBottoni.add(btnAnnulla);
        panelBottoni.add(btnConferma);
        add(panelBottoni, BorderLayout.SOUTH);
    }

    /**
     * Metodo di supporto per aggiungere una riga (etichetta + campo) nel <code>GridBagLayout</code>.
     * @param panel il pannello contenitore
     * @param gbc i vincoli del layout
     * @param riga l'indice della riga
     * @param testoEtichetta il testo della label
     * @param campo il componente di input da affiancare
     */
    private void aggiungiRigaForm(JPanel panel, GridBagConstraints gbc, int riga, String testoEtichetta, java.awt.Component campo) {
        gbc.gridx = 0;
        gbc.gridy = riga;
        gbc.weightx = 0.35;
        panel.add(StileTheKnife.creaEtichetta(testoEtichetta), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;
        panel.add(campo, gbc);
    }

    /**
     * Valida i dati inseriti dall'utente e invia la richiesta di registrazione al server.
     */
    private void eseguiRegistrazione() {
        String nome = txtNome.getText().trim();
        String cognome = txtCognome.getText().trim();
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        String dataStr = txtDataNascita.getText().trim();
        String domicilio = txtDomicilio.getText().trim();
        String ruolo = (String) cmbRuolo.getSelectedItem();

        if (nome.isEmpty() || cognome.isEmpty() || username.isEmpty() || password.isEmpty() || domicilio.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Compila tutti i campi obbligatori contrassegnati con l'asterisco (*).",
                    "Campi Mancanti", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Date dataNascita = null;
        if (!dataStr.isEmpty()) {
            try {
                dataNascita = Date.valueOf(dataStr);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this,
                        "Il formato della data di nascita non è valido.\nUsa il formato AAAA-MM-GG (es. 2002-05-15) oppure lascia il campo vuoto.",
                        "Data non valida", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        Utente nuovoUtente = new Utente(nome, cognome, username, password, dataNascita, domicilio, ruolo);
        boolean registrato = controller.registrazione(nuovoUtente);

        if (registrato) {
            JOptionPane.showMessageDialog(this,
                    "Registrazione completata con successo!\nOra puoi effettuare il login con il tuo username '" + username + "'.",
                    "Benvenuto in TheKnife", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Impossibile completare la registrazione.\nLo username '" + username + "' potrebbe essere già in uso.",
                    "Errore di Registrazione", JOptionPane.ERROR_MESSAGE);
        }
    }
}