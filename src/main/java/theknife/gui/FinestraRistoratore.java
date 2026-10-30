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
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Finestra principale per il ruolo <code>ristoratore</code> con finestre di dialogo stilizzate in verde
 * e aggiornamento automatico in tempo reale quando i clienti aggiungono o modificano recensioni.
 * @author Alessandro Frigerio
 * @author Antonio Pardo
 * @author Davide Moretti
 */
public class FinestraRistoratore extends JFrame {

    private final ControllerTheKnife controller;
    private List<Ristorante> mieiRistoranti = new ArrayList<>();
    private List<Recensione> recensioniLocaleSelezionato = new ArrayList<>();

    private Timer timerSyncRealTime;
    private long ultimaVersioneNota = -1;

    private JLabel lblTitoloHeader;
    private DefaultTableModel modelloRistoranti;
    private JTable tabellaRistoranti;
    private DefaultTableModel modelloRecensioni;
    private JTable tabellaRecensioni;
    private JLabel lblRiepilogoLocale;

    public FinestraRistoratore(ControllerTheKnife controller) {
        super("TheKnife - Area Riservata Ristoratore");
        this.controller = controller;
        this.ultimaVersioneNota = controller.getVersioneDatiServer();
        inizializzaGUI();
        caricaMieiRistoranti();
        avviaSincronizzazioneRealTime();
    }

