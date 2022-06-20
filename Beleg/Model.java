package Beleg;

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

		  /** Erzeuge ein komplettes Bild mittles Threads */
		  void apfel_bild(double xmin, double xmax, double ymin, double ymax) {
		    this.xmin = xmin;
		    this.xmax = xmax;
		    this.ymin = ymin;
		    this.ymax = ymax;

		    ApfelThread[] th = new ApfelThread[threads];
		    int b = ypix / threads;
		    int s = 0;
		    for (int i = 0; i < threads; i++) {
		      th[i] = new ApfelThread(s, s + b);
		      s = s + b;
		      th[i].start();
		    }
		    // warte auf das Ende aller Threads
		    for (int i = 0; i < threads; i++) {
		      try {
		        th[i].join(100);
		        // v.update(bild); // Zwischenergebnisse alle 100 ms
		      } catch (InterruptedException ignored) {
		      } // nichts
		    }
		    for (int i = 0; i < threads; i++)
		      try {
		        th[i].join();
		      } catch (InterruptedException ignored) {
		      } // nichts
		    v.update(bild);	// cambiarlo en el Cliente
		  }

		  // Threads and writing to arrays
		  // http://docs.oracle.com/javase/specs/jls/se7/html/jls-17.html#jls-17.6

		  /** @author jvogt lokale Klasse zum Thread-Handling */
		  class ApfelThread extends Thread {
		    int y_sta, y_sto;

		    public ApfelThread(int y_start, int y_stopp) {
		      this.y_sta = y_start;
		      this.y_sto = y_stopp;
		    }

		    public void run() {
		      double c_re, c_im;
		      for (int y = y_sta; y < y_sto; y++) {
		        c_im = ymin + (ymax - ymin) * y / ypix;

		        for (int x = 0; x < xpix; x++) {
		          c_re = xmin + (xmax - xmin) * x / xpix;
		          int iter = calc(c_re, c_im);
		          bildIter[x][y] = iter;
		          Color pix = farbwert(iter); // Farbberechnung
		          // if (iter == max_iter) pix = Color.RED; else pix = Color.WHITE;
		          // v.image.setRGB(x, y, pix.getRGB()); // rgb
		          bild[x][y] = pix;
		        }
		      }
		    }

		    /**
		     * @param cr Realteil
		     * @param ci ImaginÃ¤rteil
		     * @return Iterationen
		     */
		    public int calc(double cr, double ci) {
		      int iter;
		      double zr, zi, zr2 = 0, zi2 = 0, zri = 0, betrag2 = 0;
		      //  z_{n+1} = zÂ²_n + c
		      //  zÂ²  = xÂ² - yÂ² + i(2xy)
		      // |z|Â² = xÂ² + yÂ²
		      for (iter = 0; iter < max_iter && betrag2 <= max_betrag2; iter++) {
		        zr = zr2 - zi2 + cr;
		        zi = zri + zri + ci;

		        zr2 = zr * zr;
		        zi2 = zi * zi;
		        zri = zr * zi;
		        betrag2 = zr2 + zi2;
		      }
		      return iter;
		    }

		    /**
		     * @param iter Iterationszahl
		     * @return Farbwert nsmooth = n + 1 - Math.log(Math.log(zn.abs()))/Math.log(2)
		     *     Color.HSBtoRGB(0.95f + 10 * smoothcolor ,0.6f,1.0f);
		     */
		    Color farbwert(int iter) {
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
		  } // ApfelThread
          }