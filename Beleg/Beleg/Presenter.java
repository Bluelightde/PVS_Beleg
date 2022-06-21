package Beleg.Beleg;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;


public class Presenter implements ActionListener {
		  Socket client;
		  protected Model m;
		  protected View v;

		  // I do not understand at all these parameters
		  double xmin = -1.666, xmax = 1, ymin = -1, ymax = 1; // Parameter des Ausschnitts
		  // static double  cr = -0.3735,  ci = 0.655;
		  private double cr = -0.743643887036151;
		  private double ci = 0.131825904205330;
		  double zoomRate = 1.5;
		  int xpix = 640, ypix = 480;
        public Object actionPerformed;
		
		public static byte[] concat(byte[]... arrays) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			if (arrays != null) {
				Arrays.stream(arrays).filter(Objects::nonNull)
						.forEach(array -> out.write(array, 0, array.length));
			}
			return out.toByteArray();
		}
		public static byte[] toByteArray(double value) {
			byte[] bytes = new byte[8];
			ByteBuffer.wrap(bytes).putDouble(value);
			return bytes;
		}
		public void setModelAndView(Model m, View v) {
		    this.m = m;
		    this.v = v;
		    v.setDim(xpix, ypix);
		    m.setParameter(xpix, ypix);
		}
		synchronized void apfel() throws UnknownHostException, IOException {
			System.out.println("holaaa");
			try {
			  wait();
			} catch (InterruptedException e) {
			  e.printStackTrace();
			}
			client = new Socket("127.0.0.1", 4000);
			for (int i = 1; i < 70; i++) { // Iterationen bis zum Endpunkt
				System.out.println(i + " Vergrößerung: " + 2.6 / (xmax - xmin) + " xmin: " + xmin + " xmax: " + xmax);
				byte[] coord1 = toByteArray(xmin);
				byte[] coord2 = toByteArray(xmax);
				byte[] coord3 = toByteArray(ymin);
				byte[] coord4 = toByteArray(ymax);

				byte[] coord = new byte[32];
				coord= concat(coord1, coord2, coord3, coord4);

				OutputStream out = (OutputStream) client.getOutputStream();
				DataOutputStream dos = new DataOutputStream(out);
				dos.write(coord, 0, coord.length);
				System.out.println("long : " +coord.length);
			  // socket needs to be here
			  // send xmin, xmax, ymin, ymax to the server
			  // m.apfel_bild(xmin, xmax, ymin, ymax); // this in the server 
			  double xdim = xmax - xmin;
			  double ydim = ymax - ymin;
			  xmin = cr - xdim / 2 / zoomRate;
			  xmax = cr + xdim / 2 / zoomRate;
			  ymin = ci - ydim / 2 / zoomRate;
			  ymax = ci + ydim / 2 / zoomRate;
			}
			client.close();
		}
		  @Override
		public synchronized void actionPerformed(ActionEvent e) {
		    cr = Double.parseDouble( v.tfr.getText() );
		    ci = Double.parseDouble( v.tfi.getText() );
		    notifyAll();
		}

		 /* public double setCr(){
			return cr;
		  }
		  public double getCr(){
			return cr;
		  }
		  public double setCi(){
			return ci;
		  }
		  public double getCi(){
			return ci;
		  }*/
		}
