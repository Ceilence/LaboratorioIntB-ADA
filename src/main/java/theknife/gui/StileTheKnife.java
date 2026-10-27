/*
 * Autori del progetto:
 * - Alessandro Frigerio, Matricola: 759926, Sede: CO
 * - Antonio Pardo, Matricola: 760613, Sede: CO
 * - Davide Moretti, Matricola: 762176, Sede: CO
 */
package theknife.gui;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URL;

/**
 * Classe di utilità grafica con palette <code>VERDE_THEKNIFE</code>, gestione del logo e componenti moderni.
 * @author Alessandro Frigerio
 * @author Antonio Pardo
 * @author Davide Moretti
 */
public class StileTheKnife {

    /**
     * Colore principale verde petrolio (0, 102, 102) del progetto <code>VERDE_THEKNIFE</code>.
     */
    public static final Color VERDE_THEKNIFE = new Color(0, 102, 102);

    /**
     * Alias mantenuto per compatibilità <code>VERDE_PRIMARIO</code>.
     */
    public static final Color VERDE_PRIMARIO = VERDE_THEKNIFE;

    /**
     * Colore principale per i pulsanti <code>VERDE_BOTTONE</code>.
     */
    public static final Color VERDE_BOTTONE = VERDE_THEKNIFE;

    /**
     * Colore verde acqua (10, 160, 140) al passaggio del mouse <code>VERDE_HOVER</code>.
     */
    public static final Color VERDE_HOVER = new Color(10, 160, 140);

    /**
     * Tonalità chiara coordinata per elementi selezionati e pulsanti secondari <code>VERDE_CHIARO_SFONDO</code>.
     */
    public static final Color VERDE_CHIARO_SFONDO = new Color(224, 242, 241);

    /**
     * Bianco puro <code>BIANCO</code>.
     */
    public static final Color BIANCO = Color.WHITE;

    /**
     * Grigio chiarissimo per sfondi <code>GRIGIO_SFONDO</code>.
     */
    public static final Color GRIGIO_SFONDO = new Color(244, 247, 246);

    /**
     * Grigio scuro per il testo principale <code>TESTO_SCURO</code>.
     */
    public static final Color TESTO_SCURO = new Color(38, 50, 56);

    /**
     * Grigio medio per testi secondari <code>TESTO_GRIGIO</code>.
     */
    public static final Color TESTO_GRIGIO = new Color(100, 115, 120);

    /**
     * Grigio per linee divisorie <code>GRIGIO_BORDO</code>.
     */
    public static final Color GRIGIO_BORDO = new Color(215, 222, 220);

    public static final Font FONT_TITOLO = new Font("SansSerif", Font.BOLD, 24);
    public static final Font FONT_SOTTOTITOLO = new Font("SansSerif", Font.BOLD, 16);
    public static final Font FONT_NORMALE = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font FONT_BOTTONE = new Font("SansSerif", Font.BOLD, 14);

    /**
     * Costruttore vuoto della classe <code>StileTheKnife</code>.
     */
    public StileTheKnife() {
    }

    /**
     * Carica e ridimensiona l'immagine <code>logo.png</code> cercandola nella radice del progetto o nelle risorse.
     * @param dimensione larghezza e altezza desiderata in pixel (es. 44 o 64)
     * @return un'etichetta <code>JLabel</code> contenente l'icona del logo
     */
    public static JLabel creaLabelLogo(int dimensione) {
        JLabel lblLogo = new JLabel();
        try {
            Image img = null;
            File fileEsterno = new File("logo.png");
            if (fileEsterno.exists()) {
                img = new ImageIcon(fileEsterno.getAbsolutePath()).getImage();
            } else {
                URL urlRisorsa = StileTheKnife.class.getResource("/logo.png");
                if (urlRisorsa != null) {
                    img = new ImageIcon(urlRisorsa).getImage();
                }
            }
            if (img != null) {
                Image scalata = img.getScaledInstance(dimensione, dimensione, Image.SCALE_SMOOTH);
                lblLogo.setIcon(new ImageIcon(scalata));
            }
        } catch (Exception ignored) {
        }
        return lblLogo;
    }

