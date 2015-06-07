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
	 * Adiciona uma mensagem de erro de validação não mapeada
	 * @param message
	 */
	public void addMessage(String message){
		messageList.add(message);
	}
	
	/**
	 * Adiciona uma lista de mensagens de erro de validação não mapeada
	 * @param messsageList
	 */
	public void addMessageList(List<String> messageList){
		
		for(String message: messageList){

			addMessage(message);
			
		}
	}
	
	/**
	 * Método que retorna true quando a lista de mensagem de erros estiver vazia, ou seja, quando tudo ocorreu bem e todos os campos do usuário passaram nas validações
	 * e false para quando a lista de mensagem de erros não estive vazia, ou seja, alguma validação submetida pelo usuário não passou.
	 * @return
	 */
	public boolean isEmptyMessageList(){
		return messageList.isEmpty();
	}


	public List<String> getMessageList() {
		return messageList;
	}
}
