package Beleg;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Client {
	 static Presenter p = new Presenter();
	 static View v = new View(p);
	 static Model m = new Model(v);
	
	public static void main(String args[]) throws Exception {
		
		
		if (args.length < 4) { // Test para correcto # de args
			throw new IllegalArgumentException("Parameter(s): <HostServer> <ServerPort> <ini> <fin>");
			/*
			 * Argumentos: <Num> n?mero de servidores, y para cada uno de ellos: <ServerN>
			 * <PortN> direcci?n IP y puerto del servidor N-?simo
			 */
		}
		/*
		 * TO-DO: lectura de argumentos
		 */

		// Creo un DatagramSocket para el cliente
		DatagramSocket client = new DatagramSocket();

		// Leo los parametros del servidor
		String ipserv = args[0];
		int servPort = Integer.parseInt(args[1]);
		

		// Leo las coordenadas
		 p.setView(v);
		 p.apfel();
		
		// Asigno la direccion del servidor como destino del mensaje
		 if (args.length < 4) { // Test para correcto # de args
				throw new IllegalArgumentException("Parameter(s): <HostServer> <ServerPort> <ini> <fin>");

			}

			/*
			 * Argumentos: <Num> n?mero de servidores, y para cada uno de ellos: <ServerN>
			 * <PortN> direcci?n IP y puerto del servidor N-?simo
			 */
			String host = args[0];
			int puerto = Integer.parseInt(args[1]);

			Socket cliente = new Socket(host, puerto);
			System.out.println("Connected to server...sending echo string");

			/*
			 * TO-DO: lectura de argumentos de entrada
			 */
			// numero aleatorio para el ident
			/*
			 * TO-DO: 
			 * ****** Utilización ADECUADA de SOCKETS TCP ***** 
			 * Generación del mensaje a enviar 
			 * Conexión con el servidor y envío del mensaje 
			 * Esperar respuesta 
			 * Mostrar por pantalla la lista de primos
			 */

			_tfm.send(menviar, cliente);

			Message definitivo = _tfm.receive(cliente);

			cliente.close();
		}

	}
	
	
}

