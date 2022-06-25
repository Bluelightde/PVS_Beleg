package Beleg;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
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

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Client {
	public static void main(String args[]) throws Exception {
		Presenter p = new Presenter();
		View v = new View(p);
		Model m = new Model(v);
		p.setModelAndView(m, v);
		p.apfel();
	}
}

class Presenter implements ActionListener {
	Socket client;
	protected Model m;
	protected View v;

	double xmin = -1.666, xmax = 1, ymin = -1, ymax = 1; // Parameter des Ausschnitts
	// static double cr = -0.3735, ci = 0.655;
	private double cr = -0.743643887036151;
	private double ci = 0.131825904205330;
	double zoomRate = 1.5;
	int xpix = 640, ypix = 480;

	public Object actionPerformed;

	public byte[] concat(byte[]... arrays) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		if (arrays != null) {
			Arrays.stream(arrays).filter(Objects::nonNull)
					.forEach(array -> out.write(array, 0, array.length));
		}
		return out.toByteArray();
	}

	public byte[] toByteArray(double value) {
		byte[] bytes = new byte[8];
		ByteBuffer.wrap(bytes).putDouble(value);
		return bytes;
	}

	public int[] convert(byte buf[]) {
		int intArr[] = new int[buf.length / 4];
		int offset = 0;
		for (int i = 0; i < intArr.length; i++) {
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
			coord = concat(coord1, coord2, coord3, coord4);

			OutputStream out = (OutputStream) client.getOutputStream();
			DataOutputStream dos = new DataOutputStream(out);
			dos.write(coord, 0, coord.length);

			double xdim = xmax - xmin;
			double ydim = ymax - ymin;
			xmin = cr - xdim / 2 / zoomRate;
			xmax = cr + xdim / 2 / zoomRate;
			ymin = ci - ydim / 2 / zoomRate;
			ymax = ci + ydim / 2 / zoomRate;

			DataInputStream in = new DataInputStream(client.getInputStream());
			int lengthr = 1228800;
			byte[] message = new byte[lengthr]; // the well known size
			in.readFully(message);

			int[] values = convert(message);
			setImage(values);

		}
		client.close();

	}

	@Override
	public synchronized void actionPerformed(ActionEvent e) {
		cr = Double.parseDouble(v.tfr.getText());
		ci = Double.parseDouble(v.tfi.getText());
		notifyAll();
	}

	public void setImage(int[] image) {
		m.setImage(image);
	}

}

class View {
	private Presenter p;
	private Panel ap = new Panel();
	public JTextField tfi;
	public JTextField tfr;
	int xpix, ypix;
	BufferedImage image;

	public View(Presenter p) {
		this.p = p;
	}

	public void setDim(int xpix, int ypix) {
		this.xpix = xpix;
		this.ypix = ypix;
		image = new BufferedImage(xpix, ypix, BufferedImage.TYPE_INT_RGB);
		initView();
	}

	private void initView() {
		JFrame f = new JFrame();
		JPanel sp = new JPanel(new FlowLayout());
		JButton sb = new JButton("Start");
		sb.addActionListener(p);

		tfr = new JTextField("-0.743643887037151");
		tfi = new JTextField("0.131825904205330");
		sp.add(tfr);
		sp.add(tfi);
		sp.add(sb);

		// f.setLayout( new BorderLayout() );
		f.add(ap, BorderLayout.CENTER);
		f.add(sp, BorderLayout.SOUTH);
		f.setSize(xpix, ypix + 100);
		f.setVisible(true);
	}

	public void update(Color[][] c) {
		for (int y = 0; y < ypix; y++) {
			for (int x = 0; x < xpix; x++) {
				if (c[x][y] != null)
					image.setRGB(x, y, c[x][y].getRGB());
			}
		}
		ap.repaint();
	}

	class Panel extends JPanel {
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(image, 0, 0, null); // see javadoc
		}
	}
}

class Model {
	int[] message;
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
			{ 1, 255, 255, 255 }, // Hohe Iterationszahlen sollen hell,
			{ 30, 10, 255, 40 }, //
			{ 300, 10, 10, 40 }, // die etwas niedrigeren dunkel,
			{ 500, 205, 60, 40 }, // die "Spiralen" rot
			{ 850, 120, 140, 255 }, // und die "Arme" hellblau werden.
			{ 1000, 50, 30, 255 }, // Innen kommt ein dunkleres Blau,
			{ 1100, 0, 255, 0 }, // dann grelles GrÃ¼n
			{ 1997, 20, 70, 20 }, // und ein dunkleres GrÃ¼n.
			{ max_iter, 0, 0, 0 }
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

	public void setImage(int[] message) {
		this.message = message;
		int cont = 0;
		for (int y = 0; y < ypix; y++) {
			for (int x = 0; x < xpix; x++) {
				int actual = message[cont];
				Color color = farbwert(actual);
				bild[x][y] = color;
				cont++;
			}
		}
		v.update(bild);
	}

	/**
	 * @param iter Iterationszahl
	 * @return Farbwert nsmooth = n + 1 - Math.log(Math.log(zn.abs()))/Math.log(2)
	 *         Color.HSBtoRGB(0.95f + 10 * smoothcolor ,0.6f,1.0f);
	 */

	Color farbwert(int iter) {
		if (!farbe) {
			if (iter == max_iter)
				return Color.BLACK;
			else
				return Color.RED;
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
