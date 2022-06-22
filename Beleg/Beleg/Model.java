package Beleg.Beleg;

import java.awt.*;

public class Model {
		
		  View v;
		  boolean farbe = true;
		  int threads = 10;
		  final int max_iter = 5000;
		  final double max_betrag2 = 35;
		  int xpix, ypix;
		  double xmin, xmax, ymin, ymax;
		  int[][] bildIter; // Matrix der Iterationszahl, t.b.d.
		  Color[][] bild;

		  final int[][] farben = {
		    {1, 255, 255, 255}, // Hohe Iterationszahlen sollen hell,
		    {30, 10, 255, 40}, //
		    {300, 10, 10, 40}, // die etwas niedrigeren dunkel,
		    {500, 205, 60, 40}, // die "Spiralen" rot
		    {850, 120, 140, 255}, // und die "Arme" hellblau werden.
		    {1000, 50, 30, 255}, // Innen kommt ein dunkleres Blau,
		    {1100, 0, 255, 0}, // dann grelles GrÃ¼n
		    {1997, 20, 70, 20}, // und ein dunkleres GrÃ¼n.
		    {max_iter, 0, 0, 0}
		  }; // Der Apfelmann wird schwarz.

		  public Model(View v) {
		    this.v = v;
			
		  }

		  public void setParameter(int xpix, int ypix) {
		    this.xpix = xpix;
		    this.ypix = ypix;
		    bildIter = new int[xpix][ypix]; // Matrix der Iterationszahl, t.b.d.
		    bild = new Color[xpix][ypix];
		  }

		  
		/**
		 * @param iter Iterationszahl
		 * @return Farbwert nsmooth = n + 1 - Math.log(Math.log(zn.abs()))/Math.log(2)
		 *     Color.HSBtoRGB(0.95f + 10 * smoothcolor ,0.6f,1.0f);
		 */
		public Color farbwert(int iter) {
			if (!farbe) {
			  if (iter == max_iter) return Color.BLACK;
			  else return Color.RED;
			}
			int[] F = new int[3];
			for (int i = 1; i < farben.length - 1; i++) {
			  if (iter < farben[i][0]) {
				int iterationsInterval = farben[i - 1][0] - farben[i][0];
				double gewichtetesMittel = (iter - farben[i][0]) / (double) iterationsInterval;
	  
				for (int f = 0; f < 3; f++) {
				  int farbInterval = farben[i - 1][f + 1] - farben[i][f + 1];
				  F[f] = (int) (gewichtetesMittel * farbInterval) + farben[i][f + 1];
				}
				return new Color(F[0], F[1], F[2]);
			  }
			}
			return Color.BLACK;
		  }
		
}
          