/*
 * Autori del progetto:
 *
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
 * Finestra di dialogo per la modifica dei dati personali e delle credenziali dell'utente autenticato.
 * Permette all'utente di aggiornare nome, cognome, username, luogo di domicilio e password.
 *
 * @author Alessandro Frigerio
 * @author Antonio Pardo
 * @author Davide Moretti
 */
public class FinestraModificaProfilo extends JDialog {

    /** Riferimento al controller principale. */
    private final ControllerTheKnife controller;

    /** Funzione invocata al termine dell'aggiornamento per aggiornare l'interfaccia. */
    private final Runnable callbackAggiornamento;

    /** Campo di testo per la modifica del nome dell'utente. */
    private JTextField txtNome;

    /** Campo di testo per la modifica del cognome dell'utente. */
    private JTextField txtCognome;

    /** Campo di testo per la modifica dell'username o email dell'utente. */
    private JTextField txtUsername;

    /** Campo di testo per la modifica della città di domicilio dell'utente. */
    private JTextField txtDomicilio;

    /** Campo di testo per l'inserimento dell'eventuale nuova password. */
    private JPasswordField txtNuovaPassword;

    /**
     * Costruttore della finestra per la modifica del profilo.
     *
     * @param parent La finestra principale chiamante, rispetto alla quale centrare il JDialog.
     * @param controller Il controller principale.
     * @param callbackAggiornamento Azione da eseguire a salvataggio completato.
     */
    public FinestraModificaProfilo(JFrame parent, ControllerTheKnife controller, Runnable callbackAggiornamento) {
        super(parent, "Il mio account - modifica credenziali", true);
        this.controller = controller;
        this.callbackAggiornamento = callbackAggiornamento;
        inizializzaGUI();
    }

    /**
     * Inizializza e posiziona le componenti grafiche della finestra.
     * Costruisce il form precompilandolo con i dati attuali dell'utente.
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
        JLabel lblTitolo = new JLabel("Modifica profilo e credenziali (" + u.getRuolo() + ")");
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
        aggiungiRiga(form, gbc, 4, "Nuova password (opzionale):", panelNuovaPasswordConOcchio);

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
        JButton btnSalva = StileTheKnife.creaBottonePrimario("Salva modifiche");

        btnAnnulla.addActionListener(e -> dispose());
        btnSalva.addActionListener(e -> salvaModifiche());

        footer.add(btnAnnulla);
        footer.add(btnSalva);
        add(footer, BorderLayout.SOUTH);
    }

    /**
     * Metodo per aggiungere una riga (etichetta e componente di input)
     * all'interno del pannello.
     *
     * @param p Il pannello contenitore nel quale aggiungere gli elementi.
     * @param gbc I vincoli di posizionamento del layout.
     * @param riga L'indice della riga in cui inserire l'etichetta e il componente.
     * @param etichetta Il testo da visualizzare a sinistra.
     * @param comp Il componente grafico di input da visualizzare a destra.
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
     * Valida i dati inseriti nel form, assicurandosi che i campi obbligatori siano compilati.
     * Se è stata inserita una nuova password, ne verifica i requisiti di sicurezza.
     * In caso di successo, comunica al controller l'aggiornamento, esegue la callback
     * e chiude la finestra di dialogo.
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