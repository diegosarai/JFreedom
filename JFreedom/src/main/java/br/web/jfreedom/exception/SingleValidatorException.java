package br.web.jfreedom.exception;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * Exceção criada para identificar se algum dos campos mapeados pelo usuário possui falha de validação individual.
 * Caso não passe na validação individual uma lista de mensagens de erro será montada de acordo com as mensagens estabelecidadas
 * pelo usuário.
 * 
 * @author Diego
 *
 */
public class SingleValidatorException extends Exception{

	private Map<String,List<String>> fieldMessageList;
	
	{
		fieldMessageList = new LinkedHashMap<String,List<String>>();
	}
	
	public SingleValidatorException(){
		
	}

	/**
	 * Adiciona uma lista de mensagens para a lista de mensagens
	 * @param messageList
	 */
	public void addMessageList(String fieldName, List<String> messageList){
		
		this.fieldMessageList.put(fieldName, messageList);
		
	}
	/**
	 * Método que retorna true quando a lista de mensagem de erros estiver vazia, ou seja, quando tudo ocorreu bem e todos os campos do usuário passaram nas validações
	 * e false para quando a lista de mensagem de erros não estive vazia, ou seja, alguma validação submetida pelo usuário não passou.
	 * @return
	 */
	public boolean isEmptyMessageList(){
		return fieldMessageList.isEmpty();
	}


	public Map<String, List<String>> getFieldMessageList() {
		return fieldMessageList;
	}

}
