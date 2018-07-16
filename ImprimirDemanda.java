package Trabalho5;
/**
 * @author Marcos Paulo de Castro
 */

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

public class ImprimirDemanda extends Thread 
{

	DataOutputStream saida;
	DataInputStream entrada;
	Socket clienteRequisicao;

	public ImprimirDemanda(Socket clienteRequisicaoParametro) 
	{
		try 
		{
			clienteRequisicao = clienteRequisicaoParametro;
			entrada = new DataInputStream(clienteRequisicao.getInputStream());
			saida = new DataOutputStream(clienteRequisicao.getOutputStream());
		} 
		catch (IOException e) 
		{
			System.out.println("Erro de conexão: " + e.getMessage());
		}
	}

	synchronized public void run() 
	{
		while (true) 
		{
			try 
			{
				String data = entrada.readUTF();
				System.out.println("Nodo Porta: " + clienteRequisicao.getPort() + " SERVIDOR INFORMA: " + data);
				for (int c = 0; c < 10; c++) 
				{
					System.out.print(c + ", ");
					/*try 
					{
						sleep(500);
					} 
					catch (InterruptedException e) 
					{
						e.printStackTrace();
					}*/
				}
				System.out.println();
			} 
			catch (EOFException e) 
			{
				// Caso o node referente a esta conexao caia, a conexao é fechada
				try 
				{

					System.out.println("Conexao fechada pelo cliente");

					clienteRequisicao.close();
					break;
				} 
				catch (IOException e1) 
				{
					System.out.println("IO: " + e1.getMessage());
				}

			} 
			catch (IOException e) 
			{
				System.out.println("IO: " + e.getMessage());
			}
		}
	}
}