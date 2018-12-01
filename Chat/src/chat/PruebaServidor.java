package chat;

import javax.swing.JFrame;

public class PruebaServidor {
	public static void main(String args[]) {
		Servidor aplicacion = new Servidor(); // crea el servidor
		aplicacion.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		aplicacion.ejecutarServidor(); // ejecuta la aplicación servidor
	}
}
