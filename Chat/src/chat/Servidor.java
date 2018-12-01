package chat;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Servidor extends JFrame {
	private JTextField introducircampo; // recibe entrada mensaje del usuario
	private JTextArea sizepantalla; // muestra la info al usuario
	private ObjectOutputStream salida; // salida al cliente
	private ObjectInputStream entrada; // entrada al cliente
	private ServerSocket server; // socket server
	private Socket connection; // conexión al cliente
	private int counter = 1; // contador de conexiones

	public Servidor() {
		super("Servidor");

		introducircampo = new JTextField(); // crea introducircampo
		introducircampo.setEditable(false);
		introducircampo.addActionListener(new ActionListener() {
			// envía mensaje a cliente
			public void actionPerformed(ActionEvent evento) {
				enviarDatos(evento.getActionCommand());
				introducircampo.setText("");
			}
		} 
		); 

		add(introducircampo, BorderLayout.NORTH);

		sizepantalla = new JTextArea(); // crea objeto sizepantalla
		add(new JScrollPane(sizepantalla), BorderLayout.CENTER);

		setSize(300, 150);
		setVisible(true); 
	}

	
	public void ejecutarServidor() {
		try // establece servidor para recibir conexiones
		{
			server = new ServerSocket(12345, 100); 

			while (true) {
				try {
					esperarConexion(); // espera conexión
					obtenerFlujos(); // obtiene la entrada y salida
					procesarConexion();
				} 
				catch (EOFException excepcionEOF) {
					mostrarMensaje("\nServidor termino la conexion");
				} 
				finally {
					cerrarConexion(); 
					counter++;
				} 
			} 
		} 
		catch (IOException exepcionES) {
			exepcionES.printStackTrace();
		} 
	} 

	private void esperarConexion() throws IOException {
		mostrarMensaje("Esperando una conexion\n");
		connection = server.accept(); 
		mostrarMensaje("Conexion " + counter + " recibida de: " + connection.getInetAddress().getHostName());
	} 

	private void obtenerFlujos() throws IOException {
		salida = new ObjectOutputStream(connection.getOutputStream());
		salida.flush(); 

		entrada = new ObjectInputStream(connection.getInputStream());

		mostrarMensaje("\nSe obtuvieron los flujos de E/S\n");
	} 

	private void procesarConexion() throws IOException {
		String mensaje = "Conexion exitosa";
		enviarDatos(mensaje); // envía mensaje del String mensaje
		setTextFieldEditable(true);
		do
		{try{
				mensaje = (String) entrada.readObject(); // lee el nuevo mensaje
				mostrarMensaje("\n" + mensaje); // muestra el mensaje
			}
			catch (ClassNotFoundException excepcionClaseNoEncontrada) {
				mostrarMensaje("\nSe recibió un tipo de objeto desconocido");
			} 

		} while (!mensaje.equals("CLIENTE>>> TERMINAR")); //Cuando se escribe TERMINAR de parte del cliente se cierra la conexión con el servidor
	}

	private void cerrarConexion() {
		mostrarMensaje("\nConexión Terminada\n");
		setTextFieldEditable(false);
		try {
			salida.close(); // cierra flujo 
			entrada.close(); // cierra flujo
			connection.close(); // cierra la conexión
		}

		catch (IOException exepcionES) {
			exepcionES.printStackTrace();
		} 
	}

	private void enviarDatos(String mensaje) { //Para enviar mensajes de parte del cliente
		try{
			salida.writeObject("SERVIDOR>>> " + mensaje);
			salida.flush(); // envía la salida al cliente
			mostrarMensaje("\nSERVIDOR>>> " + mensaje);
		}
		catch (IOException exepcionES) {
			sizepantalla.append("\nError al escribir objeto");
		} 
	} 
	
	private void mostrarMensaje(final String mensajeAMostrar) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() // actualiza areaPantalla
			{
				sizepantalla.append(mensajeAMostrar); // adjunta el mensaje
			}
		}
		);
	} 
	private void setTextFieldEditable(final boolean editable) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() // da la propiedad editar de introducircampo
			{
				introducircampo.setEditable(editable);
			} 
		} 
		);
	} 
} 
