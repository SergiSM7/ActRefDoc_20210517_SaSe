import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * @author Sergi Sagrera
 */

@SuppressWarnings("serial")
public class TresEnRatlla extends JFrame {
   private static final int filaSaSe = 3;
   private static final int columnaSaSe = 3;
public static final int MIDA_CELLA = 150; // Amplada i alçada de la cel·la (quadrat)
   public static final int AMPLADA_TAULA = MIDA_CELLA * 3;  // Amplada del taulell de joc
   public static final int ALSSADA_TAULA = MIDA_CELLA * 3;  // Alçada del taulell de joc
   // L'amplada i l'lçada de la taula és igual a la mida de la cel·la
   // multiplicat per la quantitat de cel·les que hi ha, és a dir igual a 3
   public static final int AMPLADA_QUADRAT = 10;                   // Amplada de la línia de quadrícula
   public static final int MEITAT_AMPLADA_QUADRAT = AMPLADA_QUADRAT / 2; // La meitat de l'amplada de la línia de quadrícula
   // Els símbols (creu / cercle) es mostren dins d’una cel·la,
   // amb distància des de la vora (padding)
   public static final int MARGE_CELLA = MIDA_CELLA / 6; // Marge de la cel·la.
   public static final int MIDA_CARACTER = MIDA_CELLA - MARGE_CELLA * 2; // MIDA DEL CARÀCTER
 
   // Es fa servir una enumeració (classe interna) per representar els diversos estats del joc
   public enum EstatDelJoc {
      EN_JOC, EMPAT, GUANYA_CREU, GUANYA_CERCLE
   }
   
   private EstatDelJoc estatActual;  // Estat actual del joc
 
   // Es fa servir una enumeració (classe interna) per representar el contingut de les cel·les
   public enum Cella {
      BUIDA, CREU, CERCLE
   }

   private Cella jugadorActual;  // el jugador actual
 
   private Cella[][] taulell   ; // Taulell de 3 per 3 cel·les
   private EspaiTreball canvas; // Dibuixa un "canvas" (JPanel) pel taulell de joc
   private JLabel barraDEstat;  // Barra d'estat
 
