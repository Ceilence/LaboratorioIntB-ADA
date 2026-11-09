/*
 * Autori del progetto:
 * - Alessandro Frigerio, Matricola: 759926, Sede: CO
 * - Antonio Pardo, Matricola: 760613, Sede: CO
 * - Davide Moretti, Matricola: 762176, Sede: CO
 */
package theknife.gui;

import theknife.essenziali.Ristorante;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Classe di utilità grafica che raccoglie colori, font e metodi statici
 * per la creazione e personalizzazione dei componenti dell'interfaccia.
 *
 * @author Alessandro Frigerio
 * @author Antonio Pardo
 * @author Davide Moretti
 */
public class StileTheKnife {

    /** Colore verde principale del brand TheKnife. */
    public static final Color VERDE_THEKNIFE = new Color(0, 102, 102);
    /** Alias per il colore primario dell'applicazione. */
    public static final Color VERDE_PRIMARIO = VERDE_THEKNIFE;
    /** Colore di sfondo predefinito per i bottoni primari. */
    public static final Color VERDE_BOTTONE = VERDE_THEKNIFE;
    /** Colore verde più chiaro per l'effetto al passaggio del mouse sui bottoni. */
    public static final Color VERDE_HOVER = new Color(10, 160, 140);
    /** Colore verde chiaro usato per gli sfondi secondari e la selezione. */
    public static final Color VERDE_CHIARO_SFONDO = new Color(224, 242, 241);
    /** Colore giallo utilizzato per rappresentare le stelle delle recensioni. */
    public static final Color GIALLO_STELLA = new Color(245, 176, 0);
    /** Colore rosso usato per messaggi di errore o avvisi. */
    public static final Color ROSSO_NOTIFICA = new Color(220, 38, 38);
    /** Colore bianco standard. */
    public static final Color BIANCO = Color.WHITE;
    /** Colore grigio chiaro per lo sfondo dei pannelli principali. */
    public static final Color GRIGIO_SFONDO = new Color(244, 247, 246);
    /** Colore scuro per il testo principale. */
    public static final Color TESTO_SCURO = new Color(30, 40, 45);
    /** Colore grigio per testi secondari o didascalie. */
    public static final Color TESTO_GRIGIO = new Color(100, 115, 120);
    /** Colore grigio chiaro per i bordi dei componenti e delle tabelle. */
    public static final Color GRIGIO_BORDO = new Color(215, 222, 220);

    /** Font utilizzato per il logo testuale di dimensione standard. */
    public static final Font FONT_BRAND = new Font("Georgia", Font.BOLD | Font.ITALIC, 22);
    /** Font utilizzato per il logo testuale di dimensione grande. */
    public static final Font FONT_BRAND_GRANDE = new Font("Georgia", Font.BOLD | Font.ITALIC, 32);
    /** Font utilizzato per i titoli principali delle schermate. */
    public static final Font FONT_TITOLO = new Font("SansSerif", Font.BOLD, 24);
    /** Font utilizzato per i sottotitoli e le intestazioni di sezione. */
    public static final Font FONT_SOTTOTITOLO = new Font("SansSerif", Font.BOLD, 16);
    /** Font standard per il testo normale e i campi di input. */
    public static final Font FONT_NORMALE = new Font("SansSerif", Font.PLAIN, 14);
    /** Font in grassetto utilizzato per il testo dei pulsanti. */
    public static final Font FONT_BOTTONE = new Font("SansSerif", Font.BOLD, 14);

    /** Mappa per salvare in cache le immagini già caricate e ridimensionate. */
    private static final Map<String, ImageIcon> CACHE_IMMAGINI = new HashMap<>();

    /**
     * Costruttore vuoto della classe.
     */
    public StileTheKnife() {
    }

