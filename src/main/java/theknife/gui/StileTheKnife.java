/*
 * Autori del progetto:
 * - Alessandro Frigerio, Matricola: 759926, Sede: CO
 * - Antonio Pardo, Matricola: 760613, Sede: CO
 * - Davide Moretti, Matricola: 762176, Sede: CO
 */
package theknife.gui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.Border;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Classe di utilità grafica che definisce la palette di colori bianco/verde e i componenti moderni.
 * <p>
 * Centralizza i font, i bordi e lo stile dei pulsanti per mantenere un aspetto pulito
 * e uniforme in tutte le schermate dell'applicazione.
 * @author Alessandro Frigerio
 * @author Antonio Pardo
 * @author Davide Moretti
 */
public class StileTheKnife {

    /**
     * Verde scuro principale per l'intestazione e gli elementi in evidenza <code>VERDE_PRIMARIO</code>.
     */
    public static final Color VERDE_THEKNIFE = new Color(0, 102, 102);

    /**
     * Verde medio per i pulsanti principali <code>VERDE_BOTTONE</code>.
     */
    public static final Color VERDE_BOTTONE = new Color(46, 125, 50);

    /**
     * Verde più brillante quando il mouse passa sopra un pulsante <code>VERDE_HOVER</code>.
     */
    public static final Color VERDE_HOVER = new Color(67, 160, 71);

    /**
     * Verde chiarissimo per sfondi secondari e riquadri <code>VERDE_CHIARO_SFONDO</code>.
     */
    public static final Color VERDE_CHIARO_SFONDO = new Color(232, 245, 233);

    /**
     * Bianco puro per le schede e i pannelli principali <code>BIANCO</code>.
     */
    public static final Color BIANCO = Color.WHITE;

    /**
     * Grigio chiarissimo per lo sfondo generale delle finestre <code>GRIGIO_SFONDO</code>.
     */
    public static final Color GRIGIO_SFONDO = new Color(245, 247, 246);

    /**
     * Grigio scuro per i testi principali <code>TESTO_SCURO</code>.
     */
    public static final Color TESTO_SCURO = new Color(38, 50, 56);

    /**
     * Grigio medio per i bordi sottili delle caselle di testo <code>GRIGIO_BORDO</code>.
     */
    public static final Color GRIGIO_BORDO = new Color(200, 210, 205);

    /**
     * Font per i titoli principali delle schermate <code>FONT_TITOLO</code>.
     */
    public static final Font FONT_TITOLO = new Font("SansSerif", Font.BOLD, 24);

    /**
     * Font per i sottotitoli di sezione <code>FONT_SOTTOTITOLO</code>.
     */
    public static final Font FONT_SOTTOTITOLO = new Font("SansSerif", Font.BOLD, 16);

    /**
     * Font standard per etichette e campi di testo <code>FONT_NORMALE</code>.
     */
    public static final Font FONT_NORMALE = new Font("SansSerif", Font.PLAIN, 14);

    /**
     * Font in grassetto per i pulsanti <code>FONT_BOTTONE</code>.
     */
    public static final Font FONT_BOTTONE = new Font("SansSerif", Font.BOLD, 14);

    /**
     * Costruttore vuoto della classe <code>StileTheKnife</code>.
     */
    public StileTheKnife() {
    }

    /**
     * Crea un pulsante moderno con sfondo verde e testo bianco, con effetto cambio colore al passaggio del mouse.
     * @param testo il testo da mostrare sul pulsante
     * @return il componente <code>JButton</code> formattato
     */
    public static JButton creaBottonePrimario(String testo) {
        JButton btn = new JButton(testo);
        btn.setFont(FONT_BOTTONE);
        btn.setBackground(VERDE_BOTTONE);
        btn.setForeground(BIANCO);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
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
                    btn.setBackground(VERDE_BOTTONE);
                }
            }
        });
        return btn;
    }

    /**
     * Crea un pulsante secondario con sfondo bianco, bordo e testo verde.
     * @param testo il testo da mostrare sul pulsante
     * @return il componente <code>JButton</code> formattato
     */
    public static JButton creaBottoneSecondario(String testo) {
        JButton btn = new JButton(testo);
        btn.setFont(FONT_BOTTONE);
        btn.setBackground(BIANCO);
        btn.setForeground(VERDE_THEKNIFE);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(VERDE_BOTTONE, 1),
                BorderFactory.createEmptyBorder(9, 18, 9, 18)
        ));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(VERDE_CHIARO_SFONDO);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(BIANCO);
                }
            }
        });
        return btn;
    }

    /**
     * Crea una casella di testo moderna con margini interni ampi e bordo sottile.
     * @param colonne larghezza indicativa del campo
     * @return il componente <code>JTextField</code> formattato
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
     * Crea una casella per la password moderna e coordinata con i campi di testo.
     * @param colonne larghezza indicativa del campo
     * @return il componente <code>JPasswordField</code> formattato
     */
    public static JPasswordField creaCampoPassword(int colonne) {
        JPasswordField pwd = new JPasswordField(colonne);
        pwd.setFont(FONT_NORMALE);
        pwd.setForeground(TESTO_SCURO);
        pwd.setBackground(BIANCO);
        pwd.setBorder(creaBordoInput());
        return pwd;
    }

    /**
     * Crea un'etichetta di testo standard con il font e il colore del tema.
     * @param testo il testo dell'etichetta
     * @return il componente <code>JLabel</code> formattato
     */
    public static JLabel creaEtichetta(String testo) {
        JLabel lbl = new JLabel(testo);
        lbl.setFont(FONT_NORMALE);
        lbl.setForeground(TESTO_SCURO);
        return lbl;
    }

    /**
     * Crea un pannello bianco con bordo sottile e spaziatura interna, in stile scheda moderna.
     * @return il componente <code>JPanel</code> formattato
     */
    public static JPanel creaCardBianca() {
        JPanel card = new JPanel();
        card.setBackground(BIANCO);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GRIGIO_BORDO, 1),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));
        return card;
    }

    /**
     * Restituisce il bordo standard per le caselle di input.
     * @return oggetto <code>Border</code> composto da linea esterna e margine interno
     */
    public static Border creaBordoInput() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GRIGIO_BORDO, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        );
    }

    /**
     * Applica una dimensione fissa in altezza a un componente di input per evitare che si deformi nei layout.
     * @param comp il componente grafico da ridimensionare
     * @param larghezza larghezza preferita in pixel
     */
    public static void fissaDimensioneInput(JComponent comp, int larghezza) {
        Dimension d = new Dimension(larghezza, 38);
        comp.setPreferredSize(d);
        comp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
    }
}