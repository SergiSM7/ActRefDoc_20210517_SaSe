import javax.swing.SwingUtilities;

public class JocTresEnRatllaSaSe {

	public static void main(String[] args) {
	      // Executa GUI al fil (thread) de l'Event-Dispatching per seguretat
	      SwingUtilities.invokeLater(new Runnable() {
	         @Override
	         public void run() {
	            new TresEnRatlla(); // Crida al costructor
	         }
	      });
	   }

}
