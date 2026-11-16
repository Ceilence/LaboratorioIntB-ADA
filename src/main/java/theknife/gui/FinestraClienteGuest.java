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
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
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
 * Finestra principale dell'applicazione per gli utenti con ruolo cliente e per gli ospiti.
 * <p>
 * Permette di cercare e filtrare i ristoranti con paginazione, visualizzare i dettagli e le recensioni,
 * gestire la lista dei preferiti e inserire, modificare o eliminare le proprie recensioni.
 * @author Alessandro Frigerio
 * @author Antonio Pardo
 * @author Davide Moretti
 */
public class FinestraClienteGuest extends JFrame {

    /**
     * Numero massimo di schede ristorante mostrate in ogni pagina <code>ELEMENTI_PER_PAGINA</code>.
     */
    private static final int ELEMENTI_PER_PAGINA = 20;

    /**
     * Controller che gestisce la logica e la comunicazione col server <code>controller</code>.
     */
    private final ControllerTheKnife controller;

    /**
     * Lista completa dei ristoranti trovati con l'ultima ricerca <code>ristorantiTutti</code>.
     */
    private List<Ristorante> ristorantiTutti = new ArrayList<>();

    /**
     * Lista dei ristoranti preferiti salvati dal cliente loggato <code>preferitiCorrenti</code>.
     */
    private List<Ristorante> preferitiCorrenti = new ArrayList<>();

    /**
     * Lista delle recensioni scritte dal cliente loggato <code>mieRecensioniCorrenti</code>.
     */
    private List<Recensione> mieRecensioniCorrenti = new ArrayList<>();

    /**
     * Ristorante attualmente aperto nel pannello dei dettagli a destra <code>ristoranteSelezionato</code>.
     */
    private Ristorante ristoranteSelezionato = null;

    /**
     * Indice della pagina corrente nella lista dei ristoranti <code>paginaCorrente</code>.
     */
    private int paginaCorrente = 1;

    /**
     * Numero totale delle pagine disponibili per la ricerca corrente <code>totalePagine</code>.
     */
    private int totalePagine = 1;

    /**
     * Pulsante per tornare alla pagina precedente dei risultati <code>btnPaginaIndietro</code>.
     */
    private JButton btnPaginaIndietro;

    /**
     * Pulsante per passare alla pagina successiva dei risultati <code>btnPaginaAvanti</code>.
     */
    private JButton btnPaginaAvanti;

    /**
     * Etichetta che mostra il numero di pagina attuale sul totale <code>lblInfoPagina</code>.
     */
    private JLabel lblInfoPagina;

    /**
     * Timer per aggiornare automaticamente la schermata quando cambiano i dati sul database <code>timerSyncRealTime</code>.
     */
    private Timer timerSyncRealTime;

    /**
     * Stringa di controllo per verificare se le recensioni sono state modificate da altri client <code>ultimaImprontaRecensioni</code>.
     */
    private String ultimaImprontaRecensioni = "";

    /**
     * Layout per passare tra la vista esplorazione, i preferiti e le recensioni del cliente <code>cardLayoutCentrale</code>.
     */
    private CardLayout cardLayoutCentrale;

    /**
     * Pannello principale che contiene le diverse schermate gestite dal <code>CardLayout</code> <code>panelContenitoreViste</code>.
     */
    private JPanel panelContenitoreViste;

    /**
     * Etichetta nella barra superiore con i dati dell'utente attivo e il domicilio <code>lblStatoHeader</code>.
     */
    private JLabel lblStatoHeader;

    /**
     * Campo di testo per cercare i ristoranti per nome <code>txtBarraRicercaNome</code>.
     */
    private JTextField txtBarraRicercaNome;

    /**
     * Pannello a scomparsa che contiene i filtri di ricerca avanzati <code>panelFiltriAvanzati</code>.
     */
    private JPanel panelFiltriAvanzati;

    /**
     * Campo di testo per filtrare i ristoranti in base alla città o nazione <code>txtLocazione</code>.
     */
    private JTextField txtLocazione;

    /**
     * Campo di testo per filtrare i ristoranti in base al tipo di cucina <code>txtCucina</code>.
     */
    private JTextField txtCucina;

    /**
     * Campo di testo per inserire il prezzo minimo del filtro <code>txtPrezzoMin</code>.
     */
    private JTextField txtPrezzoMin;

    /**
     * Campo di testo per inserire il prezzo massimo del filtro <code>txtPrezzoMax</code>.
     */
    private JTextField txtPrezzoMax;

    /**
     * Casella di spunta per filtrare i ristoranti con consegna a domicilio <code>chkDelivery</code>.
     */
    private JCheckBox chkDelivery;

    /**
     * Casella di spunta per filtrare i ristoranti con prenotazione online <code>chkPrenotazione</code>.
     */
    private JCheckBox chkPrenotazione;

    /**
     * Menu a tendina per selezionare la media minima di stelle <code>cmbStelleMin</code>.
     */
    private JComboBox<String> cmbStelleMin;

    /**
     * Modello dati per la lista grafica dei ristoranti a sinistra <code>modelloListaCard</code>.
     */
    private DefaultListModel<Ristorante> modelloListaCard;

    /**
     * Componente grafico che mostra le schede dei ristoranti nella colonna di sinistra <code>listaCardRistoranti</code>.
     */
    private JList<Ristorante> listaCardRistoranti;

    /**
     * Etichetta che indica il numero totale di ristoranti trovati <code>lblContatoreRisultati</code>.
     */
    private JLabel lblContatoreRisultati;

    /**
     * Pannello che mostra l'icona della bandiera nella scheda di dettaglio del ristorante <code>panelBandieraDettaglio</code>.
     */
    private JPanel panelBandieraDettaglio;

    /**
     * Etichetta con il nome del ristorante selezionato <code>lblDettaglioNome</code>.
     */
    private JLabel lblDettaglioNome;

    /**
     * Etichetta con il tipo di cucina del ristorante selezionato <code>lblDettaglioCucina</code>.
     */
    private JLabel lblDettaglioCucina;

    /**
     * Etichetta con la fascia di prezzo del ristorante selezionato <code>lblDettaglioPrezzo</code>.
     */
    private JLabel lblDettaglioPrezzo;

    /**
     * Etichetta con l'indirizzo e le coordinate GPS del ristorante selezionato <code>lblDettaglioIndirizzo</code>.
     */
    private JLabel lblDettaglioIndirizzo;

    /**
     * Area di testo non modificabile con la descrizione del ristorante <code>txtDettaglioDescrizione</code>.
     */
    private JTextArea txtDettaglioDescrizione;

    /**
     * Etichetta che indica se il servizio di delivery è disponibile <code>lblBadgeDelivery</code>.
     */
    private JLabel lblBadgeDelivery;

    /**
     * Etichetta che indica se la prenotazione online è disponibile <code>lblBadgePrenotazione</code>.
     */
    private JLabel lblBadgePrenotazione;

    /**
     * Etichetta che mostra la media delle stelle e il numero totale di recensioni <code>lblValutazioneMedia</code>.
     */
    private JLabel lblValutazioneMedia;