   /** 
    * Constructor per configurar el joc i els components de la GUI 
    */
   public TresEnRatlla() {
      canvas = new EspaiTreball();  // Construeix un canvas (espai) (JPanel)
      canvas.setPreferredSize(new Dimension(AMPLADA_TAULA, ALSSADA_TAULA));
 
      // L'espai (JPanel) activa un MouseEvent pel clic del ratolí
      canvas.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e) {  // Gestor del clic del ratolí
            int posXDelRatoli = e.getX();
            int posYDelRatoli = e.getY();
            // Obté la fila i la columna d'on s'hafet el clic del ratolí
            int filaSeleccionada = posYDelRatoli / MIDA_CELLA;
            int coluSeleccionada = posXDelRatoli / MIDA_CELLA;
 
            if (estatActual == EstatDelJoc.EN_JOC) {
               if (filaSeleccionada >= 0 && filaSeleccionada < columnaSaSe && coluSeleccionada >= 0
                     && coluSeleccionada < filaSaSe && taulell[filaSeleccionada][coluSeleccionada] == Cella.BUIDA) {
                  taulell[filaSeleccionada][coluSeleccionada] = jugadorActual; // Fa un moviment
                  actualitza(jugadorActual, filaSeleccionada, coluSeleccionada); // actualitza l'estat del joc
                  // Canvia de jugador
                  jugadorActual = (jugadorActual == Cella.CREU) ? Cella.CERCLE : Cella.CREU;
               }
            } else {         // Acaba el joc!!
               iniciaElJoc(); // Inicia el joc
            }
            // Mètode de class Component que torna a dibuixar el taulell a l'espai de joc
            repaint();  // Fa una crida al metode paintComponent().
         }
      });
 
      // Configuració de la barra d'estat (JLabel) per mostrar els missatges d'estat
      barraDEstat = new JLabel("  ");
      barraDEstat.setHorizontalAlignment(0);
      barraDEstat.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 15));
      barraDEstat.setBorder(BorderFactory.createEmptyBorder(2, 5, 4, 5));
 
      Container contenidor = getContentPane();
      contenidor.setLayout(new BorderLayout());
      contenidor.add(canvas, BorderLayout.CENTER);
      contenidor.add(barraDEstat, BorderLayout.PAGE_END);
 
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      pack();  // Empaqueta tots els components JFrame
      setTitle("Tres en ratlla");
      setVisible(true);  // mostra el JFrame
 
      taulell = new Cella[3][3]; // assigna la matriu
      iniciaElJoc(); // per inicialitzar el contingut i les variables del taulell de joc
   }
 
   /**
    *  Inicialitzeu el contingut i l'estat del taulell de joc
    */
   public void iniciaElJoc() {
      for (int i = 0; i < 3; ++i) {
         for (int j = 0; j < 3; ++j) {
            taulell[i][j] = Cella.BUIDA; // Buida totes les cel·les!
         }
      }
      estatActual = EstatDelJoc.EN_JOC; // Estat del joc preparat per jugar
      jugadorActual = Cella.CREU;       // El torn inicial és per les creus
   }
 
   /* Actualitza l'estat de joc desrpès que el jugador hagi col·locat el seu moviment
       (filaSeleccionada, coluSeleccionada). */
   public void actualitza(Cella laTirada, int filaSeleccionada, int coluSeleccionada) {
      if (hiHaJuanyador(laTirada, filaSeleccionada, coluSeleccionada)) {  // comprovar si hi ha guanyador
         estatActual = (laTirada == Cella.CREU) ? EstatDelJoc.GUANYA_CREU : EstatDelJoc.GUANYA_CERCLE;
      } else if (hiHaEmpat()) {  // Comprova si hi ha un empat
         estatActual = EstatDelJoc.EMPAT;
      }
      // En cas contrari, no hi haurà cap canvi a l’estat actual (encara EstatDelJoc.EN_JOC).
   }
 
   /* Torna cert si hi ha un empat (p.e., no hi ha cap cel·la buida) */
   public boolean hiHaEmpat() {
      for (int i = 0; i < 3; ++i) {
         for (int j = 0; j < 3; ++j) {
            if (taulell[i][j] == Cella.BUIDA) {
               return false; // s'ha trobat una cel·la buida, no hi ha empat, surt
            }
         }
      }
      return true;  // ja no hi ha cap cel·la buida, és un empat
   }
 
   /**
    *  Torna cert si el jugador amb "laTirada" ha guanyat després de col·locar-se a
       (filaSeleccionada, coluSeleccionada)
    */
   public boolean hiHaJuanyador(Cella laTirada, int filaSeleccionada, int coluSeleccionada) {
      boolean guanyador = false;
      
      boolean diagonalPrincipal = false;
      boolean diagonalSecundaria = false;
      
      if (taulell[filaSeleccionada][0] == laTirada  // 3 a una fila
           && taulell[filaSeleccionada][1] == laTirada
           && taulell[filaSeleccionada][2] == laTirada
       || taulell[0][coluSeleccionada] == laTirada      // 3 a una columna
           && taulell[1][coluSeleccionada] == laTirada
           && taulell[2][coluSeleccionada] == laTirada) {
         guanyador = true;
      } 
      guanyador = guanyaDiagonalPrincipalSaSe(laTirada, filaSeleccionada, coluSeleccionada, guanyador); 
      guanyador = guanyaDiagonalSecundariaSaSe(laTirada, filaSeleccionada, coluSeleccionada, guanyador);
         return guanyador;
   }

private boolean guanyaDiagonalSecundariaSaSe(Cella laTirada, int filaSeleccionada, int coluSeleccionada,
		boolean guanyador) {
	if (filaSeleccionada + coluSeleccionada == 2  // 3 a diagonal secundaria
           && taulell[0][2] == laTirada
           && taulell[1][1] == laTirada
           && taulell[2][0] == laTirada) {
         guanyador = true;
      }
	return guanyador;
}

