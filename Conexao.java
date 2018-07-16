package Trabalho5;
/**
 * @author Marcos Paulo de Castro
 */

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Conexao extends Thread
{
	DataInputStream in;
	DataOutputStream out;
	Socket clientSocket;
	NodoModel nodoModel;

	public Conexao(Socket socketCliente, NodoModel nodoMOdelP)
	{
		try
		{
			this.nodoModel = nodoMOdelP;
		
			clientSocket = socketCliente;
			in = new DataInputStream(clientSocket.getInputStream());
			out = new DataOutputStream(clientSocket.getOutputStream());
			
		}
		catch(IOException e)
		{
			System.out.println("Connection: " + e.getMessage());
		}
	}
	
	public void run()
	{
		try
		{	
			while(true)
			{
				String data = in.readUTF();
				
				String[] info = data.split(",");
				
				if(info[1].equals("OK"))
				{
					nodoModel.possuiToken = true;
					System.out.println("\nRecebeu o Token.");
				}
				//Quando receber uma solicitacao de conexao do antecessor
				else if(info[1].equals("solicita conexao"))
				{
					//Verifica se o nodo ja tem um antecessor
					if(nodoModel.conexaoAntecessor)
					{
						//Responde que ja esta ocupado
						out.writeUTF(nodoModel.id+",CONECTADO");
						//E chama a funcao reorganizar seu antecessor
						nodoModel.nodoFluxo.mReorganizarConexao(Integer.parseInt(info[0]), this);
					}
					//Senao tem antecessor
					else 
					{
						//Responde que está livre
						out.writeUTF(nodoModel.id+",NAO_CONECTADO");
						
						//Atualiza os dados do antecessor
						nodoModel.conexaoAntecessorModel = this;
						nodoModel.idAntecessor = Integer.parseInt(info[0]);
						nodoModel.conexaoAntecessor = true;
						
						System.out.println("Novo Antecessor: " + nodoModel.idAntecessor);		
					}
				}
				//Caso receba uma solicitacao para reorganizar a conexao
				else if(info[1].equals("REORGANIZE"))
				{
					
					System.out.println("Reorganizando conexoes...");
					int id = Integer.parseInt(info[0]);
					//Chamada do metodo para reorganizar seu sucessor, enviando o id recebido pela mensagem
					nodoModel.nodoClienteTCP.mReorganizarAnel(id);
				}
			}
		//Caso o sucessor desconecte
		} 
		catch(Exception e)
		{
			
			System.out.println("Antecessor: " + nodoModel.idAntecessor);
			//Atualiza os dados de antecessor para nulo
			nodoModel.conexaoAntecessorModel = null;
			nodoModel.idAntecessor = 0;
			nodoModel.conexaoAntecessor = false;
		}
	}
}