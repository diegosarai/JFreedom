package br.web.jfreedom.exception;

import java.util.ArrayList;
import java.util.List;

public class NotMappingValidatorException extends Exception{

	private List<String> messageList;
	
	{
		messageList = new ArrayList<String>();
	}
	
	public NotMappingValidatorException(){
		
	}
	
	/**
	 * Adiciona uma mensagem de erro de valida��o n�o mapeada
	 * @param message
	 */
	public void addMessage(String message){
		messageList.add(message);
	}
	
	/**
	 * Adiciona uma lista de mensagens de erro de valida��o n�o mapeada
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