    /**
     * Applica a una <code>JTextArea</code> un limite massimo di caratteri e aggiorna
     * in tempo reale l'etichetta contatore.
     *
     * @param textArea     la casella di testo da limitare
     * @param lblContatore l'etichetta che mostra il conteggio in tempo reale
     * @param maxCaratteri il numero massimo di caratteri consentiti
     */
    public static void applicaLimiteCaratteri(JTextArea textArea, JLabel lblContatore, int maxCaratteri) {
        if (textArea.getDocument() instanceof AbstractDocument doc) {
            doc.setDocumentFilter(new DocumentFilter() {
                @Override
                public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                    if (string == null) return;
                    if ((fb.getDocument().getLength() + string.length()) <= maxCaratteri) {
                        super.insertString(fb, offset, string, attr);
                    } else {
                        int spazioRimasto = maxCaratteri - fb.getDocument().getLength();
                        if (spazioRimasto > 0) {
                            super.insertString(fb, offset, string.substring(0, spazioRimasto), attr);
                        } else {
                            Toolkit.getDefaultToolkit().beep();
                        }
                    }
                }

                @Override
                public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                    if (text == null) return;
                    int lunghezzaAttuale = fb.getDocument().getLength();
                    int nuovaLunghezza = lunghezzaAttuale - length + text.length();
                    if (nuovaLunghezza <= maxCaratteri) {
                        super.replace(fb, offset, length, text, attrs);
                    } else {
                        int spazioDisponibile = maxCaratteri - (lunghezzaAttuale - length);
                        if (spazioDisponibile > 0) {
                            super.replace(fb, offset, length, text.substring(0, spazioDisponibile), attrs);
                        } else {
                            Toolkit.getDefaultToolkit().beep();
                        }
                    }
                }
            });

            Runnable aggiornaTestoContatore = () -> {
                int len = textArea.getText().length();
                lblContatore.setText(len + "/" + maxCaratteri);
                lblContatore.setForeground(len >= maxCaratteri ? ROSSO_NOTIFICA : TESTO_GRIGIO);
            };

