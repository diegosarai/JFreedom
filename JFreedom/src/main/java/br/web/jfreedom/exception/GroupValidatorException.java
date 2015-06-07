package br.web.jfreedom.exception;
import java.util.ArrayList;
import java.util.List;
/**
 * Exce��o criada para identificar se algum dos campos mapeados pelo usu�rio possui falha de valida��o em grupo.
 * Caso n�o passe na valida��o em grupo uma lista de mensagens de erro ser� montada de acordo com as mensagens estabelecidadas
 * pelo usu�rio.
 * 
 * @author Diego
 *
 */
public class GroupValidatorException extends Exception{

	private List<String> messageList;
	
	{
		messageList = new ArrayList<String>();
	}
	
	public GroupValidatorException(){
		
	}
	
	/**
	 * Adiciona uma mensagem de erro de valida��o em grupo
	 * @param message
	 */
	public void addMessage(String message){
		messageList.add(message);
	}
	
	/**
	 * Adiciona uma lista de mensagens de erro de valida��o em grupo
	 * @param messsageList
	 */
	public void addMessageList(List<String> messageList){
		
		for(String message: messageList){

			addMessage(message);
			
		}
	}
	
	/**
	 * M�todo que retorna true quando a lista de mensagem de erros estiver vazia, ou seja, quando tudo ocorreu bem e todos os campos do usu�rio passaram nas valida��es
	 * e false para quando a lista de mensagem de erros n�o estive vazia, ou seja, alguma valida��o submetida pelo usu�rio n�o passou.
	 * @return
	 */
	public boolean isEmptyMessageList(){
		return messageList.isEmpty();
	}


	public List<String> getMessageList() {
		return messageList;
	}


}
