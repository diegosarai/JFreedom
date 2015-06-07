package br.web.jfreedom.exception;

/**
 * Essa exceção ocorrerá quando ocorrer algum erro de validação nas fases:
 * - SingleValidator
 * - GroupValidator
 * - NotMappingValidator
 * 
 * Porém essa exceção só ocorrerá quando o usuário não mapear o atributo errorValidatorPath
 * na annotation @RequestDefinition. Nesses cenários o JFreedom precisa redirecionar para a página de erro do usuário,
 * mas se o usuário não mapear não será possível identificar a JSP de erro que deverá ser redirecionada.
 * 
 * @author Diego
 *
 */
public class ErrorValidatorPathRequiredException extends RuntimeException{

	public ErrorValidatorPathRequiredException(){
		super("When SingleValidator, GroupValidator or NotMappingValidator occurs you must explicitly use errorValidatorPath attribute"
				+ "on annotation @RequestDefinition");
	}
	
}
