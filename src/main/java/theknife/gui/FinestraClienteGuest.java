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
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * Finestra principale per Clienti e Guest con divisore verde continuo, blocchi ridimensionabili
 * anche a finestra piccola e menù a tendina del profilo utente in alto a destra.
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

    // Navigazione tramite CardLayout comandata dal menù a tendina in alto a destra
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

    // Colonna destra
    private JLabel lblDettaglioNome;
    private JLabel lblDettaglioCucina;
    private JLabel lblDettaglioPrezzo;
    private JLabel lblDettaglioIndirizzo;
    private JLabel lblDettaglioCoordinate;
    private JLabel lblBadgeDelivery;
    private JLabel lblBadgePrenotazione;
    private JLabel lblValutazioneMedia;
    private JPanel panelContenitoreRecensioni;
    private JButton btnAggiungiPreferito;
    private JButton btnScriviRecensione;

    // Viste Preferiti e Mie Recensioni
    private DefaultTableModel modelloPreferiti;
    private JTable tabellaPreferiti;
    private DefaultTableModel modelloMieRecensioni;
    private JTable tabellaMieRecensioni;

    /**
     * Costruttore della finestra Cliente/Guest.
     * @param controller il controller attivo del client
     */
    public FinestraClienteGuest(ControllerTheKnife controller) {
        super("TheKnife - Scopri i Ristoranti");
        this.controller = controller;
        inizializzaGUI();
        eseguiRicercaIniziale();
    }

    /**
     * Costruisce l'interfaccia con barra superiore, logo, menù a tendina profilo e divisore verde continuo.
     */
    private void inizializzaGUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 740);
        setMinimumSize(new Dimension(680, 480));
        setLocationRelativeTo(null);
        setResizable(true);
        getContentPane().setBackground(StileTheKnife.VERDE_THEKNIFE);
        setLayout(new BorderLayout());

        Utente utente = controller.getSessione().getUtenteCorrente();

        // 1. TOP BAR CON LOGO A SINISTRA E MENÙ A TENDINA PROFILO A DESTRA
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(StileTheKnife.VERDE_THEKNIFE);
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));

        JPanel boxSinistroTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        boxSinistroTop.setOpaque(false);
        boxSinistroTop.add(StileTheKnife.creaLabelLogo(42));

        lblStatoHeader = new JLabel();
        lblStatoHeader.setFont(StileTheKnife.FONT_SOTTOTITOLO);
        lblStatoHeader.setForeground(StileTheKnife.BIANCO);
        aggiornaTestoHeader();
        boxSinistroTop.add(lblStatoHeader);

        topBar.add(boxSinistroTop, BorderLayout.WEST);

        // Pulsante Profilo / Menù a tendina in alto a destra
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
                controller.logout();
                dispose();
                new FinestraIniziale(controller).setVisible(true);
            });
            boxDestroTop.add(btnAccedi);
        }

        topBar.add(boxDestroTop, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // 2. CONTENITORE VISTE (ESPLORAZIONE, PREFERITI, MIE RECENSIONI)
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
     * Aggiorna l'intestazione con il nome e il domicilio correnti dell'utente.
     */
    private void aggiornaTestoHeader() {
        Utente utente = controller.getSessione().getUtenteCorrente();
        String testo = utente.isGuest()
                ? "THE KNIFE   —   Ospite (Guest)  |  Luogo: " + utente.getLuogoDomicilio()
                : "THE KNIFE   —   " + utente.getNome() + " " + utente.getCognome() + " (@" + utente.getUsername() + ")  |  " + utente.getLuogoDomicilio();
        lblStatoHeader.setText(testo);
    }

    /**
     * Crea il menù a tendina per accedere a Esplora, Preferiti, Mie Recensioni, Modifica Credenziali e Logout.
     * @return il componente <code>JPopupMenu</code>
     */
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

    /**
     * Crea lo Split-View con la linea verde <code>VERDE_THEKNIFE</code> che continua fino in fondo
     * e permette il ridimensionamento libero anche quando la finestra è piccola.
     * @return lo <code>JSplitPane</code> configurato
     */
    private JSplitPane creaSplitEsplorazione() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, creaColonnaSinistraLista(), creaColonnaDestraDettaglio());
        split.setDividerLocation(410);
        split.setResizeWeight(0.35);
        split.setContinuousLayout(true);
        StileTheKnife.applicaSplitVerdeContinuo(split);
        return split;
    }

    /**
     * Costruisce la colonna di sinistra lasciando una dimensione minima piccola (200px)
     * così l'utente può sempre trascinare il divisore anche a finestra rimpicciolita.
     * @return il pannello <code>JPanel</code> sinistro
     */
    private JPanel creaColonnaSinistraLista() {
        JPanel panelSinistro = new JPanel(new BorderLayout(0, 0));
        panelSinistro.setBackground(StileTheKnife.BIANCO);
        panelSinistro.setMinimumSize(new Dimension(200, 200));

        JPanel boxRicercaTop = new JPanel();
        boxRicercaTop.setLayout(new BoxLayout(boxRicercaTop, BoxLayout.Y_AXIS));
        boxRicercaTop.setBackground(StileTheKnife.GRIGIO_SFONDO);
        boxRicercaTop.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, StileTheKnife.VERDE_THEKNIFE),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        JPanel rigaBarra = new JPanel(new BorderLayout(6, 0));
        rigaBarra.setOpaque(false);

        txtBarraRicercaNome = StileTheKnife.creaCampoTesto(12);
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

        JPanel barraInfoLista = new JPanel(new BorderLayout());
        barraInfoLista.setOpaque(false);
        barraInfoLista.setBorder(BorderFactory.createEmptyBorder(6, 2, 0, 2));
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
                    // Rilegge dal server i dati freschi (incluso delivery/prenotazione)
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
     * Costruisce la colonna di destra senza il bug del badge tagliato (linea azzurra).
     * @return il pannello <code>JPanel</code> destro
     */
    private JPanel creaColonnaDestraDettaglio() {
        JPanel panelDestro = new JPanel(new BorderLayout(0, 12));
        panelDestro.setBackground(StileTheKnife.BIANCO);
        panelDestro.setMinimumSize(new Dimension(260, 200));
        panelDestro.setBorder(BorderFactory.createEmptyBorder(22, 28, 22, 28));

        JPanel panelInfoSuperiore = new JPanel();
        panelInfoSuperiore.setLayout(new BoxLayout(panelInfoSuperiore, BoxLayout.Y_AXIS));
        panelInfoSuperiore.setBackground(StileTheKnife.BIANCO);

        JPanel rigaTitoloPrezzo = new JPanel(new BorderLayout(10, 0));
        rigaTitoloPrezzo.setBackground(StileTheKnife.BIANCO);
        rigaTitoloPrezzo.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblDettaglioNome = new JLabel("Seleziona un ristorante");
        lblDettaglioNome.setFont(new Font("SansSerif", Font.BOLD, 26));
        lblDettaglioNome.setForeground(StileTheKnife.TESTO_SCURO);

        lblDettaglioPrezzo = new JLabel("");
        lblDettaglioPrezzo.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblDettaglioPrezzo.setForeground(StileTheKnife.VERDE_THEKNIFE);

        rigaTitoloPrezzo.add(lblDettaglioNome, BorderLayout.CENTER);
        rigaTitoloPrezzo.add(lblDettaglioPrezzo, BorderLayout.EAST);

        lblDettaglioCucina = new JLabel("Clicca su un locale a sinistra per vedere i dettagli");
        lblDettaglioCucina.setFont(new Font("SansSerif", Font.PLAIN, 16));
        lblDettaglioCucina.setForeground(StileTheKnife.VERDE_THEKNIFE);
        lblDettaglioCucina.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblDettaglioIndirizzo = StileTheKnife.creaEtichetta("");
        lblDettaglioIndirizzo.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblDettaglioCoordinate = StileTheKnife.creaEtichetta("");
        lblDettaglioCoordinate.setForeground(StileTheKnife.TESTO_GRIGIO);
        lblDettaglioCoordinate.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Separazione su due righe distinte per evitare che il badge Prenotazione vada a capo tagliato (linea azzurra)
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

        JPanel rigaAzioniCliente = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        rigaAzioniCliente.setBackground(StileTheKnife.BIANCO);
        rigaAzioniCliente.setAlignmentX(Component.LEFT_ALIGNMENT);

        Utente u = controller.getSessione().getUtenteCorrente();
        if (u != null && u.isCliente()) {
            btnAggiungiPreferito = StileTheKnife.creaBottoneSecondario("★ Aggiungi ai Preferiti");
            btnScriviRecensione = StileTheKnife.creaBottonePrimario("✍ Scrivi una Recensione");

            btnAggiungiPreferito.setEnabled(false);
            btnScriviRecensione.setEnabled(false);

            btnAggiungiPreferito.addActionListener(e -> {
                if (ristoranteSelezionato != null) {
                    if (controller.aggiungiPreferito(ristoranteSelezionato.getIdRistorante())) {
                        JOptionPane.showMessageDialog(this, "Aggiunto ai tuoi ristoranti preferiti!");
                    } else {
                        JOptionPane.showMessageDialog(this, "Questo ristorante è già nei tuoi preferiti.");
                    }
                }
            });

            btnScriviRecensione.addActionListener(e -> apriDialogNuovaRecensione());

            rigaAzioniCliente.add(btnScriviRecensione);
            rigaAzioniCliente.add(btnAggiungiPreferito);
        } else {
            JLabel lblAvvisoGuest = StileTheKnife.creaEtichetta("Modalità Guest: effettua il login come Cliente per salvare nei preferiti o scrivere recensioni.");
            lblAvvisoGuest.setForeground(StileTheKnife.TESTO_GRIGIO);
            rigaAzioniCliente.add(lblAvvisoGuest);
        }

        panelInfoSuperiore.add(rigaTitoloPrezzo);
        panelInfoSuperiore.add(Box.createVerticalStrut(4));
        panelInfoSuperiore.add(lblDettaglioCucina);
        panelInfoSuperiore.add(Box.createVerticalStrut(12));
        panelInfoSuperiore.add(lblDettaglioIndirizzo);
        panelInfoSuperiore.add(Box.createVerticalStrut(4));
        panelInfoSuperiore.add(lblDettaglioCoordinate);
        panelInfoSuperiore.add(Box.createVerticalStrut(10));
        panelInfoSuperiore.add(rigaValutazione);
        panelInfoSuperiore.add(rigaAzioniCliente);

        panelDestro.add(panelInfoSuperiore, BorderLayout.NORTH);

        JPanel wrapperRecensioni = new JPanel(new BorderLayout(0, 8));
        wrapperRecensioni.setBackground(StileTheKnife.BIANCO);
        wrapperRecensioni.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, StileTheKnife.GRIGIO_BORDO));

        JLabel lblTitoloRec = new JLabel("Recensioni dei Clienti:");
        lblTitoloRec.setFont(StileTheKnife.FONT_SOTTOTITOLO);
        lblTitoloRec.setForeground(StileTheKnife.VERDE_THEKNIFE);
        lblTitoloRec.setBorder(BorderFactory.createEmptyBorder(12, 0, 4, 0));
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
     * Mostra i dati completi del ristorante selezionato e le sue recensioni.
     * @param r il <code>Ristorante</code> da mostrare
     */
    private void mostraDettagliRistoranteADestra(Ristorante r) {
        this.ristoranteSelezionato = r;

        lblDettaglioNome.setText(r.getNome());
        lblDettaglioCucina.setText(r.getTipoCucina());
        lblDettaglioPrezzo.setText("Fascia di prezzo: " + StileTheKnife.formattaFasciaPrezzo(r.getFasciaPrezzo()));
        lblDettaglioIndirizzo.setText("Indirizzo: " + r.getIndirizzo() + " — " + r.getCitta() + ", " + r.getNazione());
        lblDettaglioCoordinate.setText("Coordinate GPS: Latitudine " + r.getLatitudine() + "  |  Longitudine " + r.getLongitudine());

        lblBadgeDelivery.setText("Delivery: " + (r.isDelivery() ? "Disponibile" : "Non disponibile"));
        lblBadgePrenotazione.setText("Prenotazione Online: " + (r.isPrenotazioneOnline() ? "Disponibile" : "Non disponibile"));

        if (btnAggiungiPreferito != null && btnScriviRecensione != null) {
            btnAggiungiPreferito.setEnabled(true);
            btnScriviRecensione.setEnabled(true);
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

                JLabel lblTestoRec = new JLabel("<html><div style='width:420px;'>" + rec.getTesto() + "</div></html>");
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
     * Renderer pulito per gli elementi della lista ristoranti a sinistra.
     */
    private static class RistoranteCardRenderer extends JPanel implements ListCellRenderer<Ristorante> {
        private final JLabel lblNome = new JLabel();
        private final JLabel lblCucina = new JLabel();
        private final JLabel lblCitta = new JLabel();
        private final JLabel lblPrezzo = new JLabel();

        public RistoranteCardRenderer() {
            setLayout(new BorderLayout(8, 4));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, StileTheKnife.GRIGIO_BORDO),
                    BorderFactory.createEmptyBorder(12, 14, 12, 14)
            ));

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

            add(centro, BorderLayout.CENTER);
            add(destra, BorderLayout.EAST);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Ristorante> list, Ristorante r,
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            lblNome.setText(r.getNome());
            lblCucina.setText(r.getTipoCucina());
            lblCitta.setText(r.getCitta() + " (" + r.getNazione() + ")");

            int nSimboli = Math.max(1, Math.min(4, Math.round(r.getFasciaPrezzo() / 25.0f)));
            lblPrezzo.setText("€".repeat(nSimboli));

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

    private void apriDialogNuovaRecensione() {
        if (ristoranteSelezionato == null) {
            return;
        }
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
                "Scrivi una Recensione per " + ristoranteSelezionato.getNome(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (scelta == JOptionPane.OK_OPTION) {
            String testo = txtCommento.getText().trim();
            int stelle = (Integer) cmbStelle.getSelectedItem();

            if (testo.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Inserisci un commento testuale.", "Campo Vuoto", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (controller.aggiungiRecensione(ristoranteSelezionato.getIdRistorante(), stelle, testo)) {
                mostraDettagliRistoranteADestra(ristoranteSelezionato);
                JOptionPane.showMessageDialog(this, "Recensione pubblicata con successo!");
            } else {
                JOptionPane.showMessageDialog(this,
                        "Hai già recensito questo ristorante.\nPuoi modificarla dal menù in alto a destra -> 'Le Mie Recensioni'.",
                        "Recensione Già Presente", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Crea la vista dei ristoranti preferiti (accessibile dal menù a tendina in alto a destra).
     * @return il pannello <code>JPanel</code>
     */
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
                JOptionPane.showMessageDialog(this, "Ristorante rimosso dai preferiti.");
            }
        });

        panelBottoni.add(btnVediScheda);
        panelBottoni.add(btnRimuovi);
        panel.add(panelBottoni, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Crea la vista delle recensioni del cliente (accessibile dal menù a tendina in alto a destra).
     * @return il pannello <code>JPanel</code>
     */
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
        JTextArea txtNuovoTesto = new JTextArea(testoPulito, 5, 30);
        txtNuovoTesto.setLineWrap(true);
        txtNuovoTesto.setWrapStyleWord(true);

        JPanel panel = new JPanel(new BorderLayout(8, 8));
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Nuove stelle:"));
        top.add(cmbStelle);
        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(txtNuovoTesto), BorderLayout.CENTER);

        if (JOptionPane.showConfirmDialog(this, panel, "Modifica la tua Recensione", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            String nuovoTesto = txtNuovoTesto.getText().trim();
            if (!nuovoTesto.isEmpty() && controller.modificaRecensione(rec.getIdRecensione(), (Integer) cmbStelle.getSelectedItem(), nuovoTesto)) {
                aggiornaTabellaMieRecensioni();
                if (ristoranteSelezionato != null && ristoranteSelezionato.getIdRistorante() == rec.getIdRistorante()) {
                    mostraDettagliRistoranteADestra(ristoranteSelezionato);
                }
                JOptionPane.showMessageDialog(this, "Recensione aggiornata! Il ristoratore verrà avvisato della modifica.");
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
            aggiornaTabellaMieRecensioni();
            if (ristoranteSelezionato != null && ristoranteSelezionato.getIdRistorante() == rec.getIdRistorante()) {
                mostraDettagliRistoranteADestra(ristoranteSelezionato);
            }
            JOptionPane.showMessageDialog(this, "Recensione eliminata.");
        }
    }
}