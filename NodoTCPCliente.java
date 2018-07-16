package Trabalho5;
/**
 * @author Marcos Paulo de Castro
 */

import java.io.IOException;
import java.net.Socket;

public class NodoTCPCliente extends Thread 
{
	NodoModel nodoModel;
	
	NodoTCPCliente(NodoModel model)
	{
		this.nodoModel = model;
	}

	public void run() 
	{
		mConectarServidorImpressao();
		mConectarProximoNodo(1);
	}

	public void mConectarServidorImpressao() 
	{
		int size = nodoModel.listaEstruturaNodos.size();
	
		for (int i = 0; i < size; i++) 
		{
			if (nodoModel.listaEstruturaNodos.get(i).id == 0) 
			{
				try 
				{
					Socket socket = new Socket(nodoModel.listaEstruturaNodos.get(i).ipMaquina, nodoModel.listaEstruturaNodos.get(i).porta);
					ConexaoTCPCliente c = new ConexaoTCPCliente(socket, nodoModel, true);
					//Salva a conexao na variável reservada
					nodoModel.conexaoServidor = c;

				}
				catch (IOException e) 
				{
					//Caso o processo do servidor de impressao nao esteja levantado
					System.out.println("Servidor de Impressão não encontrado!");
				}
			}
		}
	}

	//Método que estabelece a conexao com um nodo sucessor
	public void mConectarProximoNodo(int i) 
	{
		Socket socket = null;

		//size recebe o numero maximo de nodos
		int size = nodoModel.listaEstruturaNodos.size() - 1;

		try 
		{
			sleep(1000);

			//Caso tenha um sucessoar, sai do método
			if (nodoModel.conexaoNodoSucessor != null) 
			{
				return;
			}
			//Se ja tiver tantado se conectar com todos os nodos, reinicia o id de conexao para 1, para reiniciar as tentativas
			if (i > size) 
			{
				i = 1;
			}
			//Se o id for valido (diferente do seu proprio id)
			if (i != nodoModel.id) 
			{
				//Tenta criar uma conexao
				socket = new Socket(nodoModel.listaEstruturaNodos.get(i).ipMaquina, nodoModel.listaEstruturaNodos.get(i).porta);

				new ConexaoTCPCliente(socket, nodoModel, false);
			}

			//Se este este for o ultimo nodo, recomeça a tentativa de conexao pelo node 1
			else if (i == size) 
			{
				mConectarProximoNodo(1);
			}
			//Senao, apenas incrementa o id do proximo nodo
			else 
			{
				mConectarProximoNodo(i + 1);
			}

		} 
		catch (Exception e) 
		{
			System.out.println("Node " + i + " Nao encontrado..");

			//Caso a tentativa de se conectar com o proximo nao tenha sido bem sucedida, recomeca a conexao a partir do proximo nodo
			mConectarProximoNodo(i + 1);
		}
	}

	//Este método é chamado quando um novo nodo se conecta ao anel de conexoes, recebendo o id do nodo que será ser novo sucessor,
	//estabelecendo a conexão
	public void mReorganizarAnel(int id) 
	{
		try 
		{
			System.out.println("Reorganizando Conexao..");

			Socket socket = new Socket(nodoModel.listaEstruturaNodos.get(id).ipMaquina, nodoModel.listaEstruturaNodos.get(id).porta);

			new ConexaoTCPCliente(socket, nodoModel, false);

		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
}
