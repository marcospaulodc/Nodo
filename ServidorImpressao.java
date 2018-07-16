package Trabalho5;
/**
 * @author Marcos Paulo de Castro
 */

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JOptionPane;

public class ServidorImpressao 
{
	public static void main(String[] args) throws IOException 
	{
		ServerSocket listenSocket = mIniciarServidor();
		
		while (true) 
		{
			//Aceitando novas conexões dos nodos
			Socket clientSocket = listenSocket.accept();
			System.out.println("Nodo: " + clientSocket.getInetAddress() + " requisitou o servidor");

			new ImprimirDemanda(clientSocket).start();
		}
	}

	private static ServerSocket mIniciarServidor() throws IOException 
	{
		String portaAcessoServidor = JOptionPane.showInputDialog("Informe a porta de entrada do Servidor de Impressão");
		int portaServidor = 12345;
		if(!portaAcessoServidor.equals(""))
		{
			try
			{
				portaServidor = Integer.parseInt(portaAcessoServidor);
			}
			catch (Exception e) 
			{
				JOptionPane.showMessageDialog(null, "Porta informada inválida. Por padrão, utilizaremos a pota 12345.");
			}
		}
		
		ServerSocket listenSocket = new ServerSocket(portaServidor);
		System.out.println("+--------------------------------------------------------+");
		System.out.println("|#### Servidor iniciado na porta: " + portaServidor + " com sucesso! ####|");
		System.out.println("+--------------------------------------------------------+");
		return listenSocket;
	}
}
