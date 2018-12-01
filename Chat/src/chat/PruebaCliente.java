package chat;

import javax.swing.JFrame;

public class PruebaCliente extends JFrame{
	public static void main( String args[] ) 
	{
	Cliente aplicacion; // declara la aplicación cliente
	if ( args.length == 0 )
	aplicacion = new Cliente( "127.0.0.1" ); //  Tiene que poner la IP de la PC que usará de servidor 
	else
	aplicacion = new Cliente( args[ 0 ] );
	aplicacion.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
	aplicacion.ejecutarCliente(); 
	} 
	} 

