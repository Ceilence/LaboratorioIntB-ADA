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
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Finestra principale per il ruolo <code>ristoratore</code> con tabella recensioni divisa in 6 colonne
 * (Stelle, Nome Utente, Commento, Data, La Tua Risposta, Modificata), pallino rosso lampeggiante
 * per le recensioni modificate e finestra di risposta ampia e scorrevole.
 * @author Alessandro Frigerio
 * @author Antonio Pardo
 * @author Davide Moretti
 */
public class FinestraRistoratore extends JFrame {

    private final ControllerTheKnife controller;
    private List<Ristorante> mieiRistoranti = new ArrayList<>();
    private List<Recensione> recensioniLocaleSelezionato = new ArrayList<>();

    private Timer timerSyncRealTime;
    private Timer timerPallinoLampeggiante;
    private boolean pallinoRossoVisibile = true;
    private String ultimaImprontaRistoratore = "";

    private JLabel lblTitoloHeader;
    private DefaultTableModel modelloRistoranti;
    private JTable tabellaRistoranti;
    private DefaultTableModel modelloRecensioni;
    private JTable tabellaRecensioni;
    private JLabel lblRiepilogoLocale;

    public FinestraRistoratore(ControllerTheKnife controller) {
        super("TheKnife - Area Riservata Ristoratore");
        this.controller = controller;
        inizializzaGUI();
        caricaMieiRistoranti();
        avviaSincronizzazioneRealTime();
        avviaAnimazionePallinoLampeggiante();
    }

