package Trabalho5;
/**
 * @author Marcos Paulo de Castro
 */

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class NodoUDPServidor extends Thread
{
	NodoModel todosOsDadosNodo;
	DatagramSocket aSocket = null;
	String msg = "Servidor respondendo...";
	byte[] msg_byte = msg.getBytes();
	int port = 0;

	NodoUDPServidor(NodoModel model)
	{
		this.todosOsDadosNodo = model;		
		this.port = model.listaEstruturaNodos.get(model.id).porta;
	}

	public void run() 
	{
		try 
		{
			//Porta inicial 12311
			aSocket = new DatagramSocket(port);
			byte[] buffer = new byte[100000];

			while (true) 
			{
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);

				aSocket.receive(request);

				String msgEntrada = new String(buffer, 0, request.getLength());

				//Se na variavel msgEntrada chega a mensagem ELECTION, uma eleicao está ocorrendo
				if (msgEntrada.equalsIgnoreCase("ELECTION")) 
				{
					//Iniciando a eleicao
					if (!todosOsDadosNodo.eleicao) 
					{
						todosOsDadosNodo.nodoFluxo.mIniciarEleicao();
					}
					//Seta a variavel eleicao para true: ocorrendo a eleicao
					todosOsDadosNodo.eleicao = true;

					msg = "OK";
					msg_byte = msg.getBytes();

					DatagramPacket reply = new DatagramPacket(msg_byte, msg_byte.length, request.getAddress(),
							request.getPort());

					//Node que iniciou a eleicao receberá um OK
					aSocket.send(reply);
				}

				//Se receber a mensagem COORDINATOR o coordenador foi eleito
				else if (msgEntrada.equalsIgnoreCase("COORDINATOR")) 
				{
					//Atualiza a variavel eleicao para false, eleicao encerrada
					todosOsDadosNodo.eleicao = false;
					System.out.println();
					System.out.println("Fim da Eleicao!");
					System.out.println("O nodo em questao nao eh lider!");
				}
			}
		} 
		catch (SocketException e) 
		{
			System.out.println("Socket: " + e.getMessage());
		} 
		catch (IOException e) 
		{
			System.out.println("IO: " + e.getMessage());
		} 
		finally 
		{
			if (aSocket != null)
				System.out.println("Server: close");
			aSocket.close();
		}
	}
}