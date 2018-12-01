package chat;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Cliente extends JFrame {

	private JTextField campointro; // info del usuario
	private JTextArea sizePantalla; // muestra info al usuario
	private ObjectOutputStream salida; // salida hacia el server
	private ObjectInputStream entrada; // entrada del server
	private String mensaje = ""; // mensaje del server
	private String serverChat; // servidor para esta aplicación
	private Socket client; // socket para comunicarse con el server

	public Cliente(String host) {
		super("Cliente");
		serverChat = host; //server al que se conecta este cliente
		campointro = new JTextField();
		campointro.setEditable(false);
		campointro.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evento) {
				enviarDatos(evento.getActionCommand());
				campointro.setText(""); // envía el mensaje al servidor
			} 
		} 
		);
		
		add(campointro, BorderLayout.NORTH);

		sizePantalla = new JTextArea();
		add(new JScrollPane(sizePantalla), BorderLayout.CENTER);
		setSize(300, 150);
		setVisible(true); 
	}

	public void ejecutarCliente() {
		try { // se conecta al server, flujos, procesa conexión
			conectarAlServidor();
			obtenerFlujos(); 
			procesarConexion();
		}
		catch (EOFException excepcionEOF) {
			mostrarMensaje("\nEl cliente ha finalizado la conexion");
		}
		catch (IOException excepcionES) {
			excepcionES.printStackTrace();
		}
		finally {
			cerrarConexion(); 
		} 
	} 
	
	private void conectarAlServidor() throws IOException {
		mostrarMensaje("Tratando de realizar la conexión\n");
		client = new Socket(InetAddress.getByName(serverChat), 12345);
		mostrarMensaje("Conectado a: " + client.getInetAddress().getHostName());
	}

		private void obtenerFlujos() throws IOException { // flujo de salida para objetos
		salida = new ObjectOutputStream(client.getOutputStream());
		salida.flush(); 

		entrada = new ObjectInputStream(client.getInputStream()); // establece flujo de salida para los objetos
		mostrarMensaje("\nSe obtuvo los flujos de E/S\n");
	}

	private void procesarConexion() throws IOException { // campointro para que el cliente envíe mensajes
		establecerCampoEditable(true);
		do {
			try{
				mensaje = (String) entrada.readObject(); // lee el nuevo mensaje
				mostrarMensaje("\n" + mensaje); // muestra el mensaje
			}
			catch (ClassNotFoundException excepcionClaseNoEncontrada) {
				mostrarMensaje("nSe recibió un tipo de objeto desconocido");
			} 
		} while (!mensaje.equals("SERVIDOR>>> TERMINAR"));
	} 
	private void cerrarConexion() {
		mostrarMensaje("\nCerrando conexión");
		establecerCampoEditable(false); 
		try {
			salida.close(); // cierra lasalida
			entrada.close(); // cierra la entrada
			client.close();
		} 
		catch (IOException excepcionES) {
			excepcionES.printStackTrace();
		} 
	} 

	private void enviarDatos(String mensaje) {
		try{  // envía un objeto al server
			salida.writeObject("CLIENTE>>> " + mensaje);
			salida.flush();
			mostrarMensaje("\nCLIENTE>>> " + mensaje);
		} 
		catch (IOException excepcionES) {
			sizePantalla.append("\nError al escribir objeto");
		}
	} 

	private void mostrarMensaje(final String mensajeAMostrar) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() // actualiza objeto sizePantalla
			{
				sizePantalla.append(mensajeAMostrar);
			} 
		} 
		);
	} 

	private void establecerCampoEditable(final boolean editable) { // manipula a campointro en eventos
		SwingUtilities.invokeLater(new Runnable() {
			public void run()
			{
				campointro.setEditable(editable);
			} 
		}
		); 
	} 
} 
