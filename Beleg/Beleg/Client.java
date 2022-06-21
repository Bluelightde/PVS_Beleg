package Beleg.Beleg;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Client {
	public static void main(String args[]) throws Exception {
		Presenter p = new Presenter();
		View v = new View(p);
		Model m = new Model(v);
		p.setModelAndView(m, v);

		double cr = p.getCr();
		double ci = p.getCi();

		byte[] coord1 = new byte[8];
		byte[] coord2 = new byte[8];

		long lng1 = Double.doubleToLongBits(cr);
		long lng2 = Double.doubleToLongBits(ci);

		for (int i = 0; i < 8; i++) {
			coord1[i] = (byte) ((lng1 >> ((7 - i) * 8)) & 0xff);
			coord2[i] = (byte) ((lng2 >> ((7 - i) * 8)) & 0xff);
		}

		byte[] coord = new byte[16];
		System.arraycopy(coord1, 0, coord, 0, coord1.length);
		System.arraycopy(coord2, 0, coord, 0, coord2.length);

		System.out.println(coord);

		Socket client = new Socket("127.0.0.1", 6000);

		OutputStream out = client.getOutputStream();
		DataOutputStream dos = new DataOutputStream(out);

		dos.write(coord, 0, coord.length);
		client.close();

	}

}
