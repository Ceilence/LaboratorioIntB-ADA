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

/**
 * Finestra di dialogo per modificare i dati personali e la password dell'utente loggato.
 * @author Alessandro Frigerio
 * @author Antonio Pardo
 * @author Davide Moretti
 */
public class FinestraModificaProfilo extends JDialog {

    private final ControllerTheKnife controller;
    private final Runnable callbackAggiornamento;
    private JTextField txtNome;
    private JTextField txtCognome;
    private JTextField txtUsername;
    private JTextField txtDomicilio;
    private JPasswordField txtNuovaPassword;

    /**
     * Costruttore del JDialog di modifica profilo.
     * @param parent la finestra principale chiamante
     * @param controller il controller attivo del client
     * @param callbackAggiornamento azione per aggiornare l'intestazione della finestra principale
     */
    public FinestraModificaProfilo(JFrame parent, ControllerTheKnife controller, Runnable callbackAggiornamento) {
        super(parent, "Il Mio Account - Modifica Credenziali", true);
        this.controller = controller;
        this.callbackAggiornamento = callbackAggiornamento;
        inizializzaGUI();
    }

    /**
     * Costruisce il form precompilato.
     */
    private void inizializzaGUI() {
        StileTheKnife.applicaIconaFinestra(this);
        setSize(500, 460);
        setLocationRelativeTo(getParent());
        setResizable(false);
        setLayout(new BorderLayout());

        Utente u = controller.getSessione().getUtenteCorrente();

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 12));
        header.setBackground(StileTheKnife.VERDE_THEKNIFE);
        header.add(StileTheKnife.creaLabelLogo(36));
        JLabel lblTitolo = new JLabel("Modifica Profilo e Credenziali (" + u.getRuolo() + ")");
        lblTitolo.setFont(StileTheKnife.FONT_SOTTOTITOLO);
        lblTitolo.setForeground(StileTheKnife.BIANCO);
        header.add(lblTitolo);
        add(header, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(StileTheKnife.BIANCO);
        form.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 6, 7, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtNome = StileTheKnife.creaCampoTesto(18);
        txtNome.setText(u.getNome());
        txtCognome = StileTheKnife.creaCampoTesto(18);
        txtCognome.setText(u.getCognome());
        txtUsername = StileTheKnife.creaCampoTesto(18);
        txtUsername.setText(u.getUsername());
        txtDomicilio = StileTheKnife.creaCampoTesto(18);
        txtDomicilio.setText(u.getLuogoDomicilio());

        txtNuovaPassword = StileTheKnife.creaCampoPassword(14);
        JPanel panelNuovaPasswordConOcchio = StileTheKnife.creaPannelloPasswordConOcchio(txtNuovaPassword, 0);

        aggiungiRiga(form, gbc, 0, "Nome *:", txtNome);
        aggiungiRiga(form, gbc, 1, "Cognome *:", txtCognome);
        aggiungiRiga(form, gbc, 2, "Username / Email *:", txtUsername);
        aggiungiRiga(form, gbc, 3, "Città di domicilio *:", txtDomicilio);
        aggiungiRiga(form, gbc, 4, "Nuova Password (opzionale):", panelNuovaPasswordConOcchio);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        JLabel lblHint = StileTheKnife.creaEtichetta(
                "<html><small>Lascia vuoto il campo password per mantenere quella attuale. Se la cambi, deve avere almeno 8 caratteri, una maiuscola, una minuscola e un numero.</small></html>"
        );
        lblHint.setForeground(StileTheKnife.TESTO_GRIGIO);
        form.add(lblHint, gbc);

        add(form, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        footer.setBackground(StileTheKnife.GRIGIO_SFONDO);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, StileTheKnife.GRIGIO_BORDO));

        JButton btnAnnulla = StileTheKnife.creaBottoneSecondario("Annulla");
        JButton btnSalva = StileTheKnife.creaBottonePrimario("Salva Modifiche");

        btnAnnulla.addActionListener(e -> dispose());
        btnSalva.addActionListener(e -> salvaModifiche());

        footer.add(btnAnnulla);
        footer.add(btnSalva);
        add(footer, BorderLayout.SOUTH);
    }

    /**
     * Aggiunge una riga a un pannello.
     * @param p
     * @param gbc
     * @param riga
     * @param etichetta
     * @param comp
     */
    private void aggiungiRiga(JPanel p, GridBagConstraints gbc, int riga, String etichetta, java.awt.Component comp) {
        gbc.gridx = 0;
        gbc.gridy = riga;
        gbc.weightx = 0.4;
        p.add(StileTheKnife.creaEtichetta(etichetta), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.6;
        p.add(comp, gbc);
    }

    /**
     * Valida i campi e controlla che l'eventuale nuova password rispetti i requisiti di sicurezza.
     */
    private void salvaModifiche() {
        String nome = txtNome.getText().trim();
        String cognome = txtCognome.getText().trim();
        String user = txtUsername.getText().trim();
        String dom = txtDomicilio.getText().trim();
        String pass = new String(txtNuovaPassword.getPassword());

        if (nome.isEmpty() || cognome.isEmpty() || user.isEmpty() || dom.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Compila tutti i campi obbligatori (*).", "Attenzione", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!pass.isEmpty()) {
            boolean errorePassword = StileTheKnife.validaPassword(pass);
            if (!errorePassword) {
                JOptionPane.showMessageDialog(this,
                        "La password non rispetta i criteri:\nminimo 8 caratteri, una lettera maiuscola, una minuscola, un numero",
                        "Password non valida", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        if (controller.aggiornaProfiloUtente(nome, cognome, user, dom, pass)) {
            if (callbackAggiornamento != null) {
                callbackAggiornamento.run();
            }
            JOptionPane.showMessageDialog(this, "Credenziali e profilo aggiornati con successo!");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Errore: lo username scelto potrebbe essere già occupato.", "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }
}