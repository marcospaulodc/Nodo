package Trabalho5;
/**
 * @author Marcos Paulo de Castro
 */

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class NodoUDPCliente 
{
	NodoModel todosOsDadosNodo;
	DatagramSocket datagramSocket = null;
	String ip = "";
	int serverPort = 0;
	String msg = "";

	public NodoUDPCliente(NodoModel todosOsDadosNodo, String ip, int portaServidor, String msg) 
	{
		this.todosOsDadosNodo = todosOsDadosNodo;
		this.ip = ip;
		this.serverPort = portaServidor;
		this.msg = msg;
	}

	//Ao ser inicializado, envia uma mensagem e aguarda por 2 segundo a resposta.
	public void run() 
	{
		try 
		{
			datagramSocket = new DatagramSocket();
			InetAddress host = InetAddress.getByName(ip);
			DatagramPacket request = new DatagramPacket(msg.getBytes(), msg.length(), host, serverPort);
			
			datagramSocket.send(request);
			byte[] buffer = new byte[100000];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);

			datagramSocket.setSoTimeout(2000);
			datagramSocket.receive(reply);
			String msgEntrada = new String(buffer, 0, reply.getLength());

			//Se recebeu a mensagem OK, o nodo recebeu ELECTION e este nodo ja nao é mais o lider
			if (msgEntrada.equalsIgnoreCase("OK")) 
			{
				todosOsDadosNodo.respostas++;
				todosOsDadosNodo.lider = false;
			}

		} 
		catch (SocketException e) 
		{

		} 
		catch (IOException e) 
		{

		} 
		finally 
		{
			if (datagramSocket != null)
				datagramSocket.close();
		}
	}
}
