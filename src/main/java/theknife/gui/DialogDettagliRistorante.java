/*
 * Autori del progetto:
 * - Alessandro Frigerio, Matricola: 759926, Sede: CO
 * - Antonio Pardo, Matricola: 760613, Sede: CO
 * - Davide Moretti, Matricola: 762176, Sede: CO
 */
package theknife.gui;

import theknife.essenziali.Recensione;
import theknife.essenziali.Ristorante;
import theknife.essenziali.Utente;
import theknife.gestore.ControllerTheKnife;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;

/**
 * Finestra di dialogo ridimensionabile che mostra i dettagli di un ristorante e le sue recensioni.
 * <p>
 * Implementa a livello grafico <code>visualizzaRistorante()</code>, <code>visualizzaRecensioni()</code>
 * in forma anonima e, per i soli clienti loggati, l'inserimento di recensioni e l'aggiunta ai preferiti.
 * @author Alessandro Frigerio
 * @author Antonio Pardo
 * @author Davide Moretti
 */
public class DialogDettagliRistorante extends JDialog {

    /**
     * Controller del client <code>controller</code>.
     */
    private final ControllerTheKnife controller;

    /**
     * Il ristorante visualizzato <code>ristorante</code>.
     */
    private final Ristorante ristorante;

    /**
     * Etichetta che mostra la media delle stelle e il totale recensioni <code>lblRiepilogo</code>.
     */
    private JLabel lblRiepilogo;

    /**
     * Pannello contenente la lista scorrevole delle recensioni <code>panelListaRecensioni</code>.
     */
    private JPanel panelListaRecensioni;

    /**
     * Costruttore che carica e mostra la scheda del ristorante selezionato.
     * @param parent la finestra chiamante
     * @param controller il controller attivo del client
     * @param ristorante l'oggetto <code>Ristorante</code> da visualizzare
     */
    public DialogDettagliRistorante(JFrame parent, ControllerTheKnife controller, Ristorante ristorante) {
        super(parent, "Dettagli Ristorante - " + ristorante.getNome(), true);
        this.controller = controller;
        this.ristorante = ristorante;
        inizializzaGUI();
        aggiornaRecensioniERiepilogo();
    }

