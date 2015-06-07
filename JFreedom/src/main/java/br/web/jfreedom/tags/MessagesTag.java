package br.web.jfreedom.tags;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import br.web.jfreedom.enumerator.Constantes;
import br.web.jfreedom.exception.JspMessageException;

public class MessagesTag extends BodyTagSupport{

	private String messageClass;
	
	@Override
	public int doEndTag() throws JspException {

		JspWriter jspWriter = pageContext.getOut();
		
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();
		
		//Recupera a lista completa de todas as mensagens de validação para a fase SingleValidator
		Map<String,List<String>> singleMessageList = (Map<String,List<String>>) request.getAttribute(Constantes.SINGLE_VALIDATOR_ATTRIBUTE.toString());
		
		//Recupera a lista completa de todas as mensagens de validação para a fase GroupValidator
		List<String> groupMessageList = (List<String>) request.getAttribute(Constantes.GROUP_VALIDATOR_ATTRIBUTE.toString());;
		
		//Recupera a lista completa de todas as mensagens de validação para a fase NotMappingValidator
		List<String> notMappingMessageList = (List<String>) request.getAttribute(Constantes.NOT_MAPPING_VALIDATOR_ATTRIBUTE.toString());;
				
		
		StringBuilder out = new StringBuilder("");
	
		
		/**
		 * CICLO DE VIDA NotMappingValidator foi executado
		 */
		
		//Caso o atributo de validação para a fase NotMappingValidator seja nula significa que a requisição corrente não passou por validação de campos não mapeados
		//ou a validação dos campos não mapeados ocorreu com sucesso
		if(notMappingMessageList != null){
	
			for(String message: notMappingMessageList){
				
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
		
		/**
		 * CICLO DE VIDA SingleValidator foi executado
		 */
		
		//Caso o atributo de validação para a fase SingleValidator seja nula significa que a requisição corrente não passou por validação de campos individuais
		//ou a validação de campos individuais ocorreu com sucesso
		if(singleMessageList != null){
			
			Iterator<Map.Entry<String, List<String>>> messagesIterator = singleMessageList.entrySet().iterator();
			
			
			//Itera as mensagens de validação para cada campo, em que a chave do map representa o "Name" do campo no formulário html e o valor do map
			//representa a lista de mensagens a serem apresentadas para o usuário
			while(messagesIterator.hasNext()){
				
				Map.Entry<String, List<String>> item = messagesIterator.next();
				
				List<String> messageList = item.getValue();
				
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
		
		/**
		 * CICLO DE VIDA GroupValidator foi executado
		 */
		
		//Caso o atributo de validação para a fase GroupValidator seja nula significa que a requisição corrente não passou por validação em grupo
		//ou a validação de campos em grupo ocorreu com sucesso
		if(groupMessageList != null){
	
			for(String message: groupMessageList){
				
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
		
		
	
		
		try {
			
			jspWriter.println(out.toString());
	
		} catch (IOException e) {
	
			throw new JspMessageException();
		}
		
	
		return super.doEndTag();
	}


	public String getMessageClass() {
		return messageClass;
	}


	public void setMessageClass(String messageClass) {
		this.messageClass = messageClass;
	}
	
}