    /**
     * Pulsante per salvare il ristorante selezionato nella lista dei preferiti <code>btnAggiungiPreferito</code>.
     */
    private JButton btnAggiungiPreferito;

    /**
     * Numero di stelle attualmente selezionate dall'utente per la nuova recensione <code>stelleSelezionateInline</code>.
     */
    private int stelleSelezionateInline = 0;

    /**
     * Array di etichette cliccabili che rappresentano le 5 stelle di voto <code>lblStelleInterattive</code>.
     */
    private final JLabel[] lblStelleInterattive = new JLabel[5];

    /**
     * Etichetta che mostra in tempo reale il voto in stelle selezionato <code>lblTestoVotoScelto</code>.
     */
    private JLabel lblTestoVotoScelto;

    /**
     * Area di testo integrata nella pagina per scrivere una nuova recensione <code>txtNuovaRecensioneInline</code>.
     */
    private JTextArea txtNuovaRecensioneInline;

    /**
     * Pulsante per inviare e pubblicare la nuova recensione <code>btnPubblicaRecensioneInline</code>.
     */
    private JButton btnPubblicaRecensioneInline;

    /**
     * Pannello verticale che raccoglie tutte le schede delle recensioni del ristorante <code>panelContenitoreRecensioni</code>.
     */
    private JPanel panelContenitoreRecensioni;

    /**
     * Modello dati per la tabella dei ristoranti preferiti <code>modelloPreferiti</code>.
     */
    private DefaultTableModel modelloPreferiti;

    /**
     * Tabella che mostra l'elenco dei ristoranti preferiti del cliente <code>tabellaPreferiti</code>.
     */
    private JTable tabellaPreferiti;

    /**
     * Modello dati per la tabella delle recensioni scritte dal cliente <code>modelloMieRecensioni</code>.
     */
    private DefaultTableModel modelloMieRecensioni;

    /**
     * Tabella che mostra il riepilogo di tutte le recensioni scritte dal cliente <code>tabellaMieRecensioni</code>.
     */
    private JTable tabellaMieRecensioni;

    /**
     * Flag che indica se il cliente ha aperto la modifica rapida della propria recensione <code>inModificaInline</code>.
     */
    private boolean inModificaInline = false;

    /**
     * Costruttore della classe <code>FinestraClienteGuest</code>.
     * <p>
     * Inizializza l'interfaccia grafica, carica i ristoranti vicini all'utente e avvia il timer di sincronizzazione.
     * @param controller il controller attivo del client per comunicare con il server
     */
    public FinestraClienteGuest(ControllerTheKnife controller) {
        super("TheKnife");
        this.controller = controller;
        inizializzaGUI();
        eseguiRicercaIniziale();
        avviaSincronizzazioneRealTime();
    }

