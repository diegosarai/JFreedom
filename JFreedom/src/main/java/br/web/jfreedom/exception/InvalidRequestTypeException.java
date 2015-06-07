package br.web.jfreedom.exception;


/**
 * Quando o usu�rio mapear no m�todo em seu controller os tipos GET ou POST para requisi��es, por�m a requisi��o corrente
 * desrepeitar o tipo mapeado essa exce��o ser� lan�ada.
 * 
 * Exemplo. @RequestDefinition(path="/cliente",requestType=RequestType.GET)
 * Se a requisi��o corrente for do tipo POST essa exce��o ser� lan�ada
 * 
 * @author Diego
 *
 */
public class InvalidRequestTypeException extends RuntimeException{

	public InvalidRequestTypeException(){
		super("The request type (GET OR POST) is not the same mapped in @RequestDefinition annotation");
	}
	
}
