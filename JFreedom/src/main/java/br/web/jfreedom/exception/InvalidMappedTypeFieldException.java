package br.web.jfreedom.exception;

/**
 * Essa exceção ocorrerá quando o usuário mapear um campo no Form Bean que não seja do tipo:
 * - String
 * - String[]
 * - List<String>
 * - ArrayList<String>
 * @author Diego
 *
 */
public class InvalidMappedTypeFieldException extends RuntimeException{

	public InvalidMappedTypeFieldException(){
		super("The mapped Form Bean field must be String, String[], List<String> or ArrayList<String>");
	}
}
