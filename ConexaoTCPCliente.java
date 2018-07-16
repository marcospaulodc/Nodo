package Trabalho5;
/**
 * @author Marcos Paulo de Castro
 */

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ConexaoTCPCliente 
{
	boolean servidor;
	NodoModel nodoModel;
	Socket socket;
	String resposta = "";
	String[] dadosVetor = new String[2];

	DataInputStream entrada;
	DataOutputStream saida;
	
	public ConexaoTCPCliente(Socket socket, NodoModel nodoModelP, boolean servidor) 
	{
		this.servidor = servidor;
		this.nodoModel = nodoModelP;
		this.socket = socket;

		try 
		{
			this.entrada = new DataInputStream(socket.getInputStream());
			this.saida = new DataOutputStream(socket.getOutputStream());

			if (!servidor) 
			{
				saida.writeUTF(nodoModelP.id + ",solicita conexao");

				while (true) 
				{
					resposta = entrada.readUTF();
					dadosVetor = resposta.split(",");

					if (dadosVetor[1].equals("CONECTADO") || dadosVetor[1].equals("NAO_CONECTADO")) 
					{
						nodoModelP.conexaoNodoSucessor = this;
						nodoModelP.idSucessor = Integer.parseInt(dadosVetor[0]);
						nodoModelP.conexaoSucessor = true;

						System.out.println("Novo Sucessor: " + nodoModelP.idSucessor);
					}
					else if (dadosVetor[1].equals("REORGANIZAR")) 
					{

						System.out.println("Reorganizando conexões...");

						int id = Integer.parseInt(dadosVetor[0]);

						nodoModelP.nodoClienteTCP.mReorganizarAnel(id);
					}
				}
			}
		}
		catch (Exception e) 
		{
			System.out.println("Sucessor: " + nodoModelP.idSucessor + " caiu!");

			//Atualiza as informacoes de conexao com o sucessor
			nodoModelP.conexaoNodoSucessor = null;
			nodoModelP.conexaoSucessor = false;

			//Define que o id para tentar uma nova conexao eh um valor maior que a do sucessor atual, com o qual a conexao foi perdida
			int tentaProximo = nodoModelP.idSucessor + 1;

			// Se a sugestao de proxima tentativa for maior que o valor maximo do ultimo nodo, a proxima tentativa sera com o de id 1
			if (tentaProximo > 9) 
			{
				tentaProximo = 1;
			}
			//Chama o método para encontrar um proximo nodo sucessor, iniciando com a sugestao de tentativa
			nodoModelP.nodoClienteTCP.mConectarProximoNodo(tentaProximo);
		}
	}

	//Método para enviar mensagens para o nodo sucessor ou para o servidor de impressão
	public void mEnviaMensagem(String msg) 
	{
		try 
		{
			saida.writeUTF(msg);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

	//Método para fechar uma conexão
	public void mFechaConexao() 
	{
		try 
		{
			this.socket.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
}
