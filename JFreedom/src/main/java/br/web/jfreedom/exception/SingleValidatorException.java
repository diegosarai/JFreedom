package br.web.jfreedom.exception;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * Exce��o criada para identificar se algum dos campos mapeados pelo usu�rio possui falha de valida��o individual.
 * Caso n�o passe na valida��o individual uma lista de mensagens de erro ser� montada de acordo com as mensagens estabelecidadas
 * pelo usu�rio.
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
	 * M�todo que retorna true quando a lista de mensagem de erros estiver vazia, ou seja, quando tudo ocorreu bem e todos os campos do usu�rio passaram nas valida��es
	 * e false para quando a lista de mensagem de erros n�o estive vazia, ou seja, alguma valida��o submetida pelo usu�rio n�o passou.
	 * @return
	 */
	public boolean isEmptyMessageList(){
		return fieldMessageList.isEmpty();
	}


	public Map<String, List<String>> getFieldMessageList() {
		return fieldMessageList;
	}

}
