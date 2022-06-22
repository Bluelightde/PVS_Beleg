package Beleg.Beleg;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
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
		  Color[][] bild= new Color[xpix][ypix];

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
		public int[] convert(byte buf[]) {
			int intArr[] = new int[buf.length / 4];
			int offset = 0;
			for(int i = 0; i < intArr.length; i++) {
			   intArr[i] = (buf[3 + offset] & 0xFF) | ((buf[2 + offset] & 0xFF) << 8) |
						   ((buf[1 + offset] & 0xFF) << 16) | ((buf[0 + offset] & 0xFF) << 24);  
			offset += 4;
			}
			return intArr;
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
				// get the matrix
				// calculate the color
				// update the view
				
				double xdim = xmax - xmin;
				double ydim = ymax - ymin;
				xmin = cr - xdim / 2 / zoomRate;
				xmax = cr + xdim / 2 / zoomRate;
				ymin = ci - ydim / 2 / zoomRate;
				ymax = ci + ydim / 2 / zoomRate;

				DataInputStream in = new DataInputStream(client.getInputStream());
         		int lengthr = 307200;
          		byte[] message = new byte[lengthr]; // the well known size
				in.readFully(message);
				
				int cont =0;
				int [] values = convert(message);
				for (int m=0; m<ypix; m++){
					for (int l=0; l<xpix; l++){
						int actual = values[cont];
						Color colour = m.farbwert(actual);
						cont++;
					}
				}
				// hacer el vector 
				// bucle con el vector y meter estas dos cosas
				// Color pix = farbwert(iter); // Farbberechnung
				//if (iter == max_iter) pix = Color.RED; else pix = Color.WHITE;
				// v.image.setRGB(x, y, pix.getRGB()); // rgb
				//bild[x][y] = pix; 
			}

			client.close();
		}
		  @Override
		public synchronized void actionPerformed(ActionEvent e) {
		    cr = Double.parseDouble( v.tfr.getText() );
		    ci = Double.parseDouble( v.tfi.getText() );
		    notifyAll();
		}
	}