private boolean guanyaDiagonalPrincipalSaSe(Cella laTirada, int filaSeleccionada, int coluSeleccionada,
		boolean guanyador) {
	if (filaSeleccionada == coluSeleccionada            // 3 a diagonal principal
           && taulell[0][0] == laTirada
           && taulell[1][1] == laTirada
           && taulell[2][2] == laTirada) {
         guanyador = true;
        }
	return guanyador;
}
 
   /*
    *  EspaiTreball és una classe interna (amplia JPanel) que es
    *    fa servir per dibuixar gràfics personalitzats.
    */
   class EspaiTreball extends JPanel {
      @Override
      public void paintComponent(Graphics g) {  // invocar mitjançant repaint()
         super.paintComponent(g);    // omplir fons (background)
         setBackground(Color.WHITE); // canvia el seu color de fons (background)
 
         // Dibuixa les línies de quadrícula
         g.setColor(Color.LIGHT_GRAY);
         for (int i = 1; i < 3; ++i) {
            g.fillRoundRect(0, MIDA_CELLA * i - MEITAT_AMPLADA_QUADRAT,
                  AMPLADA_TAULA-1, AMPLADA_QUADRAT, AMPLADA_QUADRAT, AMPLADA_QUADRAT);
         }
         for (int j = 1; j < 3; ++j) {
            g.fillRoundRect(MIDA_CELLA * j - MEITAT_AMPLADA_QUADRAT, 0,
                  AMPLADA_QUADRAT, ALSSADA_TAULA-1, AMPLADA_QUADRAT, AMPLADA_QUADRAT);
         }
 
         // Dibuixa "les tirades" de totes les cel·les si no estan buides
         // Fa servir la classe Graphics2D que ens permet establir el traç del llapis
         Graphics2D graficEnDuesDimensions = (Graphics2D)g;
         // amplada de la ploma = 10
         graficEnDuesDimensions.setStroke(new BasicStroke(10,
               BasicStroke.CAP_ROUND,
               BasicStroke.JOIN_ROUND));  // Graphics2D exclusivament
         for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
               int x1 = j * MIDA_CELLA + MARGE_CELLA;
               int y1 = i * MIDA_CELLA + MARGE_CELLA;
               if (taulell[i][j] == Cella.CREU) {
                  graficEnDuesDimensions.setColor(Color.RED);
                  int x2 = (j + 1) * MIDA_CELLA - MARGE_CELLA;
                  int y2 = (i + 1) * MIDA_CELLA - MARGE_CELLA;
                  graficEnDuesDimensions.drawLine(x1, y1, x2, y2);
                  graficEnDuesDimensions.drawLine(x2, y1, x1, y2);
               } else if (taulell[i][j] == Cella.CERCLE) {
                  graficEnDuesDimensions.setColor(Color.BLUE);
                  graficEnDuesDimensions.drawOval(x1, y1, MIDA_CARACTER, MIDA_CARACTER);
               }
            }
         }
 
         // Mostra el missatge de la barra d'estat
         if (estatActual == EstatDelJoc.EN_JOC) {
            barraDEstat.setForeground(Color.BLACK);
            if (jugadorActual == Cella.CREU) {
               String textTornDeLesXsSaSe = "Torn de les X's";
			barraDEstat.setText(textTornDeLesXsSaSe);
            } else {
               String textTornDeLesOsSaSe = "Torn de les O's";
			barraDEstat.setText(textTornDeLesOsSaSe);
            }
         } else {
			String textSegueixSaSe = "Pitja per tornar a jugar!";
			if (estatActual == EstatDelJoc.EMPAT) {
			    barraDEstat.setBackground(Color.MAGENTA);
			    barraDEstat.setForeground(Color.RED);
			    barraDEstat.setText("Hi ha empat!" + textSegueixSaSe);
			 } else if (estatActual == EstatDelJoc.GUANYA_CREU) {
			    barraDEstat.setForeground(Color.RED);
			    barraDEstat.setText("Guanyen les 'X'!" + textSegueixSaSe);
			 } else if (estatActual == EstatDelJoc.GUANYA_CERCLE) {
			    barraDEstat.setForeground(Color.RED);
			    barraDEstat.setText("Guanyen les 'O'!" + textSegueixSaSe);
			 }
		}
      }
   }
}