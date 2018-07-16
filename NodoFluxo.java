package Trabalho5;
/**
 * @author Marcos Paulo de Castro
 */

import java.io.IOException;
import java.util.Random;

public class NodoFluxo extends Thread
{
	NodoModel nodoModel;
	Random rand = new Random();
	
	public NodoFluxo(NodoModel nodo)
	{
		this.nodoModel = nodo;
	}

	public void run()
	{	
		try 
		{
			mSolicitaImprimir();
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Timeout de espera para receber o Token (20 segundos), se exceder retorna false
	public Boolean mTempoEsgotado() throws InterruptedException
	{
		for(int i = 0; i < 19999; i++)
		{
			sleep(1);
			//Retorna true se o nodo recebe o Token
			if(nodoModel.possuiToken)
			{
				return true;
			}
			//Senao se estiver ocorrendo uma eleicao retorna false
			else if(nodoModel.eleicao)
			{
				return false;
			}
		}
		//Retorna false se o tempo estipulado termine e o token de acesso nao seja recebido
		System.out.println("\n*** TIMEOUT ***");
		return false;
	}
	
	public void mSolicitaImprimir() throws InterruptedException
	{
		while(true)
		{
			//Se o Token for recebido dentro de 20 segundos
			if(mTempoEsgotado())
			{
				//Se o nodo possui o token, poderá ocorrer uma falha
				mProvocarErro();
				
				//Se o nodo desejar escrever e ja possuir uma conexao com o servidor de impressao
				if(rand.nextBoolean() && nodoModel.conexaoServidor != null)
				{
					//Acessando a secao critica, envia mensagem através da ConexaoTCPCliente
					nodoModel.conexaoServidor.mEnviaMensagem("Node "+nodoModel.id+" esta escrevendo");
					//Se o node ja possuir um sucessor e uma falha de trasmissao do token nao foi gerada, este nodo envia o token para o sucessor e atualiza seu estado de posse do token
					if(	nodoModel.conexaoNodoSucessor != null && !nodoModel.deadLock)
					{
						nodoModel.conexaoNodoSucessor.mEnviaMensagem(nodoModel.id+",OK");
						nodoModel.possuiToken = false;
						System.out.println("\nRepassou o Token.");
					}
				}
				else
				{
				   //Caso o nodo possa e nao queira escrever, este repassa o token para o nodo sucessor e atualiza seu estado de posse do token
				   if(nodoModel.conexaoNodoSucessor != null && !nodoModel.deadLock)
				   {
					   nodoModel.conexaoNodoSucessor.mEnviaMensagem(nodoModel.id+",OK");
					   nodoModel.possuiToken = false;
					   System.out.println("\nRepassou o Token.");
				   }
				}
			}
			//Caso o tempo de espera pelo token exceda
			else 
			{
				//Se nao esteja acontecendo uma eleicao
				if(!nodoModel.eleicao)
				{
					//Este nodo inicia uma nova eleicao para definir uma novo lider
					mIniciarEleicao();
				}	
			}
		}
	}
	
	//Este método ao ser chamado, envia uma mensagem de ELECTION para todos os outros nodos com id maior que o seu,
	//Se ele receber uma mensagem de OK, ele nao eh o lider e aguarda ate o fim da eleicao, caso o tempo de espera
	//for alcançado e o nodo nao receba a mensagem de OK, ele se torna o lider
	public void mIniciarEleicao()
	{
		nodoModel.eleicao = true;
		String msn = "ELECTION";
		System.out.println("\n*** INICIOU ELEICAO ***\n");
		
		//Enviando mensagem para todos os nodos com id maiores por meio do protocolo UDP
		for(int i = nodoModel.id+1; i < 11; i++)
		{
			new NodoUDPCliente(nodoModel, 
							  nodoModel.listaEstruturaNodos.get(i).ipMaquina, 
							  nodoModel.listaEstruturaNodos.get(i).porta, 
							  msn).run();
		}
		
		try 
		{
			//Se o timeout é alcançado (20 segundos) e (2segs para cada Node responder), se houver OK de algum nodo não se torna lider
			if(nodoModel.respostas > 0 || !nodoModel.eleicao)
			{
				nodoModel.lider = false;
				nodoModel.possuiToken = false;
			}
			//Senao, o nodo se torna o lider, recebe o token e avisa a todos que é o novo lider
			else 
			{
				nodoModel.lider = true;
				mAnunciaLider();
				nodoModel.eleicao = false;
				nodoModel.possuiToken = true;
				
				System.out.println("\nFim da Eleicao!\n\nEste Node se tornou o Lider.");
			}
			
			//No final da eleicao, as variaveis de controle de eleicao sao resetadas
			nodoModel.deadLock = false;
			nodoModel.respostas = 0;
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	//Mensagem de anuncio do novo lider se torna lider ao todos os nodos
	public void mAnunciaLider()
	{
		
		//Mensagem a ser enviada.
		String msn = "COORDINATOR";
		
		//Envia a mensagem para todos os outros nodos do anel
		for(int i = 1 ; i < 11; i++)
		{
			
			if( i != nodoModel.id )
			{
				new NodoUDPCliente(nodoModel, 
								  nodoModel.listaEstruturaNodos.get(i).ipMaquina, 
								  nodoModel.listaEstruturaNodos.get(i).porta, 
								  msn).run();
			}	
		}
	}
	
	//Método para simular a perda do token, através do Rand(sorteio) e para verificar se deseja reter o token
	public void mProvocarErro()
	{
		if(rand.nextDouble() > 0.85 && nodoModel.conexaoAntecessor && nodoModel.conexaoSucessor && !nodoModel.eleicao)
		{
			System.err.println("\n Provocando Erro! ");
			//Se resolve reter o token, atualiza as variaveis de simulacao de erro e permissao de escrita
			nodoModel.deadLock = true;
			nodoModel.possuiToken = false;
		}
	}
	
	//Método para reorganizar  reorganiza as conexoes apos a entrada de uma novo nodo.
	//O nodo entrante envia soliciatacao uma conexao a este Node e este, ja possuindo
	//um antencessor, envia uma mensagem para seu antecessor atual, com o id do novo nodo
	//para que este atualize sua conexao de sucessor. Em seguida, este node atualiza sua 
	//conexao com o novo antecessor
	public void mReorganizarConexao(int id, Conexao conexao) throws IOException
	{
		System.out.println("\nReorganizando conexoes..");
		//Apos receber uma soliciatacao de conexao, este nodo envia uma mensagem para seu
		//antecessor atualizar sua conexao de sucessor com o novo nodo entrante
		nodoModel.conexaoAntecessorModel.out.writeUTF(id+",REORGANIZE");
		//Em seguida, este nodo atualiza suas informacoes de antecessor
		nodoModel.conexaoAntecessorModel = conexao;
		nodoModel.idAntecessor = id;
		nodoModel.conexaoAntecessor = true;
		
		System.out.println("Novo Antecessor: " + nodoModel.idAntecessor);
	}
}