    /**
     * Costruisce e posiziona tutti gli elementi grafici della finestra principale.
     */
    private void inizializzaGUI() {
        StileTheKnife.applicaIconaFinestra(this);
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

        JPanel barraInAlto = new JPanel(new BorderLayout());
        barraInAlto.setBackground(StileTheKnife.VERDE_THEKNIFE);
        barraInAlto.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));

        JPanel boxSinistroTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        boxSinistroTop.setOpaque(false);
        boxSinistroTop.add(StileTheKnife.creaLabelLogo(42));

        JLabel lblBrand = new JLabel("THE KNIFE");
        lblBrand.setFont(StileTheKnife.FONT_BRAND);
        lblBrand.setForeground(StileTheKnife.BIANCO);
        boxSinistroTop.add(lblBrand);

        boxSinistroTop.add(Box.createHorizontalStrut(18));

        lblStatoHeader = new JLabel();
        lblStatoHeader.setFont(StileTheKnife.FONT_SOTTOTITOLO);
        lblStatoHeader.setForeground(StileTheKnife.VERDE_CHIARO_SFONDO);
        aggiornaTestoHeader();
        boxSinistroTop.add(lblStatoHeader);

        barraInAlto.add(boxSinistroTop, BorderLayout.WEST);

        JPanel boxDestroTop = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 4));
        boxDestroTop.setOpaque(false);

        if (utente.isCliente()) {
            JButton btnMenuProfilo = StileTheKnife.creaBottoneSecondario("👤 Il mio account ▾");
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

        barraInAlto.add(boxDestroTop, BorderLayout.EAST);
        add(barraInAlto, BorderLayout.NORTH);

        //Contenitore viste
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
     * Aggiorna il testo dell'intestazione superiore mostrando i dati del cliente o la modalità Ospite.
     *
     */
    private void aggiornaTestoHeader() {
        Utente utente = controller.getSessione().getUtenteCorrente();
        String testo = utente.isGuest()
                ? "Ospite   |   Luogo: " + utente.getLuogoDomicilio()
                : "Cliente: " + utente.getNome() + " " + utente.getCognome() + " (@" + utente.getUsername() + ")   |   Domicilio: " + utente.getLuogoDomicilio();
        lblStatoHeader.setText(testo);
    }

    /**
     * Controlla ogni 2.5 secondi se ci sono novità sul database e aggiorna la vista aperta.
     * <p>
     * Sospende temporaneamente l'aggiornamento grafico se l'utente sta modificando la propria recensione.
     */
    private void avviaSincronizzazioneRealTime() {
        timerSyncRealTime = new Timer(2500, e -> {
            if (ristoranteSelezionato != null && !inModificaInline) {
                List<Recensione> recAttuali = controller.visualizzaRecensioni(ristoranteSelezionato.getIdRistorante());
                String nuovaImpronta = calcolaImprontaRecensioni(recAttuali);
                if (!nuovaImpronta.equals(ultimaImprontaRecensioni)) {
                    Ristorante rFresco = controller.visualizzaRistorante(ristoranteSelezionato.getIdRistorante());
                    aggiornaDatiRistoranteSenzaResetInput(rFresco != null ? rFresco : ristoranteSelezionato, recAttuali);
                    if (controller.getSessione().getUtenteCorrente().isCliente() && modelloMieRecensioni != null) {
                        aggiornaTabellaMieRecensioni();
                    }
                }
            }
        });
        timerSyncRealTime.start();
    }

    /**
     * Genera una stringa che riassume lo stato delle recensioni per capire se ci sono state modifiche.
     * @param lista la lista di oggetti <code>Recensione</code> da controllare
     * @return una stringa con gli ID, i voti, i testi e le risposte concatenati
     */
    private String calcolaImprontaRecensioni(List<Recensione> lista) {
        StringBuilder sb = new StringBuilder();
        for (Recensione r : lista) {
            sb.append(r.getIdRecensione()).append(":").append(r.getStelle()).append(":")
                    .append(r.getTesto()).append(":").append(r.getRispostaRistoratore()).append("|");
        }
        return sb.toString();
    }

    /**
     * Crea il menu a tendina in alto a destra riservato al cliente loggato.
     * @return l'oggetto <code>JPopupMenu</code> con le voci di navigazione del profilo
     */
    private JPopupMenu creaMenuTendinaCliente() {
        JPopupMenu menu = new JPopupMenu();
        menu.setBackground(StileTheKnife.BIANCO);
        menu.setBorder(BorderFactory.createLineBorder(StileTheKnife.VERDE_THEKNIFE, 2));

        JMenuItem itemEsplora = creaVoceMenu("🔍  Esplora ristoranti");
        JMenuItem itemPreferiti = creaVoceMenu("★  I miei ristoranti preferiti");
        JMenuItem itemRecensioni = creaVoceMenu("✍  Le mie recensioni");
        JMenuItem itemProfilo = creaVoceMenu("⚙  Modifica profilo e credenziali");
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
        itemProfilo.addActionListener(e -> new FinestraModificaProfilo(this, controller, this::aggiornaTestoHeader).setVisible(true));
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

    /**
     * Crea e formatta una singola voce del menu a tendina dell'account.
     * @param testo il testo da mostrare nella voce del menu
     * @return il componente <code>JMenuItem</code> formattato
     */
    private JMenuItem creaVoceMenu(String testo) {
        JMenuItem item = new JMenuItem(testo);
        item.setFont(StileTheKnife.FONT_NORMALE);
        item.setBackground(StileTheKnife.BIANCO);
        item.setForeground(StileTheKnife.TESTO_SCURO);
        item.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 20));
        return item;
    }

    /**
     * Crea il pannello diviso in due colonne (lista ristoranti a sinistra e dettagli a destra).
     * @return il componente <code>JSplitPane</code> configurato
     */
    private JSplitPane creaSplitEsplorazione() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, creaColonnaSinistraLista(), creaColonnaDestraDettaglio());
        split.setDividerLocation(420);
        split.setResizeWeight(0.32);
        split.setContinuousLayout(true);
        StileTheKnife.applicaSplitVerdeContinuo(split);
        return split;
    }

    /**
     * Costruisce la colonna di sinistra con barra di ricerca, filtri
     * e barra di paginazione in basso.
     * @return il pannello <code>JPanel</code> sinistro
     */
    private JPanel creaColonnaSinistraLista() {
        JPanel panelSinistro = new JPanel(new BorderLayout(0, 0));
        panelSinistro.setBackground(StileTheKnife.BIANCO);
        panelSinistro.setMinimumSize(new Dimension(315, 250));

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

        chkPrenotazione = new JCheckBox("Prenotazione online");
        chkPrenotazione.setOpaque(false);
        chkPrenotazione.setFont(StileTheKnife.FONT_NORMALE);

        cmbStelleMin = new JComboBox<>(new String[]{"Qualsiasi voto", "Almeno 1 ★", "Almeno 2 ★", "Almeno 3 ★", "Almeno 4 ★", "5 ★"});
        cmbStelleMin.setFont(StileTheKnife.FONT_NORMALE);
        cmbStelleMin.setBackground(StileTheKnife.BIANCO);

        JButton btnResetFiltri = StileTheKnife.creaBottoneSecondario("Reset filtri");
        btnResetFiltri.addActionListener(e -> resettaFiltri());

        panelFiltriAvanzati.add(StileTheKnife.creaEtichetta("Città o nazione (esatta):"));
        panelFiltriAvanzati.add(txtLocazione);
        panelFiltriAvanzati.add(StileTheKnife.creaEtichetta("Tipologia cucina:"));
        panelFiltriAvanzati.add(txtCucina);
        panelFiltriAvanzati.add(StileTheKnife.creaEtichetta("Prezzo minimo (€):"));
        panelFiltriAvanzati.add(txtPrezzoMin);
        panelFiltriAvanzati.add(StileTheKnife.creaEtichetta("Prezzo massimo (€):"));
        panelFiltriAvanzati.add(txtPrezzoMax);
        panelFiltriAvanzati.add(StileTheKnife.creaEtichetta("Media stelle:"));
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

        //Lista contenente massimo 20 card per pagina
        modelloListaCard = new DefaultListModel<>();
        listaCardRistoranti = new JList<>(modelloListaCard);
        listaCardRistoranti.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaCardRistoranti.setBackground(StileTheKnife.BIANCO);
        listaCardRistoranti.setCellRenderer(new RistoranteCardRenderer());

        listaCardRistoranti.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Ristorante scelto = listaCardRistoranti.getSelectedValue();
                if (scelto != null) {
                    resettaInputRecensioneInline();
                    Ristorante aggiornato = controller.visualizzaRistorante(scelto.getIdRistorante());
                    mostraDettagliRistoranteADestra(aggiornato != null ? aggiornato : scelto);
                }
            }
        });

        JScrollPane scrollLista = new JScrollPane(listaCardRistoranti);
        scrollLista.setBorder(null);
        scrollLista.getVerticalScrollBar().setUnitIncrement(16);
        panelSinistro.add(scrollLista, BorderLayout.CENTER);

        //Barra di paginazione in basso
        JPanel barraPaginazione = new JPanel(new BorderLayout(10, 0));
        barraPaginazione.setBackground(StileTheKnife.VERDE_THEKNIFE);
        barraPaginazione.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        btnPaginaIndietro = StileTheKnife.creaBottoneSecondario("◀ Indietro");
        btnPaginaIndietro.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));

        lblInfoPagina = new JLabel("1 / 1", SwingConstants.CENTER);
        lblInfoPagina.setFont(StileTheKnife.FONT_BOTTONE);
        lblInfoPagina.setForeground(StileTheKnife.BIANCO);

        btnPaginaAvanti = StileTheKnife.creaBottoneSecondario("Avanti ▶");
        btnPaginaAvanti.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));

        btnPaginaIndietro.addActionListener(e -> {
            if (paginaCorrente > 1) {
                paginaCorrente--;
                mostraPaginaCorrente();
            }
        });

        btnPaginaAvanti.addActionListener(e -> {
            if (paginaCorrente < totalePagine) {
                paginaCorrente++;
                mostraPaginaCorrente();
            }
        });

        barraPaginazione.add(btnPaginaIndietro, BorderLayout.WEST);
        barraPaginazione.add(lblInfoPagina, BorderLayout.CENTER);
        barraPaginazione.add(btnPaginaAvanti, BorderLayout.EAST);

        panelSinistro.add(barraPaginazione, BorderLayout.SOUTH);

        return panelSinistro;
    }

    /**
     * Costruisce la colonna di destra con le informazioni del ristorante, il box per scrivere
     * una nuova recensione e l'elenco delle recensioni dei clienti.
     * @return il pannello <code>JPanel</code> destro
     */
    private JPanel creaColonnaDestraDettaglio() {
        JPanel panelDestro = new JPanel(new BorderLayout(0, 10));
        panelDestro.setBackground(StileTheKnife.BIANCO);
        panelDestro.setMinimumSize(new Dimension(420, 250));
        panelDestro.setBorder(BorderFactory.createEmptyBorder(20, 26, 20, 26));

        JPanel panelInfoSuperiore = new JPanel();
        panelInfoSuperiore.setLayout(new BoxLayout(panelInfoSuperiore, BoxLayout.Y_AXIS));
        panelInfoSuperiore.setBackground(StileTheKnife.BIANCO);

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
            btnAggiungiPreferito = StileTheKnife.creaBottoneSecondario("★ Salva nei preferiti");
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

        lblBadgePrenotazione = new JLabel("Prenotazione o online: -");
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

        if (u != null && u.isCliente()) {
            panelInfoSuperiore.add(Box.createVerticalStrut(8));
            panelInfoSuperiore.add(creaBoxScritturaRecensioneInline());
        } else {
            JLabel lblAvvisoGuest = StileTheKnife.creaEtichetta("Modalità ospite: effettua il login come cliente per salvare nei preferiti o lasciare una recensione.");
            lblAvvisoGuest.setForeground(StileTheKnife.TESTO_GRIGIO);
            lblAvvisoGuest.setBorder(BorderFactory.createEmptyBorder(6, 4, 4, 4));
            lblAvvisoGuest.setAlignmentX(Component.LEFT_ALIGNMENT);
            panelInfoSuperiore.add(lblAvvisoGuest);
        }

        panelDestro.add(panelInfoSuperiore, BorderLayout.NORTH);

        //Sezione recensioni
        JPanel wrapperRecensioni = new JPanel(new BorderLayout(0, 8));
        wrapperRecensioni.setBackground(StileTheKnife.BIANCO);
        wrapperRecensioni.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, StileTheKnife.GRIGIO_BORDO));

        JLabel lblTitoloRec = new JLabel("Recensioni lasciate dai clienti:");
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
     * Crea un riquadro per scrivere una recensione con selettore a 5 stelle e contatore di caratteri.
     * <p>
     * @return il pannello <code>JPanel</code> per l'inserimento della recensione
     */
    private JPanel creaBoxScritturaRecensioneInline() {
        JPanel box = new JPanel(new BorderLayout(10, 4));
        box.setBackground(StileTheKnife.GRIGIO_SFONDO);
        box.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(StileTheKnife.GRIGIO_BORDO, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        box.setAlignmentX(Component.LEFT_ALIGNMENT);
        box.setPreferredSize(new Dimension(650, 125));
        box.setMaximumSize(new Dimension(Integer.MAX_VALUE, 125));
        box.setMinimumSize(new Dimension(400, 125));

        //Riga superiore: Titolo e 5 stelle
        JPanel rigaStelle = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        rigaStelle.setOpaque(false);

        JLabel lblTitoloBox = new JLabel("Lascia la tua recensione:  ");
        lblTitoloBox.setFont(StileTheKnife.FONT_BOTTONE);
        lblTitoloBox.setForeground(StileTheKnife.TESTO_SCURO);
        rigaStelle.add(lblTitoloBox);

        for (int i = 0; i < 5; i++) {
            final int valoreStella = i + 1;
            JLabel stella = new JLabel("☆");
            stella.setFont(new Font("SansSerif", Font.BOLD, 22));
            stella.setCursor(new Cursor(Cursor.HAND_CURSOR));

            stella.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    aggiornaColoriStelle(valoreStella, true);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    aggiornaColoriStelle(stelleSelezionateInline, false);
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    stelleSelezionateInline = valoreStella;
                    aggiornaColoriStelle(stelleSelezionateInline, false);
                }
            });

            lblStelleInterattive[i] = stella;
            rigaStelle.add(stella);
        }

        lblTestoVotoScelto = new JLabel("  (Passa sulle stelle e clicca per votare)");
        lblTestoVotoScelto.setFont(StileTheKnife.FONT_NORMALE);
        lblTestoVotoScelto.setForeground(StileTheKnife.TESTO_GRIGIO);
        rigaStelle.add(lblTestoVotoScelto);

        aggiornaColoriStelle(0, false);

        //Riga inferiore
        JPanel rigaInputCommento = new JPanel(new BorderLayout(10, 0));
        rigaInputCommento.setOpaque(false);

        txtNuovaRecensioneInline = new JTextArea();
        txtNuovaRecensioneInline.setFont(StileTheKnife.FONT_NORMALE);
        txtNuovaRecensioneInline.setLineWrap(true);
        txtNuovaRecensioneInline.setWrapStyleWord(true);
        txtNuovaRecensioneInline.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));

        JLabel lblContatoreInline = new JLabel("0/400", SwingConstants.RIGHT);
        lblContatoreInline.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblContatoreInline.setForeground(StileTheKnife.TESTO_GRIGIO);
        StileTheKnife.applicaLimiteCaratteri(txtNuovaRecensioneInline, lblContatoreInline, 400);

        JPanel centroConContatore = new JPanel(new BorderLayout(0, 2));
        centroConContatore.setOpaque(false);
        JScrollPane scrollTxt = new JScrollPane(txtNuovaRecensioneInline);
        scrollTxt.setBorder(BorderFactory.createLineBorder(StileTheKnife.GRIGIO_BORDO));
        centroConContatore.add(scrollTxt, BorderLayout.CENTER);
        centroConContatore.add(lblContatoreInline, BorderLayout.SOUTH);

        btnPubblicaRecensioneInline = StileTheKnife.creaBottonePrimario("Pubblica");
        btnPubblicaRecensioneInline.setEnabled(false);
        btnPubblicaRecensioneInline.setPreferredSize(new Dimension(170, 38));
        btnPubblicaRecensioneInline.setMinimumSize(new Dimension(170, 38));
        btnPubblicaRecensioneInline.setMaximumSize(new Dimension(170, 38));
        btnPubblicaRecensioneInline.addActionListener(e -> pubblicaRecensioneInline());

        // Pannello a destra
        JPanel boxBottoneDestra = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        boxBottoneDestra.setOpaque(false);
        boxBottoneDestra.add(btnPubblicaRecensioneInline);

        rigaInputCommento.add(centroConContatore, BorderLayout.CENTER);
        rigaInputCommento.add(boxBottoneDestra, BorderLayout.EAST);

        box.add(rigaStelle, BorderLayout.NORTH);
        box.add(rigaInputCommento, BorderLayout.CENTER);
        return box;
    }

    /**
     * Aggiorna il colore delle 5 stelle interattive mostrandole gialle se selezionate e grigie se vuote.
     * @param numeroStelle il numero di stelle da colorare in giallo
     * @param isAnteprimaHover <code>true</code> se il mouse sta solo passando sopra le stelle senza cliccare, <code>false</code> altrimenti
     */
    private void aggiornaColoriStelle(int numeroStelle, boolean isAnteprimaHover) {
        if (lblStelleInterattive[0] == null) {
            return;
        }
        for (int i = 0; i < 5; i++) {
            if (i < numeroStelle) {
                lblStelleInterattive[i].setText("★");
                lblStelleInterattive[i].setForeground(StileTheKnife.GIALLO_STELLA);
            } else {
                lblStelleInterattive[i].setText("☆");
                lblStelleInterattive[i].setForeground(new Color(180, 190, 195));
            }
        }
        if (lblTestoVotoScelto != null) {
            if (numeroStelle == 0) {
                lblTestoVotoScelto.setText("  (Passa sulle stelle e clicca per votare)");
            } else if (isAnteprimaHover) {
                lblTestoVotoScelto.setText("  (" + numeroStelle + "/5 stelle)");
            } else {
                lblTestoVotoScelto.setText("  (" + numeroStelle + "/5 stelle confermate)");
            }
        }
    }

    /**
     * Azzera il campo di testo della nuova recensione e riporta le stelle selezionate a 0.
     */
    private void resettaInputRecensioneInline() {
        stelleSelezionateInline = 0;
        aggiornaColoriStelle(0, false);
        if (txtNuovaRecensioneInline != null) {
            txtNuovaRecensioneInline.setText("");
        }
    }

    /**
     * Valida e invia al server la nuova recensione scritta dal cliente.
     */
    private void pubblicaRecensioneInline() {
        if (ristoranteSelezionato == null) {
            return;
        }
        if (stelleSelezionateInline < 1 || stelleSelezionateInline > 5) {
            JOptionPane.showMessageDialog(this, "Clicca prima sulle stelle (da 1 a 5) per assegnare un voto!", "Seleziona le stelle", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String testo = txtNuovaRecensioneInline.getText().trim();
        if (testo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Scrivi un commento prima di pubblicare la recensione.", "Commento vuoto", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int stelleScelte = stelleSelezionateInline;
        if (controller.aggiungiRecensione(ristoranteSelezionato.getIdRistorante(), stelleScelte, testo)) {
            resettaInputRecensioneInline();
            mostraDettagliRistoranteADestra(ristoranteSelezionato);
            JOptionPane.showMessageDialog(this, "Recensione pubblicata con successo!");
        } else {
            resettaInputRecensioneInline();
            JOptionPane.showMessageDialog(this,
                    "Hai già scritto una recensione per questo ristorante.\nPuoi modificarla dal menù in alto a destra -> 'Le mie recensioni'.",
                    "Recensione già presente", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Carica le recensioni del ristorante scelto e aggiorna il pannello dei dettagli a destra.
     * @param r l'oggetto <code>Ristorante</code> da visualizzare
     */
    private void mostraDettagliRistoranteADestra(Ristorante r) {
        List<Recensione> recensioni = controller.visualizzaRecensioni(r.getIdRistorante());
        aggiornaDatiRistoranteSenzaResetInput(r, recensioni);
    }

    /**
     * Aggiorna i dettagli del ristorante e la lista delle recensioni senza cancellare il testo che l'utente sta scrivendo.
     * <p>
     * Posiziona la recensione dell'utente loggato al primo posto e pone la scrollbar in cima alla lista.
     * @param r l'oggetto <code>Ristorante</code> selezionato
     * @param recensioni la lista di oggetti <code>Recensione</code> associati al ristorante
     */
    private void aggiornaDatiRistoranteSenzaResetInput(Ristorante r, List<Recensione> recensioni) {
        this.ristoranteSelezionato = r;
        this.inModificaInline = false;
        this.ultimaImprontaRecensioni = calcolaImprontaRecensioni(recensioni);

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
        lblBadgePrenotazione.setText("Prenotazione online: " + (r.isPrenotazioneOnline() ? "Disponibile" : "Non disponibile"));

        if (btnAggiungiPreferito != null) {
            btnAggiungiPreferito.setEnabled(true);
        }
        if (btnPubblicaRecensioneInline != null) {
            btnPubblicaRecensioneInline.setEnabled(true);
        }

        double[] riepilogo = controller.visualizzaRiepilogo(r.getIdRistorante());
        lblValutazioneMedia.setText(String.format("★ Media: %.1f / 5.0 (%d recensioni)", riepilogo[1], (int) riepilogo[0]));

        panelContenitoreRecensioni.removeAll();
        Utente utenteCorrente = controller.getSessione().getUtenteCorrente();
        boolean isUtenteGuest = utenteCorrente.isGuest();
        int idUtenteLoggato = isUtenteGuest ? -1 : utenteCorrente.getIdUtente();

        if (recensioni.isEmpty()) {
            JLabel lblVuota = StileTheKnife.creaEtichetta("Ancora nessuna recensione presente per questo ristorante.");
            lblVuota.setForeground(StileTheKnife.TESTO_GRIGIO);
            lblVuota.setBorder(BorderFactory.createEmptyBorder(15, 5, 15, 5));
            panelContenitoreRecensioni.add(lblVuota);
        } else {
            List<Recensione> recensioniOrdinate = new ArrayList<>(recensioni);
            recensioniOrdinate.sort((r1, r2) -> {
                boolean r1Mia = (!isUtenteGuest && r1.getIdUtente() == idUtenteLoggato);
                boolean r2Mia = (!isUtenteGuest && r2.getIdUtente() == idUtenteLoggato);
                if (r1Mia && !r2Mia) return -1;
                if (!r1Mia && r2Mia) return 1;
                return 0;
            });

            for (Recensione rec : recensioniOrdinate) {
                boolean isMiaRecensione = (!isUtenteGuest && rec.getIdUtente() == idUtenteLoggato);
                panelContenitoreRecensioni.add(creaCardSingolaRecensione(rec, isMiaRecensione, isUtenteGuest));
                panelContenitoreRecensioni.add(Box.createVerticalStrut(8));
            }
            panelContenitoreRecensioni.add(Box.createVerticalGlue());
        }

        panelContenitoreRecensioni.revalidate();
        panelContenitoreRecensioni.repaint();
        javax.swing.SwingUtilities.invokeLater(() -> {
            if (panelContenitoreRecensioni.getParent() instanceof javax.swing.JViewport viewport) {
                viewport.setViewPosition(new java.awt.Point(0, 0));
            }
        });
    }

    /**
     * Crea il pannello grafico per una singola recensione.
     * <p>
     * Se la recensione appartiene all'utente loggato, la evidenzia in grigio e aggiunge il tasto per modificarla sul posto.
     * @param rec l'oggetto <code>Recensione</code> da mostrare
     * @param isMiaRecensione <code>true</code> se l'autore della recensione è il cliente loggato, <code>false</code> altrimenti
     * @param isUtenteGuest <code>true</code> se chi guarda è un utente ospite, <code>false</code> altrimenti
     * @return il pannello <code>JPanel</code> della recensione formattato
     */
    private JPanel creaCardSingolaRecensione(Recensione rec, boolean isMiaRecensione, boolean isUtenteGuest) {
        Color coloreSfondoCard = isMiaRecensione ? StileTheKnife.GRIGIO_SFONDO : StileTheKnife.BIANCO;

        JPanel rigaRec = new JPanel(new BorderLayout(8, 6)) {
            @Override
            public Dimension getMaximumSize() {
                Dimension pref = getPreferredSize();
                return new Dimension(Integer.MAX_VALUE, pref.height);
            }
        };
        rigaRec.setBackground(coloreSfondoCard);

        if (isMiaRecensione) {
            rigaRec.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(StileTheKnife.GRIGIO_BORDO, 1),
                    BorderFactory.createEmptyBorder(12, 14, 12, 14)
            ));
        } else {
            rigaRec.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, StileTheKnife.GRIGIO_BORDO),
                    BorderFactory.createEmptyBorder(12, 6, 12, 8)
            ));
        }

        String nomeAutore = (!isUtenteGuest && rec.getUsernameAutore() != null && !rec.getUsernameAutore().isEmpty())
                ? rec.getUsernameAutore()
                : "Anonimo";

        String tagTuaRecensione = isMiaRecensione
                ? "  <span style='color:#006666;'>(La tua recensione)</span>"
                : "";

        //Barra superiore della recensione
        JPanel headerRec = new JPanel(new BorderLayout());
        headerRec.setOpaque(false);

        JLabel lblTopRec = new JLabel("<html>" + StileTheKnife.stelleGialleHtml(rec.getStelle())
                + "  <b style='color:#1E282D; font-size:11pt;'>(" + rec.getStelle() + "/5)   —   "
                + nomeAutore + "   [" + rec.getDataRecensione() + "]" + tagTuaRecensione + "</b></html>");
        headerRec.add(lblTopRec, BorderLayout.WEST);

        //Corpo centrale
        JPanel panelCorpoDinamico = new JPanel(new BorderLayout());
        panelCorpoDinamico.setOpaque(false);

        String testoPulito = rec.getTesto().replace(" [MODIFICATA DAL CLIENTE]", "");
        JTextArea txtTestoRec = new JTextArea(testoPulito);
        txtTestoRec.setFont(StileTheKnife.FONT_NORMALE);
        txtTestoRec.setForeground(StileTheKnife.TESTO_SCURO);
        txtTestoRec.setBackground(coloreSfondoCard);
        txtTestoRec.setLineWrap(true);
        txtTestoRec.setWrapStyleWord(true);
        txtTestoRec.setEditable(false);
        txtTestoRec.setFocusable(false);
        txtTestoRec.setBorder(BorderFactory.createEmptyBorder(4, 2, 4, 2));
        txtTestoRec.setCaretPosition(0);

        panelCorpoDinamico.add(txtTestoRec, BorderLayout.CENTER);

        if (isMiaRecensione) {
            JPanel boxBottoniAzioneMia = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
            boxBottoniAzioneMia.setOpaque(false);

            JButton btnModificaAlVolo = StileTheKnife.creaBottoneSecondario("✎ Modifica");
            btnModificaAlVolo.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));

            btnModificaAlVolo.addActionListener(e -> {
                inModificaInline = true;
                btnModificaAlVolo.setVisible(false);
                panelCorpoDinamico.removeAll();
                JPanel editorInPlace = new JPanel(new BorderLayout(6, 6));
                editorInPlace.setOpaque(false);
                editorInPlace.setBorder(BorderFactory.createEmptyBorder(6, 0, 4, 0));

                final int[] nuoveStelleScelte = {rec.getStelle()};
                final JLabel[] stelleEdit = new JLabel[5];
                JPanel rigaStelleEdit = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
                rigaStelleEdit.setOpaque(false);

                JLabel lblModVoto = new JLabel("Modifica valutazione: ");
                lblModVoto.setFont(StileTheKnife.FONT_BOTTONE);
                lblModVoto.setForeground(StileTheKnife.TESTO_SCURO);
                rigaStelleEdit.add(lblModVoto);

                Runnable aggiornaStelleEdit = () -> {
                    for (int k = 0; k < 5; k++) {
                        if (k < nuoveStelleScelte[0]) {
                            stelleEdit[k].setText("★");
                            stelleEdit[k].setForeground(StileTheKnife.GIALLO_STELLA);
                        } else {
                            stelleEdit[k].setText("☆");
                            stelleEdit[k].setForeground(new Color(180, 190, 195));
                        }
                    }
                };

                for (int i = 0; i < 5; i++) {
                    final int val = i + 1;
                    JLabel st = new JLabel("★");
                    st.setFont(new Font("SansSerif", Font.BOLD, 22));
                    st.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    st.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseEntered(MouseEvent ev) {
                            for (int k = 0; k < 5; k++) {
                                stelleEdit[k].setText(k < val ? "★" : "☆");
                                stelleEdit[k].setForeground(k < val ? StileTheKnife.GIALLO_STELLA : new Color(180, 190, 195));
                            }
                        }

                        @Override
                        public void mouseExited(MouseEvent ev) {
                            aggiornaStelleEdit.run();
                        }

                        @Override
                        public void mouseClicked(MouseEvent ev) {
                            nuoveStelleScelte[0] = val;
                            aggiornaStelleEdit.run();
                        }
                    });
                    stelleEdit[i] = st;
                    rigaStelleEdit.add(st);
                }
                aggiornaStelleEdit.run();

                // Casella di testo modificabile sul posto
                JTextArea txtEditInPlace = new JTextArea(testoPulito, 3, 25);
                txtEditInPlace.setFont(StileTheKnife.FONT_NORMALE);
                txtEditInPlace.setLineWrap(true);
                txtEditInPlace.setWrapStyleWord(true);
                txtEditInPlace.setBackground(StileTheKnife.BIANCO);
                txtEditInPlace.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));

                JLabel lblContatoreEdit = new JLabel("0/400", SwingConstants.LEFT);
                lblContatoreEdit.setFont(new Font("SansSerif", Font.BOLD, 12));
                StileTheKnife.applicaLimiteCaratteri(txtEditInPlace, lblContatoreEdit, 400);

                JScrollPane scrollEdit = new JScrollPane(txtEditInPlace);
                scrollEdit.setBorder(BorderFactory.createLineBorder(StileTheKnife.VERDE_THEKNIFE, 1));

                // Barra inferiore dell'editor
                JPanel barraSottoEdit = new JPanel(new BorderLayout());
                barraSottoEdit.setOpaque(false);
                barraSottoEdit.add(lblContatoreEdit, BorderLayout.WEST);

                JPanel bottoniConferma = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
                bottoniConferma.setOpaque(false);

                JButton btnAnnullaMod = StileTheKnife.creaBottoneSecondario("Annulla");
                btnAnnullaMod.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));

                JButton btnSalvaMod = StileTheKnife.creaBottonePrimario("Salva modifiche");
                btnSalvaMod.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));

                btnAnnullaMod.addActionListener(ev -> {
                    inModificaInline = false;
                    mostraDettagliRistoranteADestra(ristoranteSelezionato);
                });

                btnSalvaMod.addActionListener(ev -> {
                    String nuovoTesto = txtEditInPlace.getText().trim();
                    if (nuovoTesto.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Il commento della recensione non può essere vuoto.", "Attenzione", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    if (controller.modificaRecensione(rec.getIdRecensione(), nuoveStelleScelte[0], nuovoTesto)) {
                        inModificaInline = false;
                        mostraDettagliRistoranteADestra(ristoranteSelezionato);
                        if (modelloMieRecensioni != null) {
                            aggiornaTabellaMieRecensioni();
                        }
                    }
                });

                bottoniConferma.add(btnAnnullaMod);
                bottoniConferma.add(btnSalvaMod);
                barraSottoEdit.add(bottoniConferma, BorderLayout.EAST);

                editorInPlace.add(rigaStelleEdit, BorderLayout.NORTH);
                editorInPlace.add(scrollEdit, BorderLayout.CENTER);
                editorInPlace.add(barraSottoEdit, BorderLayout.SOUTH);

                panelCorpoDinamico.add(editorInPlace, BorderLayout.CENTER);
                panelCorpoDinamico.revalidate();
                panelCorpoDinamico.repaint();
                txtEditInPlace.requestFocusInWindow();
            });

            boxBottoniAzioneMia.add(btnModificaAlVolo);
            headerRec.add(boxBottoniAzioneMia, BorderLayout.EAST);
        }

        rigaRec.add(headerRec, BorderLayout.NORTH);
        rigaRec.add(panelCorpoDinamico, BorderLayout.CENTER);

        //Risposta del ristoratore
        if (rec.getRispostaRistoratore() != null && !rec.getRispostaRistoratore().isEmpty()) {
            JPanel boxRispostaEvidenziata = new JPanel(new BorderLayout(0, 3));
            boxRispostaEvidenziata.setBackground(isMiaRecensione ? StileTheKnife.BIANCO : StileTheKnife.GRIGIO_SFONDO);
            boxRispostaEvidenziata.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(6, 12, 2, 0),
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createMatteBorder(0, 3, 0, 0, StileTheKnife.VERDE_THEKNIFE),
                            BorderFactory.createEmptyBorder(8, 12, 8, 12)
                    )
            ));

            JLabel lblIntestazioneRisp = new JLabel("Risposta del ristoratore:");
            lblIntestazioneRisp.setFont(StileTheKnife.FONT_BOTTONE);
            lblIntestazioneRisp.setForeground(StileTheKnife.TESTO_SCURO);

            JTextArea txtTestoRisp = new JTextArea(rec.getRispostaRistoratore());
            txtTestoRisp.setFont(StileTheKnife.FONT_NORMALE);
            txtTestoRisp.setForeground(StileTheKnife.TESTO_SCURO);
            txtTestoRisp.setBackground(isMiaRecensione ? StileTheKnife.BIANCO : StileTheKnife.GRIGIO_SFONDO);
            txtTestoRisp.setLineWrap(true);
            txtTestoRisp.setWrapStyleWord(true);
            txtTestoRisp.setEditable(false);
            txtTestoRisp.setFocusable(false);
            txtTestoRisp.setBorder(null);
            txtTestoRisp.setCaretPosition(0);

            boxRispostaEvidenziata.add(lblIntestazioneRisp, BorderLayout.NORTH);
            boxRispostaEvidenziata.add(txtTestoRisp, BorderLayout.CENTER);

            rigaRec.add(boxRispostaEvidenziata, BorderLayout.SOUTH);
        }

        return rigaRec;
    }

    /**
     * Renderer grafico che disegna ogni elemento della lista ristoranti a sinistra sotto forma di scheda con bandiera.
     */
    private static class RistoranteCardRenderer extends JPanel implements ListCellRenderer<Ristorante> {

        /**
         * Etichetta per l'icona della bandiera nazionale <code>lblBandiera</code>.
         */
        private final JLabel lblBandiera = new JLabel();

        /**
         * Etichetta per il nome del ristorante <code>lblNome</code>.
         */
        private final JLabel lblNome = new JLabel();

        /**
         * Etichetta per il tipo di cucina <code>lblCucina</code>.
         */
        private final JLabel lblCucina = new JLabel();

        /**
         * Etichetta per la città del ristorante <code>lblCitta</code>.
         */
        private final JLabel lblCitta = new JLabel();

        /**
         * Etichetta per i simboli di fascia prezzo <code>lblPrezzo</code>.
         */
        private final JLabel lblPrezzo = new JLabel();

        /**
         * Costruttore della classe <code>RistoranteCardRenderer</code>.
         */
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

        /**
         * Configura e restituisce il componente grafico della scheda per la riga corrente della lista.
         * @param list la <code>JList</code> che contiene gli elementi
         * @param r l'oggetto <code>Ristorante</code> della riga corrente
         * @param index l'indice della riga
         * @param isSelected <code>true</code> se la scheda è selezionata dall'utente, <code>false</code> altrimenti
         * @param cellHasFocus <code>true</code> se la cella è quella selezionata, <code>false</code> altrimenti
         * @return il componente configurato da disegnare nella lista
         */
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
            if (icon == null && !fileFlag.equals("src/media/Globe.png")) {
                icon = StileTheKnife.caricaImmagine("src/media/Globe.png", 36, 24);
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

    /**
     * Crea il pannello che mostra la tabella dei ristoranti preferiti del cliente loggato.
     * @return il pannello <code>JPanel</code> dei preferiti
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
        JLabel lblTitolo = new JLabel("★ I miei ristoranti preferiti");
        lblTitolo.setFont(StileTheKnife.FONT_TITOLO);
        lblTitolo.setForeground(StileTheKnife.VERDE_THEKNIFE);
        JButton btnTorna = StileTheKnife.creaBottoneSecondario("← Torna all'esplorazione");
        btnTorna.addActionListener(e -> cardLayoutCentrale.show(panelContenitoreViste, "ESPLORA"));
        top.add(lblTitolo, BorderLayout.WEST);
        top.add(btnTorna, BorderLayout.EAST);
        panel.add(top, BorderLayout.NORTH);

        String[] colonne = {"Nome ristorante", "Città", "Nazione", "Tipo di cucina", "Fascia di prezzo"};
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

        JButton btnVediScheda = StileTheKnife.creaBottoneSecondario("Apri scheda ristorante");
        JButton btnRimuovi = StileTheKnife.creaBottonePrimario("Rimuovi dai preferiti");

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

    /**
     * Crea il pannello con la tabella riassuntiva delle recensioni scritte dal cliente loggato.
     * @return il pannello <code>JPanel</code> delle recensioni del cliente
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
        JLabel lblTitolo = new JLabel("✍ Le mie recensioni");
        lblTitolo.setFont(StileTheKnife.FONT_TITOLO);
        lblTitolo.setForeground(StileTheKnife.VERDE_THEKNIFE);
        JButton btnTorna = StileTheKnife.creaBottoneSecondario("← Torna all'esplorazione");
        btnTorna.addActionListener(e -> cardLayoutCentrale.show(panelContenitoreViste, "ESPLORA"));
        top.add(lblTitolo, BorderLayout.WEST);
        top.add(btnTorna, BorderLayout.EAST);
        panel.add(top, BorderLayout.NORTH);

        String[] colonne = {"Ristorante", "Città", "Voto (stelle)", "Il mio commento", "Data", "Risposta del ristoratore"};
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

        JButton btnModifica = StileTheKnife.creaBottoneSecondario("Modifica recensione");
        JButton btnElimina = StileTheKnife.creaBottonePrimario("Elimina recensione");

        btnModifica.addActionListener(e -> modificaRecensioneSelezionata());
        btnElimina.addActionListener(e -> eliminaRecensioneSelezionata());

        panelBottoni.add(btnModifica);
        panelBottoni.add(btnElimina);
        panel.add(panelBottoni, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Carica dal server i ristoranti vicini al luogo dell'utente all'apertura della finestra.
     */
    private void eseguiRicercaIniziale() {
        ristorantiTutti = controller.getRistorantiVicini();
        impostaNuovaListaPaginata(ristorantiTutti);
    }

    /**
     * Legge i valori inseriti nella barra di ricerca e nei filtri ed esegue la query tramite il controller.
     */
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

        ristorantiTutti = controller.cercaRistorante(nome, locazione, cucina, prezzoMin, prezzoMax, delivery, prenotazione, stelleMin);
        impostaNuovaListaPaginata(ristorantiTutti);
    }

    /**
     * Ripristina tutti i campi dei filtri ai valori predefiniti e ricarica la ricerca iniziale.
     */
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

    /**
     * Calcola il numero di pagine da 20 ristoranti ciascuna e mostra la prima.
     * @param listaCompleta la lista completa di oggetti <code>Ristorante</code> trovati dalla ricerca
     */
    private void impostaNuovaListaPaginata(List<Ristorante> listaCompleta) {
        this.ristorantiTutti = listaCompleta != null ? listaCompleta : new ArrayList<>();
        this.paginaCorrente = 1;
        this.totalePagine = Math.max(1, (int) Math.ceil((double) this.ristorantiTutti.size() / ELEMENTI_PER_PAGINA));
        lblContatoreRisultati.setText("Ristoranti trovati: " + this.ristorantiTutti.size());
        mostraPaginaCorrente();
    }

    /**
     * Carica nella lista a sinistra solo i ristoranti appartenenti alla <code>paginaCorrente</code>.
     */
    private void mostraPaginaCorrente() {
        modelloListaCard.clear();
        int indiceInizio = (paginaCorrente - 1) * ELEMENTI_PER_PAGINA;
        int indiceFine = Math.min(indiceInizio + ELEMENTI_PER_PAGINA, ristorantiTutti.size());

        for (int i = indiceInizio; i < indiceFine; i++) {
            modelloListaCard.addElement(ristorantiTutti.get(i));
        }

        lblInfoPagina.setText(paginaCorrente + " / " + totalePagine);
        btnPaginaIndietro.setEnabled(paginaCorrente > 1);
        btnPaginaAvanti.setEnabled(paginaCorrente < totalePagine);

        if (!modelloListaCard.isEmpty()) {
            listaCardRistoranti.setSelectedIndex(0);
            listaCardRistoranti.ensureIndexIsVisible(0);
        }
    }

    /**
     * Aggiorna le righe della tabella dei ristoranti preferiti del cliente.
     */
    private void aggiornaTabellaPreferiti() {
        preferitiCorrenti = controller.visualizzaPreferiti();
        modelloPreferiti.setRowCount(0);
        for (Ristorante r : preferitiCorrenti) {
            modelloPreferiti.addRow(new Object[]{
                    r.getNome(), r.getCitta(), r.getNazione(), r.getTipoCucina(), StileTheKnife.formattaFasciaPrezzo(r.getFasciaPrezzo())
            });
        }
    }

    /**
     * Aggiorna le righe della tabella che elenca tutte le recensioni scritte dal cliente.
     */
    private void aggiornaTabellaMieRecensioni() {
        mieRecensioniCorrenti = controller.visualizzaMieRecensioniCliente();
        modelloMieRecensioni.setRowCount(0);
        for (Recensione rec : mieRecensioniCorrenti) {
            Ristorante r = controller.visualizzaRistorante(rec.getIdRistorante());
            String nomeRist = (r != null) ? r.getNome() : "Ristorante #" + rec.getIdRistorante();
            String cittaRist = (r != null) ? r.getCitta() : "-";
            String risp = (rec.getRispostaRistoratore() != null) ? rec.getRispostaRistoratore() : "In attesa di risposta";

            modelloMieRecensioni.addRow(new Object[]{
                    nomeRist, cittaRist, "<html>" + StileTheKnife.stelleGialleHtml(rec.getStelle()) + "</html>", rec.getTesto().replace(" [MODIFICATA DAL CLIENTE]", ""), rec.getDataRecensione(), risp
            });
        }
    }

    /**
     * Apre il pannello per modificare la recensione selezionata nella tabella delle recensioni del cliente.
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

        JLabel lblContatoreModifica = new JLabel("0/400", SwingConstants.RIGHT);
        lblContatoreModifica.setFont(new Font("SansSerif", Font.BOLD, 12));
        StileTheKnife.applicaLimiteCaratteri(txtNuovoTesto, lblContatoreModifica, 400);

        JPanel panel = new JPanel(new BorderLayout(10, 8));
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        top.setOpaque(false);
        JLabel lblVoto = StileTheKnife.creaEtichetta("Nuova valutazione in stelle:");
        lblVoto.setForeground(StileTheKnife.VERDE_THEKNIFE);
        lblVoto.setFont(StileTheKnife.FONT_BOTTONE);
        top.add(lblVoto);
        top.add(cmbStelle);

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(txtNuovoTesto), BorderLayout.CENTER);
        panel.add(lblContatoreModifica, BorderLayout.SOUTH);

        if (StileTheKnife.mostraDialogoStilizzato(this, "Modifica la tua recensione", panel, "Salva modifiche", 500, 380)) {
            String nuovoTesto = txtNuovoTesto.getText().trim();
            if (!nuovoTesto.isEmpty() && controller.modificaRecensione(rec.getIdRecensione(), (Integer) cmbStelle.getSelectedItem(), nuovoTesto)) {
                aggiornaTabellaMieRecensioni();
                if (ristoranteSelezionato != null && ristoranteSelezionato.getIdRistorante() == rec.getIdRistorante()) {
                    mostraDettagliRistoranteADestra(ristoranteSelezionato);
                }
            }
        }
    }

    /**
     * Chiede conferma ed elimina dal database la recensione selezionata nella tabella del cliente.
     */
    private void eliminaRecensioneSelezionata() {
        int riga = tabellaMieRecensioni.getSelectedRow();
        if (riga < 0) {
            JOptionPane.showMessageDialog(this, "Seleziona prima una recensione da eliminare.");
            return;
        }
        Recensione rec = mieRecensioniCorrenti.get(riga);
        int conferma = JOptionPane.showConfirmDialog(this, "Sei sicuro di voler eliminare questa recensione?", "Conferma eliminazione", JOptionPane.YES_NO_OPTION);
        if (conferma == JOptionPane.YES_OPTION && controller.eliminaRecensione(rec.getIdRecensione())) {
            aggiornaTabellaMieRecensioni();
            if (ristoranteSelezionato != null && ristoranteSelezionato.getIdRistorante() == rec.getIdRistorante()) {
                mostraDettagliRistoranteADestra(ristoranteSelezionato);
            }
        }
    }
}