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
 * Finestra di dialogo per la registrazione di un nuovo utente.
 * Consente l'inserimento dei dati anagrafici, delle credenziali e
 * la scelta del ruolo (cliente o ristoratore) nel sistema.
 *
 * @author Alessandro Frigerio
 * @author Antonio Pardo
 * @author Davide Moretti
 */
public class FinestraRegistrazione extends JDialog {

    private final ControllerTheKnife controller;

    private JTextField txtNome;
    private JTextField txtCognome;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JTextField txtDataNascita;
    private JTextField txtDomicilio;
    private JComboBox<String> cmbRuolo;

    /**
     * Costruttore della finestra di registrazione.
     * Inizializza il componente modale bloccando l'interazione con la finestra chiamante.
     *
     * @param parent     la finestra <code>JFrame</code> genitore
     * @param controller il controller principale per inoltrare la richiesta al server
     */
    public FinestraRegistrazione(JFrame parent, ControllerTheKnife controller) {
        super(parent, "Registrazione Nuovo Utente - TheKnife", true);
        this.controller = controller;
        inizializzaGUI();
    }

    /**
     * Inizializza i layout e assembla i componenti grafici del modulo
     * di registrazione.
     */
    private void inizializzaGUI() {
        StileTheKnife.applicaIconaFinestra(this);
        setSize(500, 540);
        setLocationRelativeTo(getParent());
        setResizable(false);
        setLayout(new BorderLayout());

        JPanel panelHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 12));
        panelHeader.setBackground(StileTheKnife.VERDE_THEKNIFE);
        panelHeader.add(StileTheKnife.creaLabelLogo(36));

        JLabel lblTitolo = new JLabel("Crea il tuo account TheKnife");
        lblTitolo.setFont(StileTheKnife.FONT_SOTTOTITOLO);
        lblTitolo.setForeground(StileTheKnife.BIANCO);
        panelHeader.add(lblTitolo);
        add(panelHeader, BorderLayout.NORTH);

        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBackground(StileTheKnife.BIANCO);
        panelForm.setBorder(BorderFactory.createEmptyBorder(18, 25, 18, 25));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        txtNome = StileTheKnife.creaCampoTesto(18);
        txtCognome = StileTheKnife.creaCampoTesto(18);
        txtUsername = StileTheKnife.creaCampoTesto(18);
        txtPassword = StileTheKnife.creaCampoPassword(14);
        JPanel panelPasswordConOcchio = StileTheKnife.creaPannelloPasswordConOcchio(txtPassword, 0);

        txtDataNascita = StileTheKnife.creaCampoTesto(18);
        txtDataNascita.setToolTipText("Formato AAAA-MM-GG (es. 2002-05-15) oppure lascia vuoto");
        txtDomicilio = StileTheKnife.creaCampoTesto(18);

        cmbRuolo = new JComboBox<>(new String[]{"cliente", "ristoratore"});
        cmbRuolo.setFont(StileTheKnife.FONT_NORMALE);
        cmbRuolo.setBackground(StileTheKnife.BIANCO);

        aggiungiRigaForm(panelForm, gbc, 0, "Nome *:", txtNome);
        aggiungiRigaForm(panelForm, gbc, 1, "Cognome *:", txtCognome);
        aggiungiRigaForm(panelForm, gbc, 2, "Username / Email *:", txtUsername);
        aggiungiRigaForm(panelForm, gbc, 3, "Password *:", panelPasswordConOcchio);
        aggiungiRigaForm(panelForm, gbc, 4, "Data nascita (AAAA-MM-GG):", txtDataNascita);
        aggiungiRigaForm(panelForm, gbc, 5, "Città di domicilio *:", txtDomicilio);
        aggiungiRigaForm(panelForm, gbc, 6, "Tipo di profilo *:", cmbRuolo);

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        JLabel lblRequisitiPwd = StileTheKnife.creaEtichetta(
                "<html><small>La password deve avere almeno 8 caratteri, una maiuscola, una minuscola e un numero.</small></html>"
        );
        lblRequisitiPwd.setForeground(StileTheKnife.TESTO_GRIGIO);
        panelForm.add(lblRequisitiPwd, gbc);

        add(panelForm, BorderLayout.CENTER);

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
     * Metodo di supporto per inserire in modo ordinato un'etichetta
     * e il rispettivo componente di input all'interno del layout a griglia.
     *
     * @param panel          il pannello contenitore
     * @param gbc            le costanti del GridBagLayout per il posizionamento
     * @param riga           l'indice della riga in cui inserire i componenti
     * @param testoEtichetta il testo descrittivo del campo
     * @param campo          il componente grafico destinato all'input dell'utente
     */
    private void aggiungiRigaForm(JPanel panel, GridBagConstraints gbc, int riga, String testoEtichetta, java.awt.Component campo) {
        gbc.gridx = 0;
        gbc.gridy = riga;
        gbc.weightx = 0.38;
        panel.add(StileTheKnife.creaEtichetta(testoEtichetta), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.62;
        panel.add(campo, gbc);
    }

    /**
     * Recupera i dati dal modulo, ne convalida i vincoli di obbligatorietà e di sicurezza,
     * gestisce la conversione delle date e trasmette la richiesta al controller.
     * Se la registrazione ha esito positivo chiude la finestra, altrimenti mostra l'errore.
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
                    "Campi mancanti", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean errorePassword = StileTheKnife.validaPassword(password);
        if (!errorePassword) {
            JOptionPane.showMessageDialog(this,
                    "La password non rispetta i criteri:\nminimo 8 caratteri, una lettera maiuscola, una minuscola, un numero"
                    , "Password non valida", JOptionPane.WARNING_MESSAGE);
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
        if (controller.registrazione(nuovoUtente)) {
            JOptionPane.showMessageDialog(this,
                    "Registrazione completata con successo!",
                    "Benvenuto in TheKnife", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Impossibile completare la registrazione.\nLo username '" + username + "' è già utilizzato da un altro utente.",
                    "Errore di registrazione", JOptionPane.ERROR_MESSAGE);
        }
    }
}