            doc.addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    aggiornaTestoContatore.run();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    aggiornaTestoContatore.run();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    aggiornaTestoContatore.run();
                }
            });

            aggiornaTestoContatore.run();
        }
    }

    /**
     * Controlla che la password rispetti i requisiti minimi di sicurezza:
     * almeno 8 caratteri, una lettera maiuscola, una lettera minuscola e un numero.
     *
     * @param password la password in chiaro da verificare
     * @return <code>true</code> se la password rispetta i requisiti, <code>false</code> altrimenti
     */
    public static boolean validaPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        boolean haMaiuscola = false;
        boolean haMinuscola = false;
        boolean haNumero = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                haMaiuscola = true;
            } else if (Character.isLowerCase(c)) {
                haMinuscola = true;
            } else if (Character.isDigit(c)) {
                haNumero = true;
            }
        }

        return haMaiuscola && haMinuscola && haNumero;
    }

    /**
     * Carica un'immagine dai file di progetto o dalle risorse, la ridimensiona
     * e la salva in cache per evitare caricamenti ripetuti.
     *
     * @param nomeFile  il nome del file dell'immagine (es. "src/media/logo.png")
     * @param larghezza la larghezza desiderata in pixel
     * @param altezza   l'altezza desiderata in pixel
     * @return l'oggetto <code>ImageIcon</code> ridimensionato, oppure <code>null</code> se non trovato
     */
    public static ImageIcon caricaImmagine(String nomeFile, int larghezza, int altezza) {
        String chiaveCache = nomeFile + "_" + larghezza + "x" + altezza;
        if (CACHE_IMMAGINI.containsKey(chiaveCache)) {
            return CACHE_IMMAGINI.get(chiaveCache);
        }

        try {
            Image img = null;
            File[] possibiliPercorsi = {
                    new File(nomeFile),
                    new File("src/main/resources/" + nomeFile),
                    new File("resources/" + nomeFile),
                    new File("img/" + nomeFile)
            };
            for (File f : possibiliPercorsi) {
                if (f.exists()) {
                    img = new ImageIcon(f.getAbsolutePath()).getImage();
                    break;
                }
            }
            if (img == null) {
                URL urlRisorsa = StileTheKnife.class.getResource("/" + nomeFile);
                if (urlRisorsa != null) {
                    img = new ImageIcon(urlRisorsa).getImage();
                }
            }
            if (img != null) {
                ImageIcon scalata = new ImageIcon(img.getScaledInstance(larghezza, altezza, Image.SCALE_SMOOTH));
                CACHE_IMMAGINI.put(chiaveCache, scalata);
                return scalata;
            }
        } catch (Exception ignored) {
        }
        CACHE_IMMAGINI.put(chiaveCache, null);
        return null;
    }

    /**
     * Crea un'etichetta contenente l'icona del logo dell'applicazione.
     *
     * @param dimensione larghezza e altezza dell'icona in pixel
     * @return la <code>JLabel</code> con il logo caricato
     */
    public static JLabel creaLabelLogo(int dimensione) {
        JLabel lbl = new JLabel();
        ImageIcon icon = caricaImmagine("src/media/logo.png", dimensione, dimensione);
        if (icon != null) {
            lbl.setIcon(icon);
        }
        return lbl;
    }

    /**
     * Crea un'etichetta con l'immagine della bandiera associata alla nazione indicata.
     * Se l'immagine non è disponibile, mostra una sigla testuale di fallback.
     *
     * @param nazione   il nome della nazione
     * @param larghezza la larghezza dell'icona in pixel
     * @param altezza   l'altezza dell'icona in pixel
     * @return la <code>JLabel</code> configurata con la bandiera
     */
    public static JLabel creaLabelBandiera(String nazione, int larghezza, int altezza) {
        String fileBandiera = determinaFileBandiera(nazione);
        JLabel lbl = new JLabel();
        lbl.setPreferredSize(new Dimension(larghezza, altezza));
        ImageIcon icon = caricaImmagine(fileBandiera, larghezza, altezza);
        if (icon == null && !fileBandiera.equals("src/media/Globe.png")) {
            icon = caricaImmagine("src/media/Globe.png", larghezza, altezza);
        }
        if (icon != null) {
            lbl.setIcon(icon);
        } else {
            lbl.setOpaque(true);
            lbl.setBackground(VERDE_CHIARO_SFONDO);
            lbl.setForeground(VERDE_THEKNIFE);
            lbl.setFont(new Font("SansSerif", Font.BOLD, 11));
            lbl.setHorizontalAlignment(JLabel.CENTER);
            String sigla = (nazione != null && nazione.length() >= 2) ? nazione.substring(0, 2).toUpperCase() : "🌐";
            lbl.setText(sigla);
            lbl.setBorder(BorderFactory.createLineBorder(GRIGIO_BORDO));
        }
        return lbl;
    }

    /**
     * Restituisce il nome del file immagine della bandiera corrispondente alla nazione passata.
     *
     * @param nazione il nome della nazione (in italiano o inglese)
     * @return il nome del file della bandiera, oppure "src/media/Globe.png" come valore predefinito
     */
    public static String determinaFileBandiera(String nazione) {
        if (nazione == null) {
            return "src/media/Globe.png";
        }
        String n = nazione.trim().toLowerCase();
        if (n.contains("italy") || n.contains("italia")) {
            return "src/media/Flag_of_Italy.png";
        } else if (n.contains("france") || n.contains("francia")) {
            return "src/media/Flag_of_France.png";
        } else if (n.contains("germany") || n.contains("germania")) {
            return "src/media/Flag_of_Germany.png";
        } else if (n.contains("spain") || n.contains("spagna")) {
            return "src/media/Flag_of_Spain.png";
        } else if (n.contains("japan") || n.contains("giappone")) {
            return "src/media/Flag_of_Japan.png";
        } else if (n.contains("china") || n.contains("cina") || n.contains("hong kong") || n.contains("macau")) {
            return "src/media/Flag_of_China.png";
        } else if (n.contains("united states") || n.equals("usa") || n.contains("stati uniti")) {
            return "src/media/Flag_of_United_States.png";
        }
        return "src/media/Globe.png";
    }

    /**
     * Genera una breve descrizione testuale del ristorante a partire dai suoi dati
     * (posizione, tipo di cucina, prezzo e servizi disponibili).
     *
     * @param r il ristorante di cui generare la descrizione
     * @return la stringa con la descrizione formattata
     */
    public static String generaDescrizioneRistorante(Ristorante r) {
        if (r == null) {
            return "";
        }
        String servizi = (r.isDelivery() && r.isPrenotazioneOnline())
                ? "Il locale offre sia il comodo servizio di consegna a domicilio (Delivery) sia la prenotazione online del tavolo."
                : (r.isDelivery()
                   ? "Il locale mette a disposizione il servizio di consegna a domicilio (Delivery)."
                   : (r.isPrenotazioneOnline()
                      ? "È disponibile il servizio di prenotazione online direttamente dalla piattaforma."
                      : "Il locale accoglie i clienti direttamente in sede per un'esperienza gastronomica tradizionale."));

        return "Situato a " + r.getCitta() + " (" + r.getNazione() + ") in " + r.getIndirizzo() + ", "
                + r.getNome() + " è un ristorante selezionato che propone una cucina di tipo " + r.getTipoCucina() + ". "
                + "La fascia di prezzo media indicativa è di circa " + r.getFasciaPrezzo() + " € a persona, "
                + "con particolare attenzione alla qualità delle materie prime e all'accoglienza. " + servizi;
    }

    /**
     * Genera una stringa in formato HTML per mostrare graficamente le stelle di valutazione
     * (da 0 a 5), colorando di giallo quelle piene e di grigio quelle vuote.
     *
     * @param stelle il numero di stelle piene da visualizzare
     * @return la stringa HTML con le stelle formattate
     */
    public static String stelleGialleHtml(int stelle) {
        int piene = Math.max(0, Math.min(5, stelle));
        String sPiene = "★".repeat(piene);
        String sVuote = "☆".repeat(5 - piene);
        return "<span style='color:#F5B000; font-size:14pt;'>" + sPiene + "</span>"
                + "<span style='color:#B0BEC5; font-size:14pt;'>" + sVuote + "</span>";
    }

    /**
     * Crea un pulsante primario che ripristina sempre il colore originale quando il mouse esce
     * o quando il pulsante viene disabilitato.
     *
     * @param testo il testo del pulsante
     * @return il componente <code>JButton</code>
     */
    public static JButton creaBottonePrimario(String testo) {
        JButton btn = new JButton(testo);
        btn.setFont(FONT_BOTTONE);
        btn.setBackground(VERDE_THEKNIFE);
        btn.setForeground(BIANCO);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(VERDE_HOVER);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(VERDE_THEKNIFE);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (!btn.isEnabled()) {
                    btn.setBackground(VERDE_THEKNIFE);
                }
            }
        });

        btn.addPropertyChangeListener("enabled", evt -> {
            btn.setBackground(VERDE_THEKNIFE);
            btn.setForeground(BIANCO);
        });

        return btn;
    }

    /**
     * Crea un pulsante secondario che torna sempre del suo colore originale anche se
     * viene cliccato o disabilitato mentre il cursore è sopra.
     *
     * @param testo il testo del pulsante
     * @return il componente <code>JButton</code>
     */
    public static JButton creaBottoneSecondario(String testo) {
        JButton btn = new JButton(testo);
        btn.setFont(FONT_BOTTONE);
        btn.setBackground(VERDE_CHIARO_SFONDO);
        btn.setForeground(VERDE_THEKNIFE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(VERDE_HOVER);
                    btn.setForeground(BIANCO);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(VERDE_CHIARO_SFONDO);
                btn.setForeground(VERDE_THEKNIFE);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (!btn.isEnabled()) {
                    btn.setBackground(VERDE_CHIARO_SFONDO);
                    btn.setForeground(VERDE_THEKNIFE);
                }
            }
        });

        btn.addPropertyChangeListener("enabled", evt -> {
            btn.setBackground(VERDE_CHIARO_SFONDO);
            btn.setForeground(VERDE_THEKNIFE);
        });

        return btn;
    }

    /**
     * Crea un campo di testo (<code>JTextField</code>).
     *
     * @param colonne il numero di colonne per dimensionare il campo
     * @return il <code>JTextField</code> stilizzato
     */
    public static JTextField creaCampoTesto(int colonne) {
        JTextField txt = new JTextField(colonne);
        txt.setFont(FONT_NORMALE);
        txt.setForeground(TESTO_SCURO);
        txt.setBackground(BIANCO);
        txt.setBorder(creaBordoInput());
        return txt;
    }

    /**
     * Crea un campo per l'inserimento della password (<code>JPasswordField</code>)
     * con i caratteri oscurati da pallini.
     *
     * @param colonne il numero di colonne per dimensionare il campo
     * @return il <code>JPasswordField</code> stilizzato
     */
    public static JPasswordField creaCampoPassword(int colonne) {
        JPasswordField pwd = new JPasswordField(colonne);
        pwd.setFont(FONT_NORMALE);
        pwd.setEchoChar('\u2022');
        pwd.setForeground(TESTO_SCURO);
        pwd.setBackground(BIANCO);
        pwd.setBorder(creaBordoInput());
        return pwd;
    }

    /**
     * Crea un'etichetta di testo (<code>JLabel</code>) con font e colore standard.
     *
     * @param testo il testo da mostrare nell'etichetta
     * @return la <code>JLabel</code> configurata
     */
    public static JLabel creaEtichetta(String testo) {
        JLabel lbl = new JLabel(testo);
        lbl.setFont(FONT_NORMALE);
        lbl.setForeground(TESTO_SCURO);
        return lbl;
    }

    /**
     * Crea un pannello a sfondo bianco con margini interni predefiniti,
     * usato come contenitore in stile "card".
     *
     * @return il <code>JPanel</code> configurato
     */
    public static JPanel creaCardBianca() {
        JPanel card = new JPanel();
        card.setBackground(BIANCO);
        card.setBorder(BorderFactory.createEmptyBorder(25, 28, 25, 28));
        return card;
    }

    /**
     * Crea il bordo standard per i campi di input, composto da una linea grigia
     * esterna e una spaziatura interna (padding).
     *
     * @return l'oggetto <code>Border</code> creato
     */
    public static Border creaBordoInput() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GRIGIO_BORDO, 1),
                BorderFactory.createEmptyBorder(7, 10, 7, 10)
        );
    }

    /**
     * Blocca le dimensioni di un componente
     * all'altezza standard di 38 pixel e alla larghezza specificata.
     *
     * @param comp      il componente grafico da ridimensionare
     * @param larghezza la larghezza desiderata in pixel
     */
    public static void fissaDimensioneInput(JComponent comp, int larghezza) {
        Dimension d = new Dimension(larghezza, 38);
        comp.setPreferredSize(d);
        comp.setMinimumSize(d);
        comp.setMaximumSize(d);
    }

    /**
     * Personalizza un <code>JSplitPane</code> applicando un bordo e un divisore
     * centrale dello stesso colore verde del tema.
     *
     * @param split il componente <code>JSplitPane</code> da stilizzare
     */
    public static void applicaSplitVerdeContinuo(JSplitPane split) {
        split.setDividerSize(6);
        split.setBorder(BorderFactory.createMatteBorder(0, 4, 4, 4, VERDE_THEKNIFE));
        split.setBackground(VERDE_THEKNIFE);
        split.setUI(new BasicSplitPaneUI() {
            @Override
            public BasicSplitPaneDivider createDefaultDivider() {
                return new BasicSplitPaneDivider(this) {
                    @Override
                    public void paint(Graphics g) {
                        g.setColor(VERDE_THEKNIFE);
                        g.fillRect(0, 0, getSize().width, getSize().height);
                    }
                };
            }
        });
    }

    /**
     * Applica lo stile grafico generale a una <code>JTable</code>,
     * formattando l'altezza delle righe, i colori di selezione e l'intestazione.
     *
     * @param tabella la tabella a cui applicare lo stile
     */
    public static void applicaStileTabella(JTable tabella) {
        tabella.setFont(FONT_NORMALE);
        tabella.setRowHeight(34);
        tabella.setGridColor(GRIGIO_BORDO);
        tabella.setSelectionBackground(VERDE_CHIARO_SFONDO);
        tabella.setSelectionForeground(TESTO_SCURO);
        tabella.setShowVerticalLines(false);
        tabella.getTableHeader().setReorderingAllowed(false);

        JTableHeader header = tabella.getTableHeader();
        header.setPreferredSize(new Dimension(header.getWidth(), 38));

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setBackground(VERDE_THEKNIFE);
                lbl.setForeground(BIANCO);
                lbl.setFont(FONT_BOTTONE);
                lbl.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                lbl.setOpaque(true);
                return lbl;
            }
        };

        for (int i = 0; i < tabella.getColumnModel().getColumnCount(); i++) {
            tabella.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }
    }

    /**
     * Mostra una finestra di dialogo modale personalizzata con intestazione verde,
     * contenuto centrale e pulsanti di conferma e annullamento in basso.
     *
     * @param parent        il frame genitore su cui centrare il dialogo
     * @param titolo        il titolo mostrato nella barra superiore
     * @param contenuto     il pannello centrale con i componenti da visualizzare
     * @param testoConferma il testo del pulsante di conferma
     * @param larghezza     la larghezza della finestra in pixel
     * @param altezza       l'altezza della finestra in pixel
     * @return <code>true</code> se l'utente clicca sul pulsante di conferma, <code>false</code> se annulla
     */
    public static boolean mostraDialogoStilizzato(JFrame parent, String titolo, JPanel contenuto,
                                                  String testoConferma, int larghezza, int altezza) {
        final boolean[] confermato = {false};
        JDialog dialog = new JDialog(parent, titolo, true);
        applicaIconaFinestra(dialog);
        dialog.setSize(larghezza, altezza);
        dialog.setLocationRelativeTo(parent);
        dialog.setResizable(false);
        dialog.setLayout(new BorderLayout());

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 12));
        header.setBackground(VERDE_THEKNIFE);
        header.add(creaLabelLogo(32));
        JLabel lblTitolo = new JLabel(titolo);
        lblTitolo.setFont(FONT_SOTTOTITOLO);
        lblTitolo.setForeground(BIANCO);
        header.add(lblTitolo);
        dialog.add(header, BorderLayout.NORTH);

        contenuto.setBackground(BIANCO);
        contenuto.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));
        dialog.add(contenuto, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        footer.setBackground(GRIGIO_SFONDO);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, GRIGIO_BORDO));

        JButton btnAnnulla = creaBottoneSecondario("Annulla");
        JButton btnOk = creaBottonePrimario(testoConferma);

        btnAnnulla.addActionListener(e -> dialog.dispose());
        btnOk.addActionListener(e -> {
            confermato[0] = true;
            dialog.dispose();
        });

        footer.add(btnAnnulla);
        footer.add(btnOk);
        dialog.add(footer, BorderLayout.SOUTH);

        dialog.setVisible(true);
        return confermato[0];
    }

    /**
     * Converte il prezzo numerico in una stringa con simboli di euro (da € a €€€€)
     * seguiti dal valore effettivo tra parentesi.
     *
     * @param prezzo il prezzo medio in euro
     * @return la stringa formattata della fascia di prezzo
     */
    public static String formattaFasciaPrezzo(int prezzo) {
        int numSimboli = Math.max(1, Math.min(4, Math.round(prezzo / 25.0f)));
        return "€".repeat(numSimboli) + " (" + prezzo + "€)";
    }

    /**
     * Imposta l'immagine <code>src/media/logo.png</code> come icona della finestra nella barra del titolo
     * del sistema operativo e nella barra delle applicazioni.
     *
     * @param finestra il <code>JFrame</code> o <code>JDialog</code> a cui applicare l'icona
     */
    public static void applicaIconaFinestra(java.awt.Window finestra) {
        ImageIcon icona = caricaImmagine("src/media/logo.png", 64, 64);
        if (icona != null && finestra != null) {
            finestra.setIconImage(icona.getImage());
        }
    }

    /**
     * Crea un pannello che affianca alla casella della password un riquadro cliccabile con l'occhio
     * per mostrare o nascondere la password che si sta scrivendo.
     *
     * @param pwdField        il componente <code>JPasswordField</code> già creato
     * @param larghezzaTotale larghezza complessiva (in pixel) del campo più il pulsante occhio
     * @return un <code>JPanel</code> pronto da inserire nel form al posto del singolo <code>JPasswordField</code>
     */
    public static JPanel creaPannelloPasswordConOcchio(JPasswordField pwdField, int larghezzaTotale) {
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.setOpaque(false);

        JButton btnOcchio = new JButton("👁");
        btnOcchio.setFont(new Font("SansSerif", Font.PLAIN, 16));
        btnOcchio.setBackground(VERDE_CHIARO_SFONDO);
        btnOcchio.setForeground(VERDE_THEKNIFE);
        btnOcchio.setFocusPainted(false);
        btnOcchio.setBorderPainted(false);
        btnOcchio.setContentAreaFilled(false);
        btnOcchio.setOpaque(true);
        btnOcchio.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnOcchio.setToolTipText("Mostra / Nascondi password");

        Dimension dimOcchio = new Dimension(40, 38);
        btnOcchio.setPreferredSize(dimOcchio);
        btnOcchio.setMinimumSize(dimOcchio);
        btnOcchio.setMaximumSize(dimOcchio);
        btnOcchio.setBorder(BorderFactory.createLineBorder(GRIGIO_BORDO, 1));

        btnOcchio.addActionListener(e -> {
            if (pwdField.getEchoChar() != (char) 0) {
                // Mostra la password in chiaro
                pwdField.setEchoChar((char) 0);
                btnOcchio.setBackground(VERDE_THEKNIFE);
                btnOcchio.setForeground(BIANCO);
            } else {
                // Torna a nascondere la password con i pallini
                pwdField.setEchoChar('\u2022');
                btnOcchio.setBackground(VERDE_CHIARO_SFONDO);
                btnOcchio.setForeground(VERDE_THEKNIFE);
            }
            pwdField.requestFocusInWindow();
        });

        panel.add(pwdField, BorderLayout.CENTER);
        panel.add(btnOcchio, BorderLayout.EAST);

        if (larghezzaTotale > 0) {
            Dimension d = new Dimension(larghezzaTotale, 38);
            panel.setPreferredSize(d);
            panel.setMinimumSize(d);
            panel.setMaximumSize(d);
        }
        return panel;
    }
}