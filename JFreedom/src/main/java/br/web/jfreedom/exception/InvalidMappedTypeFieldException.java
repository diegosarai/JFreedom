package br.web.jfreedom.exception;

/**
 * Essa exce��o ocorrer� quando o usu�rio mapear um campo no Form Bean que n�o seja do tipo:
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
