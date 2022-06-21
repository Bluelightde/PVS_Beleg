package Beleg.Beleg;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;

public class Client {

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
	public static void main(String args[]) throws Exception {
		Presenter p = new Presenter();
		View v = new View(p);
		Model m = new Model(v);
		p.setModelAndView(m, v);

		double cr = p.getCr();
		double ci = p.getCi();

		System.out.println("cr "+cr);
		System.out.println("ci "+ci);
		
		byte[] coord1 = toByteArray(cr);
		byte[] coord2 = toByteArray(ci);

		byte[] coord = new byte[16];
		coord= concat(coord1, coord2);

		for (int i = 0; i < 16; i++) {
			System.out.println(coord[i]);
		}

		Socket client = new Socket("127.0.0.1", 4000);

		OutputStream out = client.getOutputStream();
		DataOutputStream dos = new DataOutputStream(out);

		dos.write(coord, 0, coord.length);
		System.out.println("long" +coord.length);
		client.close();

	}

}
