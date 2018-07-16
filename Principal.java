package Trabalho5;
/**
 * @author Marcos Paulo de Castro
 */

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

public class Principal 
{
	public static void main(String[] args)
	{		
		int portaServidor = 12345;
		String ipTeste = "localhost";
		String portaAcessoServidor = JOptionPane.showInputDialog("Informe a porta para comunicação com o Servidor");
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
		
		ipTeste = JOptionPane.showInputDialog("Informe o ip local da máquina para testes", "localhost");
		
		String quantidadeNodos = JOptionPane.showInputDialog("Informe a quantidade de Nodos que serão iniciados", "10");
		int qtdeNodos = 10;
		try
		{
			qtdeNodos = Integer.parseInt(quantidadeNodos);
		}
		catch (Exception e) 
		{
			JOptionPane.showMessageDialog(null, "Número informado inválido. Por padrão inicializaremos com 10 Nodos");
			qtdeNodos = 10;
		}
		
		List<EstruturaNodos> listaNodos = new ArrayList<EstruturaNodos>();
		mLevantarNodos(portaServidor, ipTeste, listaNodos, qtdeNodos);
		
		for(int c = 0; c < qtdeNodos; c++)
		{
			if(c == 0)
			{
				new Nodo(c + 1, listaNodos, true); //parametro primeiro lider
			}
			else
			{
				new Nodo(c + 1, listaNodos, false);
			}
		}
	}

	private static void mLevantarNodos(int portaServidor, String ipTeste, List<EstruturaNodos> listaNodos, int qtdeNodos) 
	{
		listaNodos.add(new EstruturaNodos(0, ipTeste, portaServidor));

		int portaInicial = 12311;
		for(int c = 0; c < qtdeNodos; c++)
		{
			listaNodos.add(new EstruturaNodos((c + 1), ipTeste, (12311 + c)));
		}
	}
}
