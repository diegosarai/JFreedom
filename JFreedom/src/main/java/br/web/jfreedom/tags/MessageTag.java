package br.web.jfreedom.tags;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import br.web.jfreedom.enumerator.Constantes;
import br.web.jfreedom.exception.JspMessageException;

public class MessageTag extends BodyTagSupport{

	private String name;
	private String messageClass;
	
	@Override
	public int doEndTag() throws JspException {

		JspWriter jspWriter = pageContext.getOut();
		
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();
		
		//Recupera a lista completa de todas as mensagens de validação para a fase SingleValidator
		Map<String,List<String>> fieldMessageList = (Map<String,List<String>>) request.getAttribute(Constantes.SINGLE_VALIDATOR_ATTRIBUTE.toString());
		
		StringBuilder out = new StringBuilder("");
	
		//Caso o atributo de validação para a fase SingleValidator seja nula significa que a requisição corrente não passou por validação 
		if(fieldMessageList != null){
			
			
			//Recupera a lista de mensagens de acordo com o campo correspondente pelo atributo "Name" no formulário HTML.
			List<String> messageList = fieldMessageList.get(name);
			
			//Caso o usuário tenha informado um nome que não exista ou não está mapeado não será impresso nenhuma mensagem de validação.
			if(messageList != null){
				
				//A verificação a seguir é importante para determinar quantos mensagens de validação serão apresentadas. 
				//Quando somente houver uma mensagem, ela aparecerá na mesma linha
				//Quando houver mais de uma mensagem para o campo, ela aparecerá em linhas distintas
			
				//Caso o usuário só possua uma mensagem de validação para o campo corrente
				if(messageList.size() == 1){
					
					//Caso o usuário passe a classe css para as mensagens de apresentação
					if(messageClass != null){

						out.append("<span class='" + messageClass + "'>");
						
							out.append(messageList.get(0));
			
						out.append("</span>");

					}
					//Caso o usuário não passe a classe css para as mensagens de apresentação
					else{
						

						out.append("<span style='color:red;font-weight:bold'>");
							
							out.append(messageList.get(0));
			
						out.append("</span>");

					}
				}
				//Caso o usuário possua mais de uma mensagem de validação para o campo corrente
				else{
					
					for(String message: messageList){
						
						out.append("<div>");

							//Caso o usuário passe a classe css para as mensagens de apresentação
							if(messageClass != null){
								
								out.append("<span class='" + messageClass + "'>");
								
									out.append(message);
						
								out.append("</span>");
							
							}
							//Caso o usuário não passe a classe css para as mensagens de apresentação
							else{
								
								out.append("<span style='color:red;font-weight:bold'>");
								
									out.append(message);
						
								out.append("</span>");
								
							}
							
						
						out.append("</div>");
						
					}
				}
				
			}
			
		}
		
		
		
		
		try {
			
			jspWriter.println(out.toString());
	
		} catch (IOException e) {
	
			throw new JspMessageException();
		}
		
		
		return super.doEndTag();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMessageClass() {
		return messageClass;
	}

	public void setMessageClass(String messageClass) {
		this.messageClass = messageClass;
	}
	
}