    private void inizializzaGUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1220, 740);
        setMinimumSize(new Dimension(820, 520));
        setLocationRelativeTo(null);
        setResizable(true);
        getContentPane().setBackground(StileTheKnife.VERDE_THEKNIFE);
        setLayout(new BorderLayout());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (timerSyncRealTime != null) {
                    timerSyncRealTime.stop();
                }
            }
        });

        // 1. BARRA SUPERIORE CON LOGO, FONT BRAND "THE KNIFE" E SPAZI PULITI
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(StileTheKnife.VERDE_THEKNIFE);
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));

        JPanel boxSinistro = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        boxSinistro.setOpaque(false);
        boxSinistro.add(StileTheKnife.creaLabelLogo(42));

        JLabel lblBrand = new JLabel("THE KNIFE");
        lblBrand.setFont(StileTheKnife.FONT_BRAND);
        lblBrand.setForeground(StileTheKnife.BIANCO);
        boxSinistro.add(lblBrand);

        boxSinistro.add(Box.createHorizontalStrut(18));

        lblTitoloHeader = new JLabel();
        lblTitoloHeader.setFont(StileTheKnife.FONT_SOTTOTITOLO);
        lblTitoloHeader.setForeground(StileTheKnife.VERDE_CHIARO_SFONDO);
        aggiornaTestoHeader();
        boxSinistro.add(lblTitoloHeader);

        JPanel panelAzioniTop = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 4));
        panelAzioniTop.setOpaque(false);

        JButton btnAccount = StileTheKnife.creaBottoneSecondario("👤 Il Mio Account ▾");
        JPopupMenu menu = new JPopupMenu();
        menu.setBackground(StileTheKnife.BIANCO);
        menu.setBorder(BorderFactory.createLineBorder(StileTheKnife.VERDE_THEKNIFE, 2));

        JMenuItem itemModificaProfilo = new JMenuItem("⚙  Modifica Profilo e Credenziali");
        itemModificaProfilo.setFont(StileTheKnife.FONT_NORMALE);
        itemModificaProfilo.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 20));
        itemModificaProfilo.addActionListener(e -> new DialogModificaProfilo(this, controller, this::aggiornaTestoHeader).setVisible(true));

        JMenuItem itemLogout = new JMenuItem("🚪  Logout");
        itemLogout.setFont(StileTheKnife.FONT_NORMALE);
        itemLogout.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 20));
        itemLogout.addActionListener(e -> {
            if (timerSyncRealTime != null) timerSyncRealTime.stop();
            controller.logout();
            dispose();
            new FinestraIniziale(controller).setVisible(true);
        });

        menu.add(itemModificaProfilo);
        menu.addSeparator();
        menu.add(itemLogout);
        btnAccount.addActionListener(e -> menu.show(btnAccount, 0, btnAccount.getHeight()));

        panelAzioniTop.add(btnAccount);

        topBar.add(boxSinistro, BorderLayout.WEST);
        topBar.add(panelAzioniTop, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // 2. CORPO CENTRALE CON DIVISORE VERDE CONTINUO
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, creaPannelloSinistro(), creaPannelloDestro());
        splitPane.setResizeWeight(0.50);
        splitPane.setDividerLocation(590);
        splitPane.setContinuousLayout(true);
        StileTheKnife.applicaSplitVerdeContinuo(splitPane);

        add(splitPane, BorderLayout.CENTER);
    }

    private void aggiornaTestoHeader() {
        Utente u = controller.getSessione().getUtenteCorrente();
        lblTitoloHeader.setText("Pannello Ristoratore: " + u.getNome() + " " + u.getCognome() + " (@" + u.getUsername() + ")");
    }

    /**
     * Aggiorna automaticamente le tabelle del ristoratore non appena un cliente scrive o modifica una recensione.
     */
    private void avviaSincronizzazioneRealTime() {
        timerSyncRealTime = new Timer(3000, e -> {
            long versioneAttuale = controller.getVersioneDatiServer();
            if (versioneAttuale != -1 && versioneAttuale != ultimaVersioneNota) {
                ultimaVersioneNota = versioneAttuale;
                int rigaRistSelezionata = tabellaRistoranti.getSelectedRow();
                int rigaRecSelezionata = tabellaRecensioni.getSelectedRow();

                caricaMieiRistoranti();
                if (rigaRistSelezionata >= 0 && rigaRistSelezionata < mieiRistoranti.size()) {
                    tabellaRistoranti.setRowSelectionInterval(rigaRistSelezionata, rigaRistSelezionata);
                    caricaRecensioniRistoranteSelezionato();
                    if (rigaRecSelezionata >= 0 && rigaRecSelezionata < recensioniLocaleSelezionato.size()) {
                        tabellaRecensioni.setRowSelectionInterval(rigaRecSelezionata, rigaRecSelezionata);
                    }
                }
            }
        });
        timerSyncRealTime.start();
    }

    private JPanel creaPannelloSinistro() {
        JPanel panel = StileTheKnife.creaCardBianca();
        panel.setMinimumSize(new Dimension(340, 250));
        panel.setLayout(new BorderLayout(10, 12));

        JLabel lblIntestazione = new JLabel("I Miei Ristoranti Gestiti");
        lblIntestazione.setFont(StileTheKnife.FONT_SOTTOTITOLO);
        lblIntestazione.setForeground(StileTheKnife.VERDE_THEKNIFE);
        panel.add(lblIntestazione, BorderLayout.NORTH);

        String[] col = {"Nome Locale", "Città", "Cucina", "Prezzo", "Delivery", "Media ★", "Recensioni"};
        modelloRistoranti = new DefaultTableModel(col, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabellaRistoranti = new JTable(modelloRistoranti);
        tabellaRistoranti.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        StileTheKnife.applicaStileTabella(tabellaRistoranti);

        tabellaRistoranti.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                caricaRecensioniRistoranteSelezionato();
            }
        });

        panel.add(new JScrollPane(tabellaRistoranti), BorderLayout.CENTER);

        JPanel barraBottoniRist = new JPanel(new GridLayout(1, 3, 8, 0));
        barraBottoniRist.setOpaque(false);

        JButton btnNuovo = StileTheKnife.creaBottonePrimario("+ Aggiungi");
        JButton btnModifica = StileTheKnife.creaBottoneSecondario("✎ Modifica");
        JButton btnElimina = StileTheKnife.creaBottoneSecondario("🗑 Elimina");

        btnNuovo.addActionListener(e -> apriDialogRistorante(null));
        btnModifica.addActionListener(e -> {
            int riga = tabellaRistoranti.getSelectedRow();
            if (riga < 0) {
                JOptionPane.showMessageDialog(this, "Seleziona prima un ristorante da modificare.");
                return;
            }
            apriDialogRistorante(mieiRistoranti.get(riga));
        });
        btnElimina.addActionListener(e -> eliminaRistoranteSelezionato());

        barraBottoniRist.add(btnNuovo);
        barraBottoniRist.add(btnModifica);
        barraBottoniRist.add(btnElimina);
        panel.add(barraBottoniRist, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel creaPannelloDestro() {
        JPanel panel = StileTheKnife.creaCardBianca();
        panel.setMinimumSize(new Dimension(340, 250));
        panel.setLayout(new BorderLayout(10, 12));

        lblRiepilogoLocale = new JLabel("Seleziona un ristorante a sinistra per vedere le recensioni");
        lblRiepilogoLocale.setFont(StileTheKnife.FONT_SOTTOTITOLO);
        lblRiepilogoLocale.setForeground(StileTheKnife.VERDE_THEKNIFE);
        panel.add(lblRiepilogoLocale, BorderLayout.NORTH);

        String[] col = {"Stelle", "Commento del Cliente (e avvisi di modifica)", "Data", "La tua Risposta"};
        modelloRecensioni = new DefaultTableModel(col, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabellaRecensioni = new JTable(modelloRecensioni);
        tabellaRecensioni.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        StileTheKnife.applicaStileTabella(tabellaRecensioni);

        panel.add(new JScrollPane(tabellaRecensioni), BorderLayout.CENTER);

        JButton btnRispondi = StileTheKnife.creaBottonePrimario("💬 Rispondi / Modifica Risposta");
        btnRispondi.addActionListener(e -> rispondiOModificaRisposta());
        panel.add(btnRispondi, BorderLayout.SOUTH);

        return panel;
    }

    private void caricaMieiRistoranti() {
        mieiRistoranti = controller.visualizzaMieiRistoranti();
        modelloRistoranti.setRowCount(0);

        for (Ristorante r : mieiRistoranti) {
            double[] riepilogo = controller.visualizzaRiepilogo(r.getIdRistorante());
            modelloRistoranti.addRow(new Object[]{
                    r.getNome(),
                    r.getCitta(),
                    r.getTipoCucina(),
                    r.getFasciaPrezzo() + " €",
                    r.isDelivery() ? "Sì" : "No",
                    riepilogo[1] + " / 5.0",
                    (int) riepilogo[0]
            });
        }
        if (!mieiRistoranti.isEmpty() && tabellaRistoranti.getSelectedRow() == -1) {
            tabellaRistoranti.setRowSelectionInterval(0, 0);
        }
    }

    private void caricaRecensioniRistoranteSelezionato() {
        int riga = tabellaRistoranti.getSelectedRow();
        modelloRecensioni.setRowCount(0);
        if (riga < 0 || riga >= mieiRistoranti.size()) {
            lblRiepilogoLocale.setText("Seleziona un ristorante a sinistra per vedere le recensioni");
            return;
        }

        Ristorante r = mieiRistoranti.get(riga);
        double[] riepilogo = controller.visualizzaRiepilogo(r.getIdRistorante());
        lblRiepilogoLocale.setText(r.getNome() + "   |   Media: " + riepilogo[1] + " ★  (" + (int) riepilogo[0] + " recensioni)");

        recensioniLocaleSelezionato = controller.visualizzaRecensioni(r.getIdRistorante());
        for (Recensione rec : recensioniLocaleSelezionato) {
            String risp = (rec.getRispostaRistoratore() != null && !rec.getRispostaRistoratore().isEmpty())
                    ? rec.getRispostaRistoratore()
                    : "[Ancora nessuna risposta]";
            modelloRecensioni.addRow(new Object[]{
                    rec.getStelle() + " ★",
                    rec.getTesto(),
                    rec.getDataRecensione(),
                    risp
            });
        }
    }

    /**
     * Apre la finestra di dialogo stilizzata con dettagli in verde per rispondere o modificare la risposta.
     */
    private void rispondiOModificaRisposta() {
        int riga = tabellaRecensioni.getSelectedRow();
        if (riga < 0 || riga >= recensioniLocaleSelezionato.size()) {
            JOptionPane.showMessageDialog(this, "Seleziona prima una recensione dalla tabella di destra.");
            return;
        }

        Recensione rec = recensioniLocaleSelezionato.get(riga);
        String rispostaAttuale = rec.getRispostaRistoratore() != null ? rec.getRispostaRistoratore() : "";

        JPanel panel = new JPanel(new BorderLayout(10, 12));

        JPanel boxRecCliente = new JPanel(new BorderLayout(6, 4));
        boxRecCliente.setBackground(StileTheKnife.VERDE_CHIARO_SFONDO);
        boxRecCliente.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, StileTheKnife.VERDE_THEKNIFE),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        JLabel lblStelle = new JLabel("Recensione del Cliente (" + rec.getStelle() + "/5 ★):");
        lblStelle.setFont(StileTheKnife.FONT_BOTTONE);
        lblStelle.setForeground(StileTheKnife.VERDE_THEKNIFE);
        JLabel lblCommento = StileTheKnife.creaEtichetta("<html><div style='width:380px;'>\"" + rec.getTesto() + "\"</div></html>");
        boxRecCliente.add(lblStelle, BorderLayout.NORTH);
        boxRecCliente.add(lblCommento, BorderLayout.CENTER);

        JPanel boxInputRisposta = new JPanel(new BorderLayout(6, 6));
        boxInputRisposta.setOpaque(false);
        JLabel lblPrompt = new JLabel("Scrivi o modifica la tua risposta ufficiale:");
        lblPrompt.setFont(StileTheKnife.FONT_BOTTONE);
        lblPrompt.setForeground(StileTheKnife.VERDE_THEKNIFE);

        JTextArea txtRisposta = new JTextArea(rispostaAttuale, 4, 30);
        txtRisposta.setFont(StileTheKnife.FONT_NORMALE);
        txtRisposta.setLineWrap(true);
        txtRisposta.setWrapStyleWord(true);
        txtRisposta.setBorder(StileTheKnife.creaBordoInput());

        boxInputRisposta.add(lblPrompt, BorderLayout.NORTH);
        boxInputRisposta.add(new JScrollPane(txtRisposta), BorderLayout.CENTER);

        panel.add(boxRecCliente, BorderLayout.NORTH);
        panel.add(boxInputRisposta, BorderLayout.CENTER);

        if (StileTheKnife.mostraDialogoStilizzato(this, "Risposta del Ristoratore", panel, "Pubblica Risposta", 520, 380)) {
            String nuovaRisposta = txtRisposta.getText().trim();
            if (!nuovaRisposta.isEmpty() && controller.rispostaRecensioni(rec.getIdRecensione(), nuovaRisposta)) {
                ultimaVersioneNota = controller.getVersioneDatiServer();
                caricaRecensioniRistoranteSelezionato();
            }
        }
    }

    /**
     * Apre la finestra di dialogo stilizzata con barra verde per aggiungere o modificare un ristorante.
     * @param esistente il ristorante da modificare, oppure <code>null</code> per crearne uno nuovo
     */
    private void apriDialogRistorante(Ristorante esistente) {
        boolean modalitaModifica = (esistente != null);

        JTextField txtNome = StileTheKnife.creaCampoTesto(15);
        JTextField txtNazione = StileTheKnife.creaCampoTesto(15);
        JTextField txtCitta = StileTheKnife.creaCampoTesto(15);
        JTextField txtIndirizzo = StileTheKnife.creaCampoTesto(15);
        JTextField txtLat = StileTheKnife.creaCampoTesto(10);
        JTextField txtLon = StileTheKnife.creaCampoTesto(10);
        JTextField txtPrezzo = StileTheKnife.creaCampoTesto(10);
        JTextField txtCucina = StileTheKnife.creaCampoTesto(15);

        JCheckBox chkDel = new JCheckBox("Servizio Attivo");
        chkDel.setFont(StileTheKnife.FONT_NORMALE);
        chkDel.setBackground(StileTheKnife.BIANCO);
        chkDel.setForeground(StileTheKnife.VERDE_THEKNIFE);

        JCheckBox chkPren = new JCheckBox("Servizio Attivo");
        chkPren.setFont(StileTheKnife.FONT_NORMALE);
        chkPren.setBackground(StileTheKnife.BIANCO);
        chkPren.setForeground(StileTheKnife.VERDE_THEKNIFE);

        if (modalitaModifica) {
            txtNome.setText(esistente.getNome());
            txtNazione.setText(esistente.getNazione());
            txtCitta.setText(esistente.getCitta());
            txtIndirizzo.setText(esistente.getIndirizzo());
            txtLat.setText(String.valueOf(esistente.getLatitudine()));
            txtLon.setText(String.valueOf(esistente.getLongitudine()));
            txtPrezzo.setText(String.valueOf(esistente.getFasciaPrezzo()));
            txtCucina.setText(esistente.getTipoCucina());
            chkDel.setSelected(esistente.isDelivery());
            chkPren.setSelected(esistente.isPrenotazioneOnline());
        } else {
            txtLat.setText("45.808");
            txtLon.setText("9.085");
            txtPrezzo.setText("50");
        }

        JPanel form = new JPanel(new GridLayout(10, 2, 10, 8));
        form.add(StileTheKnife.creaEtichetta("Nome Ristorante *:")); form.add(txtNome);
        form.add(StileTheKnife.creaEtichetta("Nazione (es. Italy, USA) *:")); form.add(txtNazione);
        form.add(StileTheKnife.creaEtichetta("Città *:")); form.add(txtCitta);
        form.add(StileTheKnife.creaEtichetta("Indirizzo *:")); form.add(txtIndirizzo);
        form.add(StileTheKnife.creaEtichetta("Latitudine *:")); form.add(txtLat);
        form.add(StileTheKnife.creaEtichetta("Longitudine *:")); form.add(txtLon);
        form.add(StileTheKnife.creaEtichetta("Prezzo Medio (€) *:")); form.add(txtPrezzo);
        form.add(StileTheKnife.creaEtichetta("Tipo di Cucina *:")); form.add(txtCucina);
        form.add(StileTheKnife.creaEtichetta("Consegna Delivery:")); form.add(chkDel);
        form.add(StileTheKnife.creaEtichetta("Prenotazione Online:")); form.add(chkPren);

        String titolo = modalitaModifica ? "Modifica Ristorante: " + esistente.getNome() : "Aggiungi Nuovo Ristorante";
        String testoBottone = modalitaModifica ? "Salva Modifiche" : "Crea Ristorante";

        if (StileTheKnife.mostraDialogoStilizzato(this, titolo, form, testoBottone, 520, 560)) {
            try {
                String nome = txtNome.getText().trim();
                String nazione = txtNazione.getText().trim();
                String citta = txtCitta.getText().trim();
                String indirizzo = txtIndirizzo.getText().trim();
                String cucina = txtCucina.getText().trim();
                double lat = Double.parseDouble(txtLat.getText().trim().replace(",", "."));
                double lon = Double.parseDouble(txtLon.getText().trim().replace(",", "."));
                int prezzo = Integer.parseInt(txtPrezzo.getText().trim());

                if (nome.isEmpty() || nazione.isEmpty() || citta.isEmpty() || indirizzo.isEmpty() || cucina.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Compila tutti i campi obbligatori (*).", "Attenzione", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (modalitaModifica) {
                    Ristorante aggiornato = new Ristorante(
                            esistente.getIdRistorante(), nome, nazione, citta, indirizzo,
                            lat, lon, prezzo, chkDel.isSelected(), chkPren.isSelected(), cucina, esistente.getIdRistoratore()
                    );
                    if (controller.modificaRistorante(aggiornato)) {
                        ultimaVersioneNota = controller.getVersioneDatiServer();
                        caricaMieiRistoranti();
                    }
                } else {
                    Ristorante nuovo = new Ristorante(
                            nome, nazione, citta, indirizzo, lat, lon, prezzo,
                            chkDel.isSelected(), chkPren.isSelected(), cucina, null
                    );
                    if (controller.aggiungiRistorante(nuovo)) {
                        ultimaVersioneNota = controller.getVersioneDatiServer();
                        caricaMieiRistoranti();
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Verifica che Latitudine, Longitudine e Prezzo siano numeri validi.", "Errore Formato", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void eliminaRistoranteSelezionato() {
        int riga = tabellaRistoranti.getSelectedRow();
        if (riga < 0) {
            JOptionPane.showMessageDialog(this, "Seleziona prima un ristorante da eliminare.");
            return;
        }
        Ristorante r = mieiRistoranti.get(riga);
        int conferma = JOptionPane.showConfirmDialog(
                this,
                "Sei sicuro di voler eliminare definitivamente il ristorante '" + r.getNome() + "'?",
                "Conferma Eliminazione",
                JOptionPane.YES_NO_OPTION
        );
        if (conferma == JOptionPane.YES_OPTION && controller.eliminaRistorante(r.getIdRistorante())) {
            ultimaVersioneNota = controller.getVersioneDatiServer();
            caricaMieiRistoranti();
            modelloRecensioni.setRowCount(0);
        }
    }
}