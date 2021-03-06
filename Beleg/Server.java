package Beleg;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class Server {

  static int xpix = 640;
  static int ypix = 480;
  private static int[][] bildIter = new int[xpix][ypix]; // Matrix der Iterationszahl, t.b.d.

  public static double toDouble(byte[] bytes) {
    return ByteBuffer.wrap(bytes).getDouble();
  }

  static byte[] integersToBytes(int[] values) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);
    for (int i = 0; i < values.length; ++i) {
      dos.writeInt(values[i]);
    }

    return baos.toByteArray();
  }

  public static void main(String[] args) throws IOException {

    try (ServerSocket server = new ServerSocket(4000)) {
      Socket clntSock = server.accept(); // Socket connected to the client
      SocketAddress clientAddress = clntSock.getRemoteSocketAddress();
      System.out.println("Handling client at " + clientAddress);

      DataInputStream in = new DataInputStream(clntSock.getInputStream());
      int lengthr = 32;
      byte[] message = new byte[lengthr]; // the well known size

      for (int i = 1; i < 70; i++) {
        in.readFully(message);

        byte[] d1 = new byte[8];
        byte[] d2 = new byte[8];
        byte[] d3 = new byte[8];
        byte[] d4 = new byte[8];
        d1 = Arrays.copyOfRange(message, 0, 8);
        d2 = Arrays.copyOfRange(message, 8, 16);
        d3 = Arrays.copyOfRange(message, 16, 24);
        d4 = Arrays.copyOfRange(message, 24, 32);
        double xmin = toDouble(d1);
        double xmax = toDouble(d2);
        double ymin = toDouble(d3);
        double ymax = toDouble(d4);
        apfel_bild(xmin, xmax, ymin, ymax, clntSock);
      }
    }
  }

  // Erzeuge ein komplettes Bild mittles Threads
  static void apfel_bild(double xmin, double xmax, double ymin, double ymax, Socket clntSock) throws IOException {

    int threads = 10;
    ApfelThread[] th = new ApfelThread[threads];
    int ypix = 480;
    int b = ypix / threads;
    int s = 0;
    for (int i = 0; i < threads; i++) {
      th[i] = new ApfelThread(s, s + b, xmin, xmax, ymin, ymax);
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

    int[] send = new int[xpix * ypix];
    int cont = 0;

    for (int y = 0; y < bildIter[0].length; y++) {
      for (int x = 0; x < bildIter.length; x++) {
        send[cont] = bildIter[x][y];
        cont++;
      }
    }

    byte[] tosend = integersToBytes(send);
    OutputStream out = (OutputStream) clntSock.getOutputStream();
    DataOutputStream dos = new DataOutputStream(out);
    dos.write(tosend, 0, tosend.length);
  }

  // Threads and writing to arrays
  // http://docs.oracle.com/javase/specs/jls/se7/html/jls-17.html#jls-17.6

  /** @author jvogt lokale Klasse zum Thread-Handling */

  static class ApfelThread extends Thread {
    int y_sta, y_sto;
    double x_min, x_max, y_min, y_max;

    public ApfelThread(int y_start, int y_stopp, double xmin, double xmax, double ymin, double ymax) {
      this.y_sta = y_start;
      this.y_sto = y_stopp;
      this.x_min = xmin;
      this.x_max = xmax;
      this.y_min = ymin;
      this.y_max = ymax;
    }

    public void run() {
      double c_re, c_im;
      for (int y = y_sta; y < y_sto; y++) {
        c_im = y_min + (y_max - y_min) * y / ypix;
        for (int x = 0; x < xpix; x++) {
          c_re = x_min + (x_max - x_min) * x / xpix;
          int iter = calc(c_re, c_im);
          bildIter[x][y] = iter;
        }
      }
    }

    /**
     * @param cr Realteil
     * @param ci Imagin??rteil
     * @return Iterationen
     */

    public int calc(double cr, double ci) {
      int iter;
      double zr, zi, zr2 = 0, zi2 = 0, zri = 0, betrag2 = 0;
      // z_{n+1} = z??_n + c
      // z?? = x?? - y?? + i(2xy)
      // |z|?? = x?? + y??
      final int max_iter = 5000;
      final double max_betrag2 = 35;
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

  }
}