    /**
     * Crea un pulsante moderno verde (0, 102, 102) con hover (10, 160, 140).
     * @param testo il testo del pulsante
     * @return il componente <code>JButton</code> formattato
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
                if (btn.isEnabled()) {
                    btn.setBackground(VERDE_THEKNIFE);
                }
            }
        });
        return btn;
    }

    /**
     * Crea un pulsante secondario senza contorno (flat).
     * @param testo il testo del pulsante
     * @return il componente <code>JButton</code> formattato
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
                if (btn.isEnabled()) {
                    btn.setBackground(VERDE_CHIARO_SFONDO);
                    btn.setForeground(VERDE_THEKNIFE);
                }
            }
        });
        return btn;
    }

    /**
     * Crea una casella di testo moderna.
     * @param colonne larghezza indicativa
     * @return il componente <code>JTextField</code>
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
     * Crea una casella password con pallini piccoli e ordinati ('•' invece dei cerchi giganti di default).
     * @param colonne larghezza indicativa
     * @return il componente <code>JPasswordField</code>
     */
    public static JPasswordField creaCampoPassword(int colonne) {
        JPasswordField pwd = new JPasswordField(colonne);
        pwd.setFont(FONT_NORMALE);
        pwd.setEchoChar('\u2022'); // Pallino piccolo ed elegante
        pwd.setForeground(TESTO_SCURO);
        pwd.setBackground(BIANCO);
        pwd.setBorder(creaBordoInput());
        return pwd;
    }

    /**
     * Crea un'etichetta di testo standard.
     * @param testo il testo dell'etichetta
     * @return il componente <code>JLabel</code>
     */
    public static JLabel creaEtichetta(String testo) {
        JLabel lbl = new JLabel(testo);
        lbl.setFont(FONT_NORMALE);
        lbl.setForeground(TESTO_SCURO);
        return lbl;
    }

    /**
     * Crea un pannello bianco pulito senza effetto "scatola pesante".
     * @return il componente <code>JPanel</code>
     */
    public static JPanel creaCardBianca() {
        JPanel card = new JPanel();
        card.setBackground(BIANCO);
        card.setBorder(BorderFactory.createEmptyBorder(25, 28, 25, 28));
        return card;
    }

    /**
     * Restituisce il bordo per le caselle di input.
     * @return oggetto <code>Border</code>
     */
    public static Border creaBordoInput() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GRIGIO_BORDO, 1),
                BorderFactory.createEmptyBorder(7, 10, 7, 10)
        );
    }

    /**
     * Applica una dimensione fissa a un componente di input.
     * @param comp il componente grafico
     * @param larghezza larghezza in pixel
     */
    public static void fissaDimensioneInput(JComponent comp, int larghezza) {
        Dimension d = new Dimension(larghezza, 38);
        comp.setPreferredSize(d);
        comp.setMinimumSize(d);
        comp.setMaximumSize(d);
    }

    /**
     * Colora la barra divisoria di uno <code>JSplitPane</code> con il colore <code>VERDE_THEKNIFE</code> continuo fino in fondo.
     * @param split lo <code>JSplitPane</code> da personalizzare
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
     * Applica lo stile moderno a una <code>JTable</code> forzando l'intestazione verde.
     * @param tabella la tabella da formattare
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
     * Formatta il prezzo nei simboli dell'euro.
     * @param prezzo il prezzo medio in euro
     * @return stringa formattata
     */
    public static String formattaFasciaPrezzo(int prezzo) {
        int numSimboli = Math.max(1, Math.min(4, Math.round(prezzo / 25.0f)));
        return "€".repeat(numSimboli) + " (" + prezzo + "€)";
    }
}