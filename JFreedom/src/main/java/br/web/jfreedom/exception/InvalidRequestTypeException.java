package br.web.jfreedom.exception;


/**
 * Quando o usuário mapear no método em seu controller os tipos GET ou POST para requisições, porém a requisição corrente
 * desrepeitar o tipo mapeado essa exceção será lançada.
 * 
 * Exemplo. @RequestDefinition(path="/cliente",requestType=RequestType.GET)
 * Se a requisição corrente for do tipo POST essa exceção será lançada
 * 
 * @author Diego
 *
 */
public class InvalidRequestTypeException extends RuntimeException{

	public InvalidRequestTypeException(){
		super("The request type (GET OR POST) is not the same mapped in @RequestDefinition annotation");
	}
	
}
