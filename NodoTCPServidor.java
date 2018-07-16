package Trabalho5;
/**
 * @author Marcos Paulo de Castro
 */

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class NodoTCPServidor extends Thread
{
	NodoModel nodoModel;
	
	public NodoTCPServidor(NodoModel todosOsDadosNodo)
	{
		this.nodoModel = todosOsDadosNodo;
	}	
	
	public void run() 
	{
		try
		{
			int serverPort = nodoModel.listaEstruturaNodos.get(nodoModel.id).porta;
			ServerSocket listenSocket = new ServerSocket(serverPort);
			
			while(true)
			{
				Socket clientSocket = listenSocket.accept();
				System.out.println("Conexão estabelecida com: " + clientSocket.getInetAddress());
				new Conexao(clientSocket, nodoModel).start();
			}
		}
		catch(IOException e)
		{
			System.out.println("Listen: " + e.getMessage());
		}
	}
}