    /**
     * Costruisce l'interfaccia grafica della finestra dettagli.
     */
    private void inizializzaGUI() {
        setSize(800, 650);
        setLocationRelativeTo(getParent());
        setResizable(true);
        getContentPane().setBackground(StileTheKnife.GRIGIO_SFONDO);
        setLayout(new BorderLayout(15, 15));

        // 1. HEADER CON NOME RISTORANTE E VALUTAZIONE MEDIA
        JPanel panelHeader = new JPanel(new BorderLayout(10, 5));
        panelHeader.setBackground(StileTheKnife.VERDE_THEKNIFE);
        panelHeader.setBorder(BorderFactory.createEmptyBorder(18, 25, 18, 25));

        JLabel lblNome = new JLabel(ristorante.getNome());
        lblNome.setFont(StileTheKnife.FONT_TITOLO);
        lblNome.setForeground(StileTheKnife.BIANCO);

        lblRiepilogo = new JLabel("Caricamento valutazioni...");
        lblRiepilogo.setFont(StileTheKnife.FONT_SOTTOTITOLO);
        lblRiepilogo.setForeground(StileTheKnife.VERDE_CHIARO_SFONDO);

        panelHeader.add(lblNome, BorderLayout.WEST);
        panelHeader.add(lblRiepilogo, BorderLayout.EAST);
        add(panelHeader, BorderLayout.NORTH);

        // 2. CORPO CENTRALE: INFO RISTORANTE + LISTA RECENSIONI ANONIME
        JPanel panelCentro = new JPanel(new BorderLayout(15, 15));
        panelCentro.setBackground(StileTheKnife.GRIGIO_SFONDO);
        panelCentro.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Scheda caratteristiche del ristorante
        JPanel cardInfo = StileTheKnife.creaCardBianca();
        cardInfo.setLayout(new GridLayout(3, 2, 15, 10));
        cardInfo.add(StileTheKnife.creaEtichetta("<html><b>Città e Nazione:</b> " + ristorante.getCitta() + " (" + ristorante.getNazione() + ")</html>"));
        cardInfo.add(StileTheKnife.creaEtichetta("<html><b>Tipologia Cucina:</b> " + ristorante.getTipoCucina() + "</html>"));
        cardInfo.add(StileTheKnife.creaEtichetta("<html><b>Indirizzo:</b> " + ristorante.getIndirizzo() + "</html>"));
        cardInfo.add(StileTheKnife.creaEtichetta("<html><b>Fascia di Prezzo:</b> ~" + ristorante.getFasciaPrezzo() + " €</html>"));
        cardInfo.add(StileTheKnife.creaEtichetta("<html><b>Coordinate:</b> Lat " + ristorante.getLatitudine() + ", Lon " + ristorante.getLongitudine() + "</html>"));
        cardInfo.add(StileTheKnife.creaEtichetta("<html><b>Servizi:</b> Delivery: " + (ristorante.isDelivery() ? "Sì" : "No")
                + " | Prenotazione Online: " + (ristorante.isPrenotazioneOnline() ? "Sì" : "No") + "</html>"));

        panelCentro.add(cardInfo, BorderLayout.NORTH);

        // Sezione recensioni scorrevole
        panelListaRecensioni = new JPanel();
        panelListaRecensioni.setLayout(new BoxLayout(panelListaRecensioni, BoxLayout.Y_AXIS));
        panelListaRecensioni.setBackground(StileTheKnife.BIANCO);

        JScrollPane scrollRecensioni = new JScrollPane(panelListaRecensioni);
        scrollRecensioni.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(StileTheKnife.GRIGIO_BORDO),
                "Recensioni dei Clienti (Visualizzazione Anonima)"
        ));
        scrollRecensioni.getVerticalScrollBar().setUnitIncrement(16);
        panelCentro.add(scrollRecensioni, BorderLayout.CENTER);

        add(panelCentro, BorderLayout.CENTER);

        // 3. BARRA INFERIORE (AZIONI CLIENTE O AVVISO GUEST)
        JPanel panelSud = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 12));
        panelSud.setBackground(StileTheKnife.BIANCO);
        panelSud.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, StileTheKnife.GRIGIO_BORDO));

        Utente utente = controller.getSessione().getUtenteCorrente();
        if (utente != null && utente.isCliente()) {
            JButton btnPreferito = StileTheKnife.creaBottoneSecondario("★ Aggiungi ai Preferiti");
            JButton btnScriviRec = StileTheKnife.creaBottonePrimario("✍ Scrivi una Recensione");

            btnPreferito.addActionListener(e -> {
                if (controller.aggiungiPreferito(ristorante.getIdRistorante())) {
                    JOptionPane.showMessageDialog(this, "Ristorante aggiunto alla tua lista dei preferiti!");
                } else {
                    JOptionPane.showMessageDialog(this, "Questo ristorante è già nei tuoi preferiti.", "Info", JOptionPane.INFORMATION_MESSAGE);
                }
            });

            btnScriviRec.addActionListener(e -> apriDialogNuovaRecensione());

            panelSud.add(btnPreferito);
            panelSud.add(btnScriviRec);
        } else {
            JLabel lblGuestInfo = StileTheKnife.creaEtichetta("Accedi come Cliente per salvare nei preferiti o scrivere una recensione.");
            panelSud.add(lblGuestInfo);
        }

        JButton btnChiudi = StileTheKnife.creaBottoneSecondario("Chiudi");
        btnChiudi.addActionListener(e -> dispose());
        panelSud.add(btnChiudi);

        add(panelSud, BorderLayout.SOUTH);
    }

    /**
     * Ricarica dal server il riepilogo delle stelle e la lista delle recensioni anonime.
     */
    private void aggiornaRecensioniERiepilogo() {
        double[] riepilogo = controller.visualizzaRiepilogo(ristorante.getIdRistorante());
        lblRiepilogo.setText(String.format("★ %.1f / 5.0  (%d recensioni)", riepilogo[1], (int) riepilogo[0]));

        panelListaRecensioni.removeAll();
        List<Recensione> recensioni = controller.visualizzaRecensioni(ristorante.getIdRistorante());

        if (recensioni.isEmpty()) {
            JLabel lblVuoto = StileTheKnife.creaEtichetta("Nessuna recensione presente per questo ristorante.");
            lblVuoto.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            panelListaRecensioni.add(lblVuoto);
        } else {
            for (Recensione r : recensioni) {
                JPanel boxRec = new JPanel(new BorderLayout(5, 5));
                boxRec.setBackground(StileTheKnife.BIANCO);
                boxRec.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, StileTheKnife.GRIGIO_BORDO),
                        BorderFactory.createEmptyBorder(12, 15, 12, 15)
                ));

                String stelleStr = "★".repeat(Math.max(0, r.getStelle())) + "☆".repeat(Math.max(0, 5 - r.getStelle()));
                JLabel lblIntestazione = new JLabel(stelleStr + "  (" + r.getStelle() + "/5)  -  Cliente Anonimo  [" + r.getDataRecensione() + "]");
                lblIntestazione.setFont(StileTheKnife.FONT_BOTTONE);
                lblIntestazione.setForeground(StileTheKnife.VERDE_THEKNIFE);

                JLabel lblTesto = new JLabel("<html><p style='width:550px;'>" + r.getTesto() + "</p></html>");
                lblTesto.setFont(StileTheKnife.FONT_NORMALE);

                boxRec.add(lblIntestazione, BorderLayout.NORTH);
                boxRec.add(lblTesto, BorderLayout.CENTER);

                if (r.getRispostaRistoratore() != null && !r.getRispostaRistoratore().isEmpty()) {
                    JLabel lblRisp = new JLabel("<html><b>Risposta del Ristoratore:</b> " + r.getRispostaRistoratore() + "</html>");
                    lblRisp.setFont(StileTheKnife.FONT_NORMALE);
                    lblRisp.setForeground(StileTheKnife.VERDE_THEKNIFE);
                    lblRisp.setBorder(BorderFactory.createEmptyBorder(6, 15, 0, 0));
                    boxRec.add(lblRisp, BorderLayout.SOUTH);
                }

                panelListaRecensioni.add(boxRec);
            }
        }
        panelListaRecensioni.revalidate();
        panelListaRecensioni.repaint();
    }

    /**
     * Mostra un form per permettere al cliente di votare da 1 a 5 stelle e inserire il testo della recensione.
     */
    private void apriDialogNuovaRecensione() {
        JComboBox<Integer> cmbStelle = new JComboBox<>(new Integer[]{5, 4, 3, 2, 1});
        JTextArea txtCommento = new JTextArea(5, 30);
        txtCommento.setLineWrap(true);
        txtCommento.setWrapStyleWord(true);
        txtCommento.setFont(StileTheKnife.FONT_NORMALE);

        JPanel panelInput = new JPanel(new BorderLayout(8, 8));
        JPanel rigaStelle = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rigaStelle.add(new JLabel("Valutazione (Stelle da 1 a 5):"));
        rigaStelle.add(cmbStelle);

        panelInput.add(rigaStelle, BorderLayout.NORTH);
        panelInput.add(new JScrollPane(txtCommento), BorderLayout.CENTER);

        int scelta = JOptionPane.showConfirmDialog(this, panelInput,
                "Nuova Recensione per " + ristorante.getNome(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (scelta == JOptionPane.OK_OPTION) {
            String testo = txtCommento.getText().trim();
            int stelle = (Integer) cmbStelle.getSelectedItem();

            if (testo.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Inserisci un commento testuale.", "Campo Vuoto", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (controller.aggiungiRecensione(ristorante.getIdRistorante(), stelle, testo)) {
                JOptionPane.showMessageDialog(this, "Recensione pubblicata con successo!");
                aggiornaRecensioniERiepilogo();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Impossibile aggiungere la recensione.\nHai già recensito questo ristorante (puoi modificarla dalla sezione 'Le Mie Recensioni').",
                        "Recensione Già Presente", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}