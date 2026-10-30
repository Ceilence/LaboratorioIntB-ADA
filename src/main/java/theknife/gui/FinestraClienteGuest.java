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
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Finestra principale per Clienti e Ospiti con bandiere nazionali, descrizione del ristorante,
 * riquadro integrato per scrivere recensioni con 5 stelle interattive (effetto hover + click)
 * e sincronizzazione automatica in tempo reale con il database.
 * @author Alessandro Frigerio
 * @author Antonio Pardo
 * @author Davide Moretti
 */
public class FinestraClienteGuest extends JFrame {

    private final ControllerTheKnife controller;
    private List<Ristorante> ristorantiCorrenti = new ArrayList<>();
    private List<Ristorante> preferitiCorrenti = new ArrayList<>();
    private List<Recensione> mieRecensioniCorrenti = new ArrayList<>();
    private Ristorante ristoranteSelezionato = null;

    // Timer e versione per l'aggiornamento automatico in tempo reale
    private Timer timerSyncRealTime;
    private long ultimaVersioneNota = -1;

    private CardLayout cardLayoutCentrale;
    private JPanel panelContenitoreViste;
    private JLabel lblStatoHeader;

    // Colonna sinistra
    private JTextField txtBarraRicercaNome;
    private JPanel panelFiltriAvanzati;
    private JTextField txtLocazione;
    private JTextField txtCucina;
    private JTextField txtPrezzoMin;
    private JTextField txtPrezzoMax;
    private JCheckBox chkDelivery;
    private JCheckBox chkPrenotazione;
    private JComboBox<String> cmbStelleMin;
    private DefaultListModel<Ristorante> modelloListaCard;
    private JList<Ristorante> listaCardRistoranti;
    private JLabel lblContatoreRisultati;

    // Colonna destra (Dettagli, Bandiera, Descrizione, Box 5 Stelle inline e Recensioni)
    private JPanel panelBandieraDettaglio;
    private JLabel lblDettaglioNome;
    private JLabel lblDettaglioCucina;
    private JLabel lblDettaglioPrezzo;
    private JLabel lblDettaglioIndirizzo;
    private JTextArea txtDettaglioDescrizione;
    private JLabel lblBadgeDelivery;
    private JLabel lblBadgePrenotazione;
    private JLabel lblValutazioneMedia;
    private JButton btnAggiungiPreferito;

    // Componenti Riquadro Scrittura Recensione Integrato con 5 Stelle interattive
    private int stelleSelezionateInline = 5;
    private final JLabel[] lblStelleInterattive = new JLabel[5];
    private JLabel lblTestoVotoScelto;
    private JTextArea txtNuovaRecensioneInline;
    private JButton btnPubblicaRecensioneInline;

    private JPanel panelContenitoreRecensioni;

    // Tabelle Preferiti e Mie Recensioni
    private DefaultTableModel modelloPreferiti;
    private JTable tabellaPreferiti;
    private DefaultTableModel modelloMieRecensioni;
    private JTable tabellaMieRecensioni;

    public FinestraClienteGuest(ControllerTheKnife controller) {
        super("TheKnife - Scopri i Ristoranti");
        this.controller = controller;
        this.ultimaVersioneNota = controller.getVersioneDatiServer();
        inizializzaGUI();
        eseguiRicercaIniziale();
        avviaSincronizzazioneRealTime();
    }

    private void inizializzaGUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1240, 780);
        setMinimumSize(new Dimension(880, 580));
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

        Utente utente = controller.getSessione().getUtenteCorrente();

        // 1. TOP BAR CON LOGO, FONT BRAND "THE KNIFE" E SPAZI SENZA TRATTINI
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(StileTheKnife.VERDE_THEKNIFE);
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));

        JPanel boxSinistroTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        boxSinistroTop.setOpaque(false);
        boxSinistroTop.add(StileTheKnife.creaLabelLogo(42));

        JLabel lblBrand = new JLabel("THE KNIFE");
        lblBrand.setFont(StileTheKnife.FONT_BRAND);
        lblBrand.setForeground(StileTheKnife.BIANCO);
        boxSinistroTop.add(lblBrand);

        // Spazio pulito invece del doppio trattino
        boxSinistroTop.add(Box.createHorizontalStrut(18));

        lblStatoHeader = new JLabel();
        lblStatoHeader.setFont(StileTheKnife.FONT_SOTTOTITOLO);
        lblStatoHeader.setForeground(StileTheKnife.VERDE_CHIARO_SFONDO);
        aggiornaTestoHeader();
        boxSinistroTop.add(lblStatoHeader);

        topBar.add(boxSinistroTop, BorderLayout.WEST);

        JPanel boxDestroTop = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 4));
        boxDestroTop.setOpaque(false);

        if (utente.isCliente()) {
            JButton btnMenuProfilo = StileTheKnife.creaBottoneSecondario("👤 Il Mio Account ▾");
            JPopupMenu menuTendina = creaMenuTendinaCliente();
            btnMenuProfilo.addActionListener(e -> menuTendina.show(btnMenuProfilo, 0, btnMenuProfilo.getHeight()));
            boxDestroTop.add(btnMenuProfilo);
        } else {
            JButton btnAccedi = StileTheKnife.creaBottoneSecondario("Accedi / Registrati");
            btnAccedi.addActionListener(e -> {
                if (timerSyncRealTime != null) timerSyncRealTime.stop();
                controller.logout();
                dispose();
                new FinestraIniziale(controller).setVisible(true);
            });
            boxDestroTop.add(btnAccedi);
        }

        topBar.add(boxDestroTop, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // 2. CONTENITORE VISTE
        cardLayoutCentrale = new CardLayout();
        panelContenitoreViste = new JPanel(cardLayoutCentrale);
        panelContenitoreViste.setBackground(StileTheKnife.VERDE_THEKNIFE);

        panelContenitoreViste.add(creaSplitEsplorazione(), "ESPLORA");
        if (utente.isCliente()) {
            panelContenitoreViste.add(creaPannelloPreferiti(), "PREFERITI");
            panelContenitoreViste.add(creaPannelloMieRecensioni(), "RECENSIONI");
        }

        add(panelContenitoreViste, BorderLayout.CENTER);
    }

    /**
     * Aggiorna il testo in alto usando solo "Ospite" per il guest e senza trattini.
     */
    private void aggiornaTestoHeader() {
        Utente utente = controller.getSessione().getUtenteCorrente();
        String testo = utente.isGuest()
                ? "Ospite   |   Luogo: " + utente.getLuogoDomicilio()
                : "Cliente: " + utente.getNome() + " " + utente.getCognome() + " (@" + utente.getUsername() + ")   |   Domicilio: " + utente.getLuogoDomicilio();
        lblStatoHeader.setText(testo);
    }

    /**
     * Avvia un Timer leggero che controlla ogni 3 secondi se un altro client ha modificato il DB
     * e aggiorna automaticamente la pagina aperta in tempo reale.
     */
    private void avviaSincronizzazioneRealTime() {
        timerSyncRealTime = new Timer(3000, e -> {
            long versioneAttuale = controller.getVersioneDatiServer();
            if (versioneAttuale != -1 && versioneAttuale != ultimaVersioneNota) {
                ultimaVersioneNota = versioneAttuale;
                if (ristoranteSelezionato != null) {
                    Ristorante rAggiornato = controller.visualizzaRistorante(ristoranteSelezionato.getIdRistorante());
                    if (rAggiornato != null) {
                        mostraDettagliRistoranteADestra(rAggiornato);
                    }
                }
                if (controller.getSessione().getUtenteCorrente().isCliente() && modelloMieRecensioni != null) {
                    aggiornaTabellaMieRecensioni();
                    aggiornaTabellaPreferiti();
                }
            }
        });
        timerSyncRealTime.start();
    }

    private JPopupMenu creaMenuTendinaCliente() {
        JPopupMenu menu = new JPopupMenu();
        menu.setBackground(StileTheKnife.BIANCO);
        menu.setBorder(BorderFactory.createLineBorder(StileTheKnife.VERDE_THEKNIFE, 2));

        JMenuItem itemEsplora = creaVoceMenu("🔍  Esplora Ristoranti");
        JMenuItem itemPreferiti = creaVoceMenu("★  I Miei Ristoranti Preferiti");
        JMenuItem itemRecensioni = creaVoceMenu("✍  Le Mie Recensioni");
        JMenuItem itemProfilo = creaVoceMenu("⚙  Modifica Profilo e Credenziali");
        JMenuItem itemLogout = creaVoceMenu("🚪  Logout");

        itemEsplora.addActionListener(e -> cardLayoutCentrale.show(panelContenitoreViste, "ESPLORA"));
        itemPreferiti.addActionListener(e -> {
            aggiornaTabellaPreferiti();
            cardLayoutCentrale.show(panelContenitoreViste, "PREFERITI");
        });
        itemRecensioni.addActionListener(e -> {
            aggiornaTabellaMieRecensioni();
            cardLayoutCentrale.show(panelContenitoreViste, "RECENSIONI");
        });
        itemProfilo.addActionListener(e -> new DialogModificaProfilo(this, controller, this::aggiornaTestoHeader).setVisible(true));
        itemLogout.addActionListener(e -> {
            if (timerSyncRealTime != null) timerSyncRealTime.stop();
            controller.logout();
            dispose();
            new FinestraIniziale(controller).setVisible(true);
        });

        menu.add(itemEsplora);
        menu.add(itemPreferiti);
        menu.add(itemRecensioni);
        menu.addSeparator();
        menu.add(itemProfilo);
        menu.addSeparator();
        menu.add(itemLogout);
        return menu;
    }

    private JMenuItem creaVoceMenu(String testo) {
        JMenuItem item = new JMenuItem(testo);
        item.setFont(StileTheKnife.FONT_NORMALE);
        item.setBackground(StileTheKnife.BIANCO);
        item.setForeground(StileTheKnife.TESTO_SCURO);
        item.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 20));
        return item;
    }

    private JSplitPane creaSplitEsplorazione() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, creaColonnaSinistraLista(), creaColonnaDestraDettaglio());
        split.setDividerLocation(420);
        split.setResizeWeight(0.32);
        split.setContinuousLayout(true);
        StileTheKnife.applicaSplitVerdeContinuo(split);
        return split;
    }

    /**
     * Costruisce la colonna di sinistra con larghezza minima fissata a 310px così la barra di ricerca
     * non scompare mai e le scritte "Ristoranti trovati" e "Reset Filtri" non si sovrappongono.
     * @return il pannello <code>JPanel</code> sinistro
     */
    private JPanel creaColonnaSinistraLista() {
        JPanel panelSinistro = new JPanel(new BorderLayout(0, 0));
        panelSinistro.setBackground(StileTheKnife.BIANCO);
        panelSinistro.setMinimumSize(new Dimension(310, 250));

        JPanel boxRicercaTop = new JPanel();
        boxRicercaTop.setLayout(new BoxLayout(boxRicercaTop, BoxLayout.Y_AXIS));
        boxRicercaTop.setBackground(StileTheKnife.GRIGIO_SFONDO);
        boxRicercaTop.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, StileTheKnife.VERDE_THEKNIFE),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        JPanel rigaBarra = new JPanel(new BorderLayout(6, 0));
        rigaBarra.setOpaque(false);

        txtBarraRicercaNome = StileTheKnife.creaCampoTesto(10);
        txtBarraRicercaNome.setMinimumSize(new Dimension(110, 38));
        txtBarraRicercaNome.setToolTipText("Lascia vuoto per visualizzare tutti i ristoranti");
        txtBarraRicercaNome.addActionListener(e -> eseguiRicercaFiltrata());

        JPanel bottoniBarra = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        bottoniBarra.setOpaque(false);

        JButton btnCercaRapido = StileTheKnife.creaBottonePrimario("Cerca");
        JButton btnToggleFiltri = StileTheKnife.creaBottoneSecondario("Filtri ▾");

        btnCercaRapido.addActionListener(e -> eseguiRicercaFiltrata());

        bottoniBarra.add(btnCercaRapido);
        bottoniBarra.add(btnToggleFiltri);

        rigaBarra.add(txtBarraRicercaNome, BorderLayout.CENTER);
        rigaBarra.add(bottoniBarra, BorderLayout.EAST);
        boxRicercaTop.add(rigaBarra);

        panelFiltriAvanzati = new JPanel(new GridLayout(6, 2, 6, 6));
        panelFiltriAvanzati.setBackground(StileTheKnife.VERDE_CHIARO_SFONDO);
        panelFiltriAvanzati.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelFiltriAvanzati.setVisible(false);

        txtLocazione = StileTheKnife.creaCampoTesto(8);
        txtLocazione.setText(controller.getSessione().getLuogoRiferimento());
        txtCucina = StileTheKnife.creaCampoTesto(8);
        txtPrezzoMin = StileTheKnife.creaCampoTesto(4);
        txtPrezzoMax = StileTheKnife.creaCampoTesto(4);

        chkDelivery = new JCheckBox("Delivery");
        chkDelivery.setOpaque(false);
        chkDelivery.setFont(StileTheKnife.FONT_NORMALE);

        chkPrenotazione = new JCheckBox("Prenotazione Online");
        chkPrenotazione.setOpaque(false);
        chkPrenotazione.setFont(StileTheKnife.FONT_NORMALE);

        cmbStelleMin = new JComboBox<>(new String[]{"Qualsiasi voto", "Almeno 1 ★", "Almeno 2 ★", "Almeno 3 ★", "Almeno 4 ★", "5 ★"});
        cmbStelleMin.setFont(StileTheKnife.FONT_NORMALE);
        cmbStelleMin.setBackground(StileTheKnife.BIANCO);

        JButton btnResetFiltri = StileTheKnife.creaBottoneSecondario("Reset Filtri");
        btnResetFiltri.addActionListener(e -> resettaFiltri());

        panelFiltriAvanzati.add(StileTheKnife.creaEtichetta("Luogo (Città/Nazione):"));
        panelFiltriAvanzati.add(txtLocazione);
        panelFiltriAvanzati.add(StileTheKnife.creaEtichetta("Tipologia Cucina:"));
        panelFiltriAvanzati.add(txtCucina);
        panelFiltriAvanzati.add(StileTheKnife.creaEtichetta("Prezzo Minimo (€):"));
        panelFiltriAvanzati.add(txtPrezzoMin);
        panelFiltriAvanzati.add(StileTheKnife.creaEtichetta("Prezzo Massimo (€):"));
        panelFiltriAvanzati.add(txtPrezzoMax);
        panelFiltriAvanzati.add(StileTheKnife.creaEtichetta("Media Stelle:"));
        panelFiltriAvanzati.add(cmbStelleMin);
        panelFiltriAvanzati.add(chkDelivery);
        panelFiltriAvanzati.add(chkPrenotazione);

        btnToggleFiltri.addActionListener(e -> {
            boolean visibile = !panelFiltriAvanzati.isVisible();
            panelFiltriAvanzati.setVisible(visibile);
            btnToggleFiltri.setText(visibile ? "Filtri ▴" : "Filtri ▾");
            boxRicercaTop.revalidate();
        });

        boxRicercaTop.add(Box.createVerticalStrut(6));
        boxRicercaTop.add(panelFiltriAvanzati);

        JPanel barraInfoLista = new JPanel(new BorderLayout(8, 0));
        barraInfoLista.setOpaque(false);
        barraInfoLista.setBorder(BorderFactory.createEmptyBorder(8, 2, 0, 2));
        lblContatoreRisultati = StileTheKnife.creaEtichetta("Ristoranti trovati: 0");
        lblContatoreRisultati.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblContatoreRisultati.setForeground(StileTheKnife.VERDE_THEKNIFE);

        barraInfoLista.add(lblContatoreRisultati, BorderLayout.WEST);
        barraInfoLista.add(btnResetFiltri, BorderLayout.EAST);
        boxRicercaTop.add(barraInfoLista);

        panelSinistro.add(boxRicercaTop, BorderLayout.NORTH);

        modelloListaCard = new DefaultListModel<>();
        listaCardRistoranti = new JList<>(modelloListaCard);
        listaCardRistoranti.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaCardRistoranti.setBackground(StileTheKnife.BIANCO);
        listaCardRistoranti.setCellRenderer(new RistoranteCardRenderer());

        listaCardRistoranti.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Ristorante scelto = listaCardRistoranti.getSelectedValue();
                if (scelto != null) {
                    Ristorante aggiornato = controller.visualizzaRistorante(scelto.getIdRistorante());
                    mostraDettagliRistoranteADestra(aggiornato != null ? aggiornato : scelto);
                }
            }
        });

        JScrollPane scrollLista = new JScrollPane(listaCardRistoranti);
        scrollLista.setBorder(null);
        scrollLista.getVerticalScrollBar().setUnitIncrement(16);
        panelSinistro.add(scrollLista, BorderLayout.CENTER);

        return panelSinistro;
    }

    /**
     * Costruisce la colonna di destra con Bandiera, Dettagli, Descrizione, Box scrittura recensione
     * con 5 stelle interattive (hover + click), linea grigia divisoria e recensioni scorrevoli sotto.
     * @return il pannello <code>JPanel</code> destro
     */
    private JPanel creaColonnaDestraDettaglio() {
        JPanel panelDestro = new JPanel(new BorderLayout(0, 10));
        panelDestro.setBackground(StileTheKnife.BIANCO);
        panelDestro.setMinimumSize(new Dimension(420, 250));
        panelDestro.setBorder(BorderFactory.createEmptyBorder(20, 26, 20, 26));

        // 1. BLOCCO SUPERIORE: Bandiera + Titolo + Indirizzo + Descrizione + Badge
        JPanel panelInfoSuperiore = new JPanel();
        panelInfoSuperiore.setLayout(new BoxLayout(panelInfoSuperiore, BoxLayout.Y_AXIS));
        panelInfoSuperiore.setBackground(StileTheKnife.BIANCO);

        // Intestazione con Bandiera a sinistra, Nome + Cucina al centro, Prezzo + Stella Preferiti a destra
        JPanel rigaHeaderRistorante = new JPanel(new BorderLayout(14, 0));
        rigaHeaderRistorante.setBackground(StileTheKnife.BIANCO);
        rigaHeaderRistorante.setAlignmentX(Component.LEFT_ALIGNMENT);

        panelBandieraDettaglio = new JPanel(new BorderLayout());
        panelBandieraDettaglio.setBackground(StileTheKnife.BIANCO);
        panelBandieraDettaglio.add(StileTheKnife.creaLabelBandiera("Globe", 56, 38), BorderLayout.CENTER);

        JPanel boxTitoloECucina = new JPanel(new GridLayout(2, 1, 0, 2));
        boxTitoloECucina.setBackground(StileTheKnife.BIANCO);

        lblDettaglioNome = new JLabel("Seleziona un ristorante");
        lblDettaglioNome.setFont(new Font("SansSerif", Font.BOLD, 25));
        lblDettaglioNome.setForeground(StileTheKnife.TESTO_SCURO);

        lblDettaglioCucina = new JLabel("Clicca su un locale a sinistra per vedere i dettagli");
        lblDettaglioCucina.setFont(new Font("SansSerif", Font.PLAIN, 15));
        lblDettaglioCucina.setForeground(StileTheKnife.VERDE_THEKNIFE);

        boxTitoloECucina.add(lblDettaglioNome);
        boxTitoloECucina.add(lblDettaglioCucina);

        JPanel boxPrezzoEPreferito = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        boxPrezzoEPreferito.setBackground(StileTheKnife.BIANCO);

        lblDettaglioPrezzo = new JLabel("");
        lblDettaglioPrezzo.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblDettaglioPrezzo.setForeground(StileTheKnife.VERDE_THEKNIFE);
        boxPrezzoEPreferito.add(lblDettaglioPrezzo);

        Utente u = controller.getSessione().getUtenteCorrente();
        if (u != null && u.isCliente()) {
            btnAggiungiPreferito = StileTheKnife.creaBottoneSecondario("★ Salva nei Preferiti");
            btnAggiungiPreferito.setEnabled(false);
            btnAggiungiPreferito.addActionListener(e -> {
                if (ristoranteSelezionato != null) {
                    if (controller.aggiungiPreferito(ristoranteSelezionato.getIdRistorante())) {
                        JOptionPane.showMessageDialog(this, "Ristorante aggiunto ai tuoi preferiti!");
                    } else {
                        JOptionPane.showMessageDialog(this, "Questo ristorante è già nei tuoi preferiti.");
                    }
                }
            });
            boxPrezzoEPreferito.add(btnAggiungiPreferito);
        }

        rigaHeaderRistorante.add(panelBandieraDettaglio, BorderLayout.WEST);
        rigaHeaderRistorante.add(boxTitoloECucina, BorderLayout.CENTER);
        rigaHeaderRistorante.add(boxPrezzoEPreferito, BorderLayout.EAST);

        lblDettaglioIndirizzo = StileTheKnife.creaEtichetta("");
        lblDettaglioIndirizzo.setForeground(StileTheKnife.TESTO_GRIGIO);
        lblDettaglioIndirizzo.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Descrizione del Ristorante
        txtDettaglioDescrizione = new JTextArea();
        txtDettaglioDescrizione.setFont(StileTheKnife.FONT_NORMALE);
        txtDettaglioDescrizione.setForeground(StileTheKnife.TESTO_SCURO);
        txtDettaglioDescrizione.setBackground(StileTheKnife.GRIGIO_SFONDO);
        txtDettaglioDescrizione.setLineWrap(true);
        txtDettaglioDescrizione.setWrapStyleWord(true);
        txtDettaglioDescrizione.setEditable(false);
        txtDettaglioDescrizione.setFocusable(false);
        txtDettaglioDescrizione.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 3, 0, 0, StileTheKnife.VERDE_THEKNIFE),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        txtDettaglioDescrizione.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Riga Valutazione Media e Servizi
        JPanel rigaValutazione = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        rigaValutazione.setBackground(StileTheKnife.BIANCO);
        rigaValutazione.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblValutazioneMedia = new JLabel("Valutazione media: -");
        lblValutazioneMedia.setFont(StileTheKnife.FONT_BOTTONE);
        lblValutazioneMedia.setForeground(StileTheKnife.BIANCO);
        lblValutazioneMedia.setBackground(StileTheKnife.VERDE_THEKNIFE);
        lblValutazioneMedia.setOpaque(true);
        lblValutazioneMedia.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));

        lblBadgeDelivery = new JLabel("Delivery: -");
        lblBadgeDelivery.setFont(StileTheKnife.FONT_NORMALE);
        lblBadgeDelivery.setBackground(StileTheKnife.VERDE_CHIARO_SFONDO);
        lblBadgeDelivery.setForeground(StileTheKnife.VERDE_THEKNIFE);
        lblBadgeDelivery.setOpaque(true);
        lblBadgeDelivery.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));

        lblBadgePrenotazione = new JLabel("Prenotazione Online: -");
        lblBadgePrenotazione.setFont(StileTheKnife.FONT_NORMALE);
        lblBadgePrenotazione.setBackground(StileTheKnife.VERDE_CHIARO_SFONDO);
        lblBadgePrenotazione.setForeground(StileTheKnife.VERDE_THEKNIFE);
        lblBadgePrenotazione.setOpaque(true);
        lblBadgePrenotazione.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));

        rigaValutazione.add(lblValutazioneMedia);
        rigaValutazione.add(lblBadgeDelivery);
        rigaValutazione.add(lblBadgePrenotazione);

        panelInfoSuperiore.add(rigaHeaderRistorante);
        panelInfoSuperiore.add(Box.createVerticalStrut(10));
        panelInfoSuperiore.add(lblDettaglioIndirizzo);
        panelInfoSuperiore.add(Box.createVerticalStrut(10));
        panelInfoSuperiore.add(txtDettaglioDescrizione);
        panelInfoSuperiore.add(Box.createVerticalStrut(10));
        panelInfoSuperiore.add(rigaValutazione);

        // 2. RIQUADRO INTEGRATO NELLA PAGINA PER SCRIVERE LA RECENSIONE CON 5 STELLE INTERATTIVE
        if (u != null && u.isCliente()) {
            panelInfoSuperiore.add(Box.createVerticalStrut(8));
            panelInfoSuperiore.add(creaBoxScritturaRecensioneInline());
        } else {
            JLabel lblAvvisoGuest = StileTheKnife.creaEtichetta("Modalità Ospite: effettua il login come Cliente per salvare nei preferiti o lasciare una recensione.");
            lblAvvisoGuest.setForeground(StileTheKnife.TESTO_GRIGIO);
            lblAvvisoGuest.setBorder(BorderFactory.createEmptyBorder(6, 4, 4, 4));
            lblAvvisoGuest.setAlignmentX(Component.LEFT_ALIGNMENT);
            panelInfoSuperiore.add(lblAvvisoGuest);
        }

        panelDestro.add(panelInfoSuperiore, BorderLayout.NORTH);

        // 3. LINEA DI SEPARAZIONE GRIGIA CHIARA E RECENSIONI SCORREVOLI SOTTO
        JPanel wrapperRecensioni = new JPanel(new BorderLayout(0, 8));
        wrapperRecensioni.setBackground(StileTheKnife.BIANCO);
        wrapperRecensioni.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, StileTheKnife.GRIGIO_BORDO));

        JLabel lblTitoloRec = new JLabel("Recensioni lasciate dai Clienti:");
        lblTitoloRec.setFont(StileTheKnife.FONT_SOTTOTITOLO);
        lblTitoloRec.setForeground(StileTheKnife.VERDE_THEKNIFE);
        lblTitoloRec.setBorder(BorderFactory.createEmptyBorder(10, 0, 4, 0));
        wrapperRecensioni.add(lblTitoloRec, BorderLayout.NORTH);

        panelContenitoreRecensioni = new JPanel();
        panelContenitoreRecensioni.setLayout(new BoxLayout(panelContenitoreRecensioni, BoxLayout.Y_AXIS));
        panelContenitoreRecensioni.setBackground(StileTheKnife.BIANCO);

        JScrollPane scrollRecensioni = new JScrollPane(panelContenitoreRecensioni);
        scrollRecensioni.setBorder(null);
        scrollRecensioni.getVerticalScrollBar().setUnitIncrement(16);
        wrapperRecensioni.add(scrollRecensioni, BorderLayout.CENTER);

        panelDestro.add(wrapperRecensioni, BorderLayout.CENTER);

        return panelDestro;
    }

    /**
     * Crea il riquadro integrato nella scheda del ristorante con 5 stelle che si illuminano
     * in anteprima al passaggio del mouse (hover) e si confermano al click.
     * @return il pannello <code>JPanel</code> per scrivere la recensione
     */
    private JPanel creaBoxScritturaRecensioneInline() {
        JPanel box = new JPanel(new BorderLayout(10, 8));
        box.setBackground(StileTheKnife.GRIGIO_SFONDO);
        box.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(StileTheKnife.GRIGIO_BORDO, 1),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));
        box.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Barra superiore del box con le 5 stelle interattive
        JPanel rigaStelle = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        rigaStelle.setOpaque(false);

        JLabel lblTitoloBox = new JLabel("Lascia la tua recensione:  ");
        lblTitoloBox.setFont(StileTheKnife.FONT_BOTTONE);
        lblTitoloBox.setForeground(StileTheKnife.VERDE_THEKNIFE);
        rigaStelle.add(lblTitoloBox);

        for (int i = 0; i < 5; i++) {
            final int valoreStella = i + 1;
            JLabel stella = new JLabel("★");
            stella.setFont(new Font("SansSerif", Font.BOLD, 24));
            stella.setCursor(new Cursor(Cursor.HAND_CURSOR));

            stella.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    // Anteprima colorazione passando sopra col mouse senza cliccare
                    aggiornaColoriStelle(valoreStella, true);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    // Ripristina la selezione confermata quando il mouse esce
                    aggiornaColoriStelle(stelleSelezionateInline, false);
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    // Conferma il voto cliccando sulla stella
                    stelleSelezionateInline = valoreStella;
                    aggiornaColoriStelle(stelleSelezionateInline, false);
                }
            });

            lblStelleInterattive[i] = stella;
            rigaStelle.add(stella);
        }

        lblTestoVotoScelto = new JLabel("  (5/5 stelle selezionate)");
        lblTestoVotoScelto.setFont(StileTheKnife.FONT_NORMALE);
        lblTestoVotoScelto.setForeground(StileTheKnife.TESTO_GRIGIO);
        rigaStelle.add(lblTestoVotoScelto);

        aggiornaColoriStelle(stelleSelezionateInline, false);

        // Casella di testo e bottone Pubblica a destra
        JPanel rigaInputCommento = new JPanel(new BorderLayout(10, 0));
        rigaInputCommento.setOpaque(false);

        txtNuovaRecensioneInline = new JTextArea(2, 30);
        txtNuovaRecensioneInline.setFont(StileTheKnife.FONT_NORMALE);
        txtNuovaRecensioneInline.setLineWrap(true);
        txtNuovaRecensioneInline.setWrapStyleWord(true);
        txtNuovaRecensioneInline.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));

        JScrollPane scrollTxt = new JScrollPane(txtNuovaRecensioneInline);
        scrollTxt.setBorder(BorderFactory.createLineBorder(StileTheKnife.GRIGIO_BORDO));

        btnPubblicaRecensioneInline = StileTheKnife.creaBottonePrimario("Pubblica Recensione");
        btnPubblicaRecensioneInline.setEnabled(false);
        btnPubblicaRecensioneInline.addActionListener(e -> pubblicaRecensioneInline());

        rigaInputCommento.add(scrollTxt, BorderLayout.CENTER);
        rigaInputCommento.add(btnPubblicaRecensioneInline, BorderLayout.EAST);

        box.add(rigaStelle, BorderLayout.NORTH);
        box.add(rigaInputCommento, BorderLayout.CENTER);
        return box;
    }

    /**
     * Colora le stelle fino a <code>numeroStelle</code> (mostrando anche l'anteprima hover).
     * @param numeroStelle numero di stelle da illuminare da 1 a 5
     * @param isAnteprimaHover true se l'utente sta solo passando sopra col mouse
     */
    private void aggiornaColoriStelle(int numeroStelle, boolean isAnteprimaHover) {
        for (int i = 0; i < 5; i++) {
            if (i < numeroStelle) {
                lblStelleInterattive[i].setText("★");
                lblStelleInterattive[i].setForeground(isAnteprimaHover ? StileTheKnife.VERDE_HOVER : StileTheKnife.GIALLO_STELLA);
            } else {
                lblStelleInterattive[i].setText("☆");
                lblStelleInterattive[i].setForeground(StileTheKnife.TESTO_GRIGIO);
            }
        }
        if (lblTestoVotoScelto != null) {
            lblTestoVotoScelto.setText(isAnteprimaHover
                    ? "  (Anteprima: " + numeroStelle + "/5 stelle - clicca per confermare)"
                    : "  (" + numeroStelle + "/5 stelle confermate)");
        }
    }

    /**
     * Invia la recensione scritta nel riquadro integrato della pagina ristorante.
     */
    private void pubblicaRecensioneInline() {
        if (ristoranteSelezionato == null) {
            return;
        }
        String testo = txtNuovaRecensioneInline.getText().trim();
        if (testo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Scrivi un commento prima di pubblicare la recensione.", "Commento Vuoto", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (controller.aggiungiRecensione(ristoranteSelezionato.getIdRistorante(), stelleSelezionateInline, testo)) {
            txtNuovaRecensioneInline.setText("");
            ultimaVersioneNota = controller.getVersioneDatiServer();
            mostraDettagliRistoranteADestra(ristoranteSelezionato);
            JOptionPane.showMessageDialog(this, "Recensione pubblicata con successo!");
        } else {
            JOptionPane.showMessageDialog(this,
                    "Hai già scritto una recensione per questo ristorante.\nPuoi modificarla dal menù in alto a destra -> 'Le Mie Recensioni'.",
                    "Recensione Già Presente", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Aggiorna tutti i dettagli del ristorante selezionato a destra (inclusa bandiera e descrizione).
     * @param r il <code>Ristorante</code> selezionato
     */
    private void mostraDettagliRistoranteADestra(Ristorante r) {
        this.ristoranteSelezionato = r;

        panelBandieraDettaglio.removeAll();
        panelBandieraDettaglio.add(StileTheKnife.creaLabelBandiera(r.getNazione(), 56, 38), BorderLayout.CENTER);
        panelBandieraDettaglio.revalidate();
        panelBandieraDettaglio.repaint();

        lblDettaglioNome.setText(r.getNome());
        lblDettaglioCucina.setText(r.getTipoCucina());
        lblDettaglioPrezzo.setText("Fascia di prezzo: " + StileTheKnife.formattaFasciaPrezzo(r.getFasciaPrezzo()));
        lblDettaglioIndirizzo.setText("Indirizzo: " + r.getIndirizzo() + ", " + r.getCitta() + ", " + r.getNazione()
                + "   (GPS: " + r.getLatitudine() + ", " + r.getLongitudine() + ")");

        txtDettaglioDescrizione.setText(StileTheKnife.generaDescrizioneRistorante(r));

        lblBadgeDelivery.setText("Delivery: " + (r.isDelivery() ? "Disponibile" : "Non disponibile"));
        lblBadgePrenotazione.setText("Prenotazione Online: " + (r.isPrenotazioneOnline() ? "Disponibile" : "Non disponibile"));

        if (btnAggiungiPreferito != null) {
            btnAggiungiPreferito.setEnabled(true);
        }
        if (btnPubblicaRecensioneInline != null) {
            btnPubblicaRecensioneInline.setEnabled(true);
        }

        double[] riepilogo = controller.visualizzaRiepilogo(r.getIdRistorante());
        lblValutazioneMedia.setText(String.format("★ Media: %.1f / 5.0 (%d recensioni)", riepilogo[1], (int) riepilogo[0]));

        panelContenitoreRecensioni.removeAll();
        List<Recensione> recensioni = controller.visualizzaRecensioni(r.getIdRistorante());

        if (recensioni.isEmpty()) {
            JLabel lblVuota = StileTheKnife.creaEtichetta("Ancora nessuna recensione presente per questo ristorante.");
            lblVuota.setForeground(StileTheKnife.TESTO_GRIGIO);
            lblVuota.setBorder(BorderFactory.createEmptyBorder(15, 5, 15, 5));
            panelContenitoreRecensioni.add(lblVuota);
        } else {
            for (Recensione rec : recensioni) {
                JPanel rigaRec = new JPanel(new BorderLayout(6, 4));
                rigaRec.setBackground(StileTheKnife.BIANCO);
                rigaRec.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, StileTheKnife.GRIGIO_BORDO),
                        BorderFactory.createEmptyBorder(10, 4, 10, 4)
                ));
                rigaRec.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

                String stelle = "★".repeat(Math.max(0, rec.getStelle())) + "☆".repeat(Math.max(0, 5 - rec.getStelle()));
                JLabel lblTopRec = new JLabel(stelle + "  (" + rec.getStelle() + "/5)   —   Cliente Anonimo   [" + rec.getDataRecensione() + "]");
                lblTopRec.setFont(StileTheKnife.FONT_BOTTONE);
                lblTopRec.setForeground(StileTheKnife.VERDE_THEKNIFE);

                JLabel lblTestoRec = new JLabel("<html><div style='width:450px;'>" + rec.getTesto() + "</div></html>");
                lblTestoRec.setFont(StileTheKnife.FONT_NORMALE);
                lblTestoRec.setForeground(StileTheKnife.TESTO_SCURO);

                rigaRec.add(lblTopRec, BorderLayout.NORTH);
                rigaRec.add(lblTestoRec, BorderLayout.CENTER);

                if (rec.getRispostaRistoratore() != null && !rec.getRispostaRistoratore().isEmpty()) {
                    JLabel lblRisp = new JLabel("<html><b>Risposta del Ristoratore:</b> " + rec.getRispostaRistoratore() + "</html>");
                    lblRisp.setFont(StileTheKnife.FONT_NORMALE);
                    lblRisp.setForeground(StileTheKnife.VERDE_THEKNIFE);
                    lblRisp.setBorder(BorderFactory.createEmptyBorder(4, 12, 0, 0));
                    rigaRec.add(lblRisp, BorderLayout.SOUTH);
                }

                panelContenitoreRecensioni.add(rigaRec);
            }
        }

        panelContenitoreRecensioni.revalidate();
        panelContenitoreRecensioni.repaint();
    }

    /**
     * Renderer delle Card a sinistra con il riquadro della Bandiera nazionale accanto a ogni ristorante.
     */
    private static class RistoranteCardRenderer extends JPanel implements ListCellRenderer<Ristorante> {
        private final JLabel lblBandiera = new JLabel();
        private final JLabel lblNome = new JLabel();
        private final JLabel lblCucina = new JLabel();
        private final JLabel lblCitta = new JLabel();
        private final JLabel lblPrezzo = new JLabel();

        public RistoranteCardRenderer() {
            setLayout(new BorderLayout(10, 4));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, StileTheKnife.GRIGIO_BORDO),
                    BorderFactory.createEmptyBorder(10, 12, 10, 12)
            ));

            lblBandiera.setPreferredSize(new Dimension(38, 26));
            lblNome.setFont(new Font("SansSerif", Font.BOLD, 15));
            lblCucina.setFont(new Font("SansSerif", Font.PLAIN, 13));
            lblCitta.setFont(new Font("SansSerif", Font.BOLD, 12));
            lblPrezzo.setFont(new Font("SansSerif", Font.BOLD, 14));

            JPanel centro = new JPanel(new GridLayout(2, 1, 0, 2));
            centro.setOpaque(false);
            centro.add(lblNome);
            centro.add(lblCucina);

            JPanel destra = new JPanel(new GridLayout(2, 1, 0, 2));
            destra.setOpaque(false);
            lblPrezzo.setHorizontalAlignment(JLabel.RIGHT);
            lblCitta.setHorizontalAlignment(JLabel.RIGHT);
            destra.add(lblPrezzo);
            destra.add(lblCitta);

            add(lblBandiera, BorderLayout.WEST);
            add(centro, BorderLayout.CENTER);
            add(destra, BorderLayout.EAST);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Ristorante> list, Ristorante r,
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            lblNome.setText(r.getNome());
            lblCucina.setText(r.getTipoCucina());
            lblCitta.setText(r.getCitta());

            int nSimboli = Math.max(1, Math.min(4, Math.round(r.getFasciaPrezzo() / 25.0f)));
            lblPrezzo.setText("€".repeat(nSimboli));

            String fileFlag = StileTheKnife.determinaFileBandiera(r.getNazione());
            ImageIcon icon = StileTheKnife.caricaImmagine(fileFlag, 36, 24);
            if (icon == null && !fileFlag.equals("Globe.png")) {
                icon = StileTheKnife.caricaImmagine("Globe.png", 36, 24);
            }
            if (icon != null) {
                lblBandiera.setIcon(icon);
                lblBandiera.setText("");
            } else {
                lblBandiera.setIcon(null);
                lblBandiera.setText(r.getNazione() != null && r.getNazione().length() >= 2 ? r.getNazione().substring(0, 2).toUpperCase() : "🌐");
            }

            if (isSelected) {
                setBackground(StileTheKnife.VERDE_CHIARO_SFONDO);
                lblNome.setForeground(StileTheKnife.VERDE_THEKNIFE);
                lblCucina.setForeground(StileTheKnife.TESTO_SCURO);
                lblCitta.setForeground(StileTheKnife.VERDE_THEKNIFE);
                lblPrezzo.setForeground(StileTheKnife.VERDE_THEKNIFE);
            } else {
                setBackground(StileTheKnife.BIANCO);
                lblNome.setForeground(StileTheKnife.TESTO_SCURO);
                lblCucina.setForeground(StileTheKnife.TESTO_GRIGIO);
                lblCitta.setForeground(StileTheKnife.TESTO_GRIGIO);
                lblPrezzo.setForeground(StileTheKnife.VERDE_THEKNIFE);
            }
            return this;
        }
    }

    private JPanel creaPannelloPreferiti() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBackground(StileTheKnife.BIANCO);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 4, 4, StileTheKnife.VERDE_THEKNIFE),
                BorderFactory.createEmptyBorder(18, 22, 18, 22)
        ));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JLabel lblTitolo = new JLabel("★ I Miei Ristoranti Preferiti");
        lblTitolo.setFont(StileTheKnife.FONT_TITOLO);
        lblTitolo.setForeground(StileTheKnife.VERDE_THEKNIFE);
        JButton btnTorna = StileTheKnife.creaBottoneSecondario("← Torna all'Esplorazione");
        btnTorna.addActionListener(e -> cardLayoutCentrale.show(panelContenitoreViste, "ESPLORA"));
        top.add(lblTitolo, BorderLayout.WEST);
        top.add(btnTorna, BorderLayout.EAST);
        panel.add(top, BorderLayout.NORTH);

        String[] colonne = {"Nome Ristorante", "Città", "Nazione", "Tipo di Cucina", "Fascia di Prezzo"};
        modelloPreferiti = new DefaultTableModel(colonne, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabellaPreferiti = new JTable(modelloPreferiti);
        tabellaPreferiti.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        StileTheKnife.applicaStileTabella(tabellaPreferiti);

        panel.add(new JScrollPane(tabellaPreferiti), BorderLayout.CENTER);

        JPanel panelBottoni = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 5));
        panelBottoni.setOpaque(false);

        JButton btnVediScheda = StileTheKnife.creaBottoneSecondario("Apri Scheda Ristorante");
        JButton btnRimuovi = StileTheKnife.creaBottonePrimario("Rimuovi dai Preferiti");

        btnVediScheda.addActionListener(e -> {
            int riga = tabellaPreferiti.getSelectedRow();
            if (riga >= 0 && riga < preferitiCorrenti.size()) {
                cardLayoutCentrale.show(panelContenitoreViste, "ESPLORA");
                mostraDettagliRistoranteADestra(preferitiCorrenti.get(riga));
            }
        });

        btnRimuovi.addActionListener(e -> {
            int riga = tabellaPreferiti.getSelectedRow();
            if (riga < 0) {
                JOptionPane.showMessageDialog(this, "Seleziona prima un ristorante dalla lista.");
                return;
            }
            Ristorante r = preferitiCorrenti.get(riga);
            if (controller.rimuoviPreferito(r.getIdRistorante())) {
                aggiornaTabellaPreferiti();
            }
        });

        panelBottoni.add(btnVediScheda);
        panelBottoni.add(btnRimuovi);
        panel.add(panelBottoni, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel creaPannelloMieRecensioni() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBackground(StileTheKnife.BIANCO);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 4, 4, StileTheKnife.VERDE_THEKNIFE),
                BorderFactory.createEmptyBorder(18, 22, 18, 22)
        ));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JLabel lblTitolo = new JLabel("✍ Le Mie Recensioni");
        lblTitolo.setFont(StileTheKnife.FONT_TITOLO);
        lblTitolo.setForeground(StileTheKnife.VERDE_THEKNIFE);
        JButton btnTorna = StileTheKnife.creaBottoneSecondario("← Torna all'Esplorazione");
        btnTorna.addActionListener(e -> cardLayoutCentrale.show(panelContenitoreViste, "ESPLORA"));
        top.add(lblTitolo, BorderLayout.WEST);
        top.add(btnTorna, BorderLayout.EAST);
        panel.add(top, BorderLayout.NORTH);

        String[] colonne = {"Ristorante", "Città", "Voto (Stelle)", "Il mio commento", "Data", "Risposta del Ristoratore"};
        modelloMieRecensioni = new DefaultTableModel(colonne, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabellaMieRecensioni = new JTable(modelloMieRecensioni);
        tabellaMieRecensioni.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        StileTheKnife.applicaStileTabella(tabellaMieRecensioni);

        panel.add(new JScrollPane(tabellaMieRecensioni), BorderLayout.CENTER);

        JPanel panelBottoni = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 5));
        panelBottoni.setOpaque(false);

        JButton btnModifica = StileTheKnife.creaBottoneSecondario("Modifica Recensione");
        JButton btnElimina = StileTheKnife.creaBottonePrimario("Elimina Recensione");

        btnModifica.addActionListener(e -> modificaRecensioneSelezionata());
        btnElimina.addActionListener(e -> eliminaRecensioneSelezionata());

        panelBottoni.add(btnModifica);
        panelBottoni.add(btnElimina);
        panel.add(panelBottoni, BorderLayout.SOUTH);

        return panel;
    }

    private void eseguiRicercaIniziale() {
        ristorantiCorrenti = controller.getRistorantiVicini();
        popolaListaCard(ristorantiCorrenti);
    }

    private void eseguiRicercaFiltrata() {
        String nome = txtBarraRicercaNome.getText().trim();
        String locazione = txtLocazione.getText().trim();
        String cucina = txtCucina.getText().trim();

        Integer prezzoMin = null;
        Integer prezzoMax = null;
        try {
            if (!txtPrezzoMin.getText().trim().isEmpty()) {
                prezzoMin = Integer.parseInt(txtPrezzoMin.getText().trim());
            }
            if (!txtPrezzoMax.getText().trim().isEmpty()) {
                prezzoMax = Integer.parseInt(txtPrezzoMax.getText().trim());
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Inserisci solo numeri interi nei campi Prezzo.", "Prezzo non valido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Boolean delivery = chkDelivery.isSelected() ? true : null;
        Boolean prenotazione = chkPrenotazione.isSelected() ? true : null;

        Double stelleMin = null;
        int idxStelle = cmbStelleMin.getSelectedIndex();
        if (idxStelle > 0) {
            stelleMin = (double) idxStelle;
        }

        ristorantiCorrenti = controller.cercaRistorante(nome, locazione, cucina, prezzoMin, prezzoMax, delivery, prenotazione, stelleMin);
        popolaListaCard(ristorantiCorrenti);
    }

    private void resettaFiltri() {
        txtBarraRicercaNome.setText("");
        txtLocazione.setText(controller.getSessione().getLuogoRiferimento());
        txtCucina.setText("");
        txtPrezzoMin.setText("");
        txtPrezzoMax.setText("");
        chkDelivery.setSelected(false);
        chkPrenotazione.setSelected(false);
        cmbStelleMin.setSelectedIndex(0);
        eseguiRicercaIniziale();
    }

    private void popolaListaCard(List<Ristorante> lista) {
        modelloListaCard.clear();
        for (Ristorante r : lista) {
            modelloListaCard.addElement(r);
        }
        lblContatoreRisultati.setText("Ristoranti trovati: " + lista.size());

        if (!lista.isEmpty()) {
            listaCardRistoranti.setSelectedIndex(0);
        }
    }

    private void aggiornaTabellaPreferiti() {
        preferitiCorrenti = controller.visualizzaPreferiti();
        modelloPreferiti.setRowCount(0);
        for (Ristorante r : preferitiCorrenti) {
            modelloPreferiti.addRow(new Object[]{
                    r.getNome(), r.getCitta(), r.getNazione(), r.getTipoCucina(), StileTheKnife.formattaFasciaPrezzo(r.getFasciaPrezzo())
            });
        }
    }

    private void aggiornaTabellaMieRecensioni() {
        mieRecensioniCorrenti = controller.visualizzaMieRecensioniCliente();
        modelloMieRecensioni.setRowCount(0);
        for (Recensione rec : mieRecensioniCorrenti) {
            Ristorante r = controller.visualizzaRistorante(rec.getIdRistorante());
            String nomeRist = (r != null) ? r.getNome() : "Ristorante #" + rec.getIdRistorante();
            String cittaRist = (r != null) ? r.getCitta() : "-";
            String risp = (rec.getRispostaRistoratore() != null) ? rec.getRispostaRistoratore() : "In attesa di risposta";

            modelloMieRecensioni.addRow(new Object[]{
                    nomeRist, cittaRist, rec.getStelle() + " / 5 ★", rec.getTesto(), rec.getDataRecensione(), risp
            });
        }
    }

    /**
     * Modifica la recensione usando il nuovo dialogo stilizzato con dettagli in verde.
     */
    private void modificaRecensioneSelezionata() {
        int riga = tabellaMieRecensioni.getSelectedRow();
        if (riga < 0) {
            JOptionPane.showMessageDialog(this, "Seleziona prima una recensione da modificare.");
            return;
        }
        Recensione rec = mieRecensioniCorrenti.get(riga);
        String testoPulito = rec.getTesto().replace(" [MODIFICATA DAL CLIENTE]", "");

        JComboBox<Integer> cmbStelle = new JComboBox<>(new Integer[]{5, 4, 3, 2, 1});
        cmbStelle.setSelectedItem(rec.getStelle());
        cmbStelle.setFont(StileTheKnife.FONT_NORMALE);

        JTextArea txtNuovoTesto = new JTextArea(testoPulito, 5, 28);
        txtNuovoTesto.setFont(StileTheKnife.FONT_NORMALE);
        txtNuovoTesto.setLineWrap(true);
        txtNuovoTesto.setWrapStyleWord(true);
        txtNuovoTesto.setBorder(StileTheKnife.creaBordoInput());

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        top.setOpaque(false);
        JLabel lblVoto = StileTheKnife.creaEtichetta("Nuova valutazione in stelle:");
        lblVoto.setForeground(StileTheKnife.VERDE_THEKNIFE);
        lblVoto.setFont(StileTheKnife.FONT_BOTTONE);
        top.add(lblVoto);
        top.add(cmbStelle);

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(txtNuovoTesto), BorderLayout.CENTER);

        if (StileTheKnife.mostraDialogoStilizzato(this, "Modifica la tua Recensione", panel, "Salva Modifiche", 480, 340)) {
            String nuovoTesto = txtNuovoTesto.getText().trim();
            if (!nuovoTesto.isEmpty() && controller.modificaRecensione(rec.getIdRecensione(), (Integer) cmbStelle.getSelectedItem(), nuovoTesto)) {
                ultimaVersioneNota = controller.getVersioneDatiServer();
                aggiornaTabellaMieRecensioni();
                if (ristoranteSelezionato != null && ristoranteSelezionato.getIdRistorante() == rec.getIdRistorante()) {
                    mostraDettagliRistoranteADestra(ristoranteSelezionato);
                }
            }
        }
    }

    private void eliminaRecensioneSelezionata() {
        int riga = tabellaMieRecensioni.getSelectedRow();
        if (riga < 0) {
            JOptionPane.showMessageDialog(this, "Seleziona prima una recensione da eliminare.");
            return;
        }
        Recensione rec = mieRecensioniCorrenti.get(riga);
        int conferma = JOptionPane.showConfirmDialog(this, "Sei sicuro di voler eliminare questa recensione?", "Conferma Eliminazione", JOptionPane.YES_NO_OPTION);
        if (conferma == JOptionPane.YES_OPTION && controller.eliminaRecensione(rec.getIdRecensione())) {
            ultimaVersioneNota = controller.getVersioneDatiServer();
            aggiornaTabellaMieRecensioni();
            if (ristoranteSelezionato != null && ristoranteSelezionato.getIdRistorante() == rec.getIdRistorante()) {
                mostraDettagliRistoranteADestra(ristoranteSelezionato);
            }
        }
    }
}