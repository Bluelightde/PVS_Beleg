package Beleg;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Client {
	 static Presenter p = new Presenter();
	 static View v = new View(p);
	 static Model m = new Model(v);
	
	public static void main(String args[]) throws Exception {
		
		
		/*if (args.length < 4) { // Test para correcto # de args
			throw new IllegalArgumentException("Parameter(s): <HostServer> <ServerPort> <ini> <fin>");
			/*
			 * Argumentos: <Num> n?mero de servidores, y para cada uno de ellos: <ServerN>
			 * <PortN> direcci?n IP y puerto del servidor N-?simo
			 */
		// }
		
		/*
		 * TO-DO: lectura de argumentos
		 */

		// Creo un DatagramSocket para el cliente
		//DatagramSocket client = new DatagramSocket();

		// Leo los parametros del servidor
		//String ipserv = args[0];
		// int servPort = Integer.parseInt(args[1]);
		

		// Leo las coordenadas
		Presenter p = new Presenter();
    	View v = new View(p);
    	Model m = new Model(v);
		p.setModelAndView(m, v);
		
	
		 if (args.length < 4) { // Test para correcto # de args
				throw new IllegalArgumentException("Parameter(s): <HostServer> <ServerPort> <ini> <fin>");
			}

			/*
			 * Argumentos: <Num> n?mero de servidores, y para cada uno de ellos: <ServerN>
			 * <PortN> direcci?n IP y puerto del servidor N-?simo
			 */
			/*String host = args[0];
			int puerto = Integer.parseInt(args[1]);
			*/

			Socket client = new Socket("127.0.0.1",6000);

			double d = 65.43;
			byte[] output = new byte[8];
			long lng = Double.doubleToLongBits(d);
			for(int i = 0; i < 8; i++) output[i] = (byte)((lng >> ((7 - i) * 8)) & 0xff);


			byte [] coord = new byte [8];
			
			OutputStream out = client.getOutputStream(); 
			DataOutputStream dos = new DataOutputStream(out);
	
		
			dos.write(coord, 0, coord.length);

			client.close();
			*/
		// }

	}
	
	
}

