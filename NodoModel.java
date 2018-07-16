package Trabalho5;
/**
 * @author Marcos Paulo de Castro
 */

import java.util.ArrayList;
import java.util.List;

public class NodoModel 
{
	int id = 0;

	int portTCP = 0;

	int portUDP = 0;

	String ip = null;

	boolean lider = false;

	boolean primeiroNodo = false;

	List<EstruturaNodos> listaEstruturaNodos = new ArrayList<EstruturaNodos>();

	int idServidorImpressao = 0;

	ConexaoTCPCliente conexaoServidor = null;

	int idAntecessor;

	Conexao conexaoAntecessorModel = null;

	int idSucessor;

	ConexaoTCPCliente conexaoNodoSucessor = null;

	boolean possuiToken = false;

	boolean conexaoSucessor = false;

	boolean conexaoAntecessor = false;

	int respostas = 0;

	boolean deadLock = false;

	boolean eleicao = false;

	NodoFluxo nodoFluxo = new NodoFluxo(this);

	NodoTCPCliente nodoClienteTCP;

	public NodoModel()
	{
		nodoClienteTCP = new NodoTCPCliente(this);
		nodoClienteTCP.start();
	}
}