    private void inizializzaGUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1260, 740);
        setMinimumSize(new Dimension(880, 520));
        setLocationRelativeTo(null);
        setResizable(true);
        getContentPane().setBackground(StileTheKnife.VERDE_THEKNIFE);
        setLayout(new BorderLayout());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (timerSyncRealTime != null) timerSyncRealTime.stop();
                if (timerPallinoLampeggiante != null) timerPallinoLampeggiante.stop();
            }
        });

        // 1. BARRA SUPERIORE
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
            if (timerPallinoLampeggiante != null) timerPallinoLampeggiante.stop();
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
        splitPane.setResizeWeight(0.42);
        splitPane.setDividerLocation(510);
        splitPane.setContinuousLayout(true);
        StileTheKnife.applicaSplitVerdeContinuo(splitPane);

        add(splitPane, BorderLayout.CENTER);
    }

    private void aggiornaTestoHeader() {
        Utente u = controller.getSessione().getUtenteCorrente();
        lblTitoloHeader.setText("Pannello Ristoratore: " + u.getNome() + " " + u.getCognome() + " (@" + u.getUsername() + ")");
    }

    /**
     * Fa lampeggiare il pallino rosso nella colonna "Modificata" ogni 500 millisecondi.
     */
    private void avviaAnimazionePallinoLampeggiante() {
        timerPallinoLampeggiante = new Timer(500, e -> {
            pallinoRossoVisibile = !pallinoRossoVisibile;
            if (tabellaRecensioni != null && tabellaRecensioni.getRowCount() > 0) {
                tabellaRecensioni.repaint();
            }
        });
        timerPallinoLampeggiante.start();
    }

    private void avviaSincronizzazioneRealTime() {
        timerSyncRealTime = new Timer(2500, e -> {
            int rigaRist = tabellaRistoranti.getSelectedRow();
            if (rigaRist >= 0 && rigaRist < mieiRistoranti.size()) {
                Ristorante r = mieiRistoranti.get(rigaRist);
                List<Recensione> recAttuali = controller.visualizzaRecensioni(r.getIdRistorante());
                StringBuilder sb = new StringBuilder();
                for (Recensione rec : recAttuali) {
                    sb.append(rec.getIdRecensione()).append(":").append(rec.getStelle()).append(":")
                            .append(rec.getTesto()).append(":").append(rec.getRispostaRistoratore()).append("|");
                }
                String nuovaImpronta = sb.toString();
                if (!nuovaImpronta.equals(ultimaImprontaRistoratore)) {
                    ultimaImprontaRistoratore = nuovaImpronta;
                    int rigaRecSel = tabellaRecensioni.getSelectedRow();
                    caricaMieiRistoranti();
                    if (rigaRist < tabellaRistoranti.getRowCount()) {
                        tabellaRistoranti.setRowSelectionInterval(rigaRist, rigaRist);
                    }
                    caricaRecensioniRistoranteSelezionato();
                    if (rigaRecSel >= 0 && rigaRecSel < tabellaRecensioni.getRowCount()) {
                        tabellaRecensioni.setRowSelectionInterval(rigaRecSel, rigaRecSel);
                    }
                }
            }
        });
        timerSyncRealTime.start();
    }

    private JPanel creaPannelloSinistro() {
        JPanel panel = StileTheKnife.creaCardBianca();
        panel.setMinimumSize(new Dimension(320, 250));
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

    /**
     * Costruisce il pannello di destra con le 6 colonne richieste:
     * Stelle, Nome Utente, Commento, Data, La Tua Risposta e Modificata (con pallino rosso lampeggiante).
     */
    private JPanel creaPannelloDestro() {
        JPanel panel = StileTheKnife.creaCardBianca();
        panel.setMinimumSize(new Dimension(420, 250));
        panel.setLayout(new BorderLayout(10, 12));

        lblRiepilogoLocale = new JLabel("Seleziona un ristorante a sinistra per vedere le recensioni");
        lblRiepilogoLocale.setFont(StileTheKnife.FONT_SOTTOTITOLO);
        lblRiepilogoLocale.setForeground(StileTheKnife.VERDE_THEKNIFE);
        panel.add(lblRiepilogoLocale, BorderLayout.NORTH);

        String[] col = {"Stelle", "Nome Utente", "Commento", "Data", "La Tua Risposta", "Modificata"};
        modelloRecensioni = new DefaultTableModel(col, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabellaRecensioni = new JTable(modelloRecensioni);
        tabellaRecensioni.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        StileTheKnife.applicaStileTabella(tabellaRecensioni);

        // Larghezza ottimizzata per le 6 colonne
        tabellaRecensioni.getColumnModel().getColumn(0).setPreferredWidth(95);  // Stelle
        tabellaRecensioni.getColumnModel().getColumn(1).setPreferredWidth(100); // Nome Utente
        tabellaRecensioni.getColumnModel().getColumn(2).setPreferredWidth(190); // Commento
        tabellaRecensioni.getColumnModel().getColumn(3).setPreferredWidth(85);  // Data
        tabellaRecensioni.getColumnModel().getColumn(4).setPreferredWidth(170); // La Tua Risposta
        tabellaRecensioni.getColumnModel().getColumn(5).setPreferredWidth(80);  // Modificata

        // Renderer personalizzato per il pallino rosso lampeggiante nella colonna 5 ("Modificata")
        tabellaRecensioni.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, "", isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                boolean isModificata = Boolean.TRUE.equals(value);
                if (isModificata && pallinoRossoVisibile) {
                    lbl.setText("●");
                    lbl.setFont(new Font("SansSerif", Font.BOLD, 20));
                    lbl.setForeground(StileTheKnife.ROSSO_NOTIFICA);
                } else {
                    lbl.setText("");
                }
                return lbl;
            }
        });

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
        lblRiepilogoLocale.setText("<html>" + r.getNome() + "   |   Media: " + riepilogo[1]
                + " <span style='color:#F5B000;'>★</span>  (" + (int) riepilogo[0] + " recensioni)</html>");

        recensioniLocaleSelezionato = controller.visualizzaRecensioni(r.getIdRistorante());
        StringBuilder sb = new StringBuilder();
        for (Recensione rec : recensioniLocaleSelezionato) {
            sb.append(rec.getIdRecensione()).append(":").append(rec.getStelle()).append(":")
                    .append(rec.getTesto()).append(":").append(rec.getRispostaRistoratore()).append("|");

            boolean modificataDalCliente = rec.getTesto() != null && rec.getTesto().contains("[MODIFICATA DAL CLIENTE]");
            String testoPulito = rec.getTesto() != null ? rec.getTesto().replace(" [MODIFICATA DAL CLIENTE]", "") : "";
            String username = (rec.getUsernameAutore() != null && !rec.getUsernameAutore().isEmpty())
                    ? rec.getUsernameAutore()
                    : "Cliente";
            String risp = (rec.getRispostaRistoratore() != null && !rec.getRispostaRistoratore().isEmpty())
                    ? rec.getRispostaRistoratore()
                    : "[Ancora nessuna risposta]";

            modelloRecensioni.addRow(new Object[]{
                    "<html>" + StileTheKnife.stelleGialleHtml(rec.getStelle()) + "</html>",
                    username,
                    testoPulito,
                    rec.getDataRecensione(),
                    risp,
                    modificataDalCliente
            });
        }
        ultimaImprontaRistoratore = sb.toString();
    }

    /**
     * Apre una finestra di dialogo ben proporzionata (560x480) divisa in due parti uguali:
     * sopra il commento completo del cliente (scorrevole e mai tagliato) e sotto un ampio box
     * con contatore 0/400 per scrivere o modificare la risposta del ristoratore.
     */
    private void rispondiOModificaRisposta() {
        int riga = tabellaRecensioni.getSelectedRow();
        if (riga < 0 || riga >= recensioniLocaleSelezionato.size()) {
            JOptionPane.showMessageDialog(this, "Seleziona prima una recensione dalla tabella di destra.");
            return;
        }

        Recensione rec = recensioniLocaleSelezionato.get(riga);
        String testoClientePulito = rec.getTesto() != null ? rec.getTesto().replace(" [MODIFICATA DAL CLIENTE]", "") : "";
        String rispostaAttuale = rec.getRispostaRistoratore() != null ? rec.getRispostaRistoratore() : "";
        String autore = (rec.getUsernameAutore() != null) ? rec.getUsernameAutore() : "Cliente";

        // Usiamo GridLayout(2, 1) così il commento sopra non schiaccia mai la casella di risposta sotto!
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 14));

        // 1. Meta superiore: Commento completo del cliente in JTextArea scorrevole
        JPanel boxRecCliente = new JPanel(new BorderLayout(6, 6));
        boxRecCliente.setBackground(StileTheKnife.VERDE_CHIARO_SFONDO);
        boxRecCliente.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, StileTheKnife.VERDE_THEKNIFE),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        JLabel lblStelle = new JLabel("<html>Recensione di <b>" + autore + "</b>  (" + rec.getStelle() + "/5)  "
                + StileTheKnife.stelleGialleHtml(rec.getStelle()) + "</html>");
        lblStelle.setFont(StileTheKnife.FONT_BOTTONE);
        lblStelle.setForeground(StileTheKnife.TESTO_SCURO);

        JTextArea txtCommentoCliente = new JTextArea(testoClientePulito);
        txtCommentoCliente.setFont(StileTheKnife.FONT_NORMALE);
        txtCommentoCliente.setForeground(StileTheKnife.TESTO_SCURO);
        txtCommentoCliente.setBackground(StileTheKnife.VERDE_CHIARO_SFONDO);
        txtCommentoCliente.setLineWrap(true);
        txtCommentoCliente.setWrapStyleWord(true);
        txtCommentoCliente.setEditable(false);
        txtCommentoCliente.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        JScrollPane scrollCommento = new JScrollPane(txtCommentoCliente);
        scrollCommento.setBorder(null);

        boxRecCliente.add(lblStelle, BorderLayout.NORTH);
        boxRecCliente.add(scrollCommento, BorderLayout.CENTER);

        // 2. Metà inferiore: Casella ampia per la risposta del ristoratore + contatore 0/400
        JPanel boxInputRisposta = new JPanel(new BorderLayout(6, 6));
        boxInputRisposta.setOpaque(false);

        JLabel lblPrompt = new JLabel("Scrivi o modifica la tua risposta ufficiale:");
        lblPrompt.setFont(StileTheKnife.FONT_BOTTONE);
        lblPrompt.setForeground(StileTheKnife.VERDE_THEKNIFE);

        JTextArea txtRisposta = new JTextArea(rispostaAttuale, 5, 30);
        txtRisposta.setFont(StileTheKnife.FONT_NORMALE);
        txtRisposta.setLineWrap(true);
        txtRisposta.setWrapStyleWord(true);
        txtRisposta.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JLabel lblContatoreRisp = new JLabel("0/400", SwingConstants.RIGHT);
        lblContatoreRisp.setFont(new Font("SansSerif", Font.BOLD, 12));
        StileTheKnife.applicaLimiteCaratteri(txtRisposta, lblContatoreRisp, 400);

        JScrollPane scrollRisposta = new JScrollPane(txtRisposta);
        scrollRisposta.setBorder(BorderFactory.createLineBorder(StileTheKnife.GRIGIO_BORDO, 1));

        boxInputRisposta.add(lblPrompt, BorderLayout.NORTH);
        boxInputRisposta.add(scrollRisposta, BorderLayout.CENTER);
        boxInputRisposta.add(lblContatoreRisp, BorderLayout.SOUTH);

        panel.add(boxRecCliente);
        panel.add(boxInputRisposta);

        if (StileTheKnife.mostraDialogoStilizzato(this, "Risposta del Ristoratore", panel, "Pubblica Risposta", 560, 480)) {
            String nuovaRisposta = txtRisposta.getText().trim();
            if (!nuovaRisposta.isEmpty() && controller.rispostaRecensioni(rec.getIdRecensione(), nuovaRisposta)) {
                caricaRecensioniRistoranteSelezionato();
            }
        }
    }

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
                        caricaMieiRistoranti();
                    }
                } else {
                    Ristorante nuovo = new Ristorante(
                            nome, nazione, citta, indirizzo, lat, lon, prezzo,
                            chkDel.isSelected(), chkPren.isSelected(), cucina, null
                    );
                    if (controller.aggiungiRistorante(nuovo)) {
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
            caricaMieiRistoranti();
            modelloRecensioni.setRowCount(0);
        }
    }
}