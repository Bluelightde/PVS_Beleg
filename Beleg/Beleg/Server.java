package Beleg;
import java.awt.Color;

public class Server {
/** Erzeuge ein komplettes Bild mittles Threads */
int threads = 10;
static int xpix;
static int ypix;
private static double xmin;
private static double xmax;
private static double ymin;
private static double ymax;
static int[][] bildIter; // Matrix der Iterationszahl, t.b.d.
static Color[][] bild;
final static int max_iter = 5000;
final static double max_betrag2 = 35;

/** Komplette Berechnung aller Bilder */
synchronized void apfel() {
    try {
      wait();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    for (int i = 1; i < 70; i++) { // Iterationen bis zum Endpunkt
      System.out.println(i + " VergrÃ¶ÃŸerung: " + 2.6 / (xmax - xmin) + " xmin: " + xmin + " xmax: " + xmax);
      apfel_bild(xmin, xmax, ymin, ymax);
      double xdim = xmax - xmin;
      double ydim = ymax - ymin;
      xmin = cr - xdim / 2 / zoomRate;
      xmax = cr + xdim / 2 / zoomRate;
      ymin = ci - ydim / 2 / zoomRate;
      ymax = ci + ydim / 2 / zoomRate;
    }
  }
public void apfel_bild(double xmin, double xmax, double ymin, double ymax) {
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
  static class ApfelThread extends Thread {
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

    public static void main(String[] args) {
    }
  }
