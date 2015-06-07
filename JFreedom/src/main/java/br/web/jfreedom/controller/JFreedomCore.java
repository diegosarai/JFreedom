package br.web.jfreedom.controller;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.web.jfreedom.annotation.ControllerMapping;
import br.web.jfreedom.annotation.RequestDefinition;
import br.web.jfreedom.bo.JFreedomBO;
import br.web.jfreedom.config.LocaleConfig;
import br.web.jfreedom.enumerator.Constantes;
import br.web.jfreedom.enumerator.RequestType;
import br.web.jfreedom.exception.GroupValidatorException;
import br.web.jfreedom.exception.NotMappingValidatorException;
import br.web.jfreedom.exception.SingleValidatorException;
import br.web.jfreedom.util.NamedAnnotation;
import br.web.jfreedom.util.Util;
import br.web.jfreedom.util.XmlConfigReader;
import br.web.jfreedom.vo.MappingClassVO;
import br.web.jfreedom.vo.MessageVO;

public class JFreedomCore extends HttpServlet{

	@Inject
	private JFreedomBO bo;
	
	@Inject
	private LocaleConfig localeConfig;
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	
		execute(request, response, RequestType.GET);
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {


		execute(request, response, RequestType.POST);
	}
	
	public void execute(HttpServletRequest request, HttpServletResponse response, RequestType currentRequestType){
		
		/**
		 * CICLO DE VIDA JFeedomCore
		 */
		
		try {
			
			
			
			String servletPath = request.getServletPath();
			String servletPathFormatted = null;
			
			//Caso o caminho passado esteja vazio, n�o h� porque continuar porque nenhum m�todo poder� ser chamado
			if(Util.isEmpty(servletPath)){
				
				return;
			}
			
			//Recupera o Locale da aplica��o. Quando n�o definido, o locale default ser� o do navegador do usu�rio, quando definido ser� o Locale da 
			//classe LocaleConfig definido pelo usu�rio
			Locale locale = localeConfig.getLocale() != null ? localeConfig.getLocale() : request.getLocale();

			
			
			servletPathFormatted = bo.removePathExtension(servletPath);
			
			String filePath = getServletContext().getRealPath(Constantes.CONFIG_PATH.toString());
			
			//L� o conte�do do arquivo de configura��es definido pelo usu�rio (user-config.xml)
			String fileContent = Util.readFile(filePath);
			
			//Cria um handler para o arquivo xml de configura��o. Atrav�s dele � poss�vel recuperar todas as configura��es necess�rios do JFreedom
			XmlConfigReader handler = bo.getXmlHandler(fileContent);
			
			//Recupera todas as classes que est�o mapeadas como controller pelo usu�rio
			List<MappingClassVO> mappingList  = handler.getMappingList();
			
			//Recupera as configura��es de internacionaliza��o do framework JFreedom atrav�s do arquivo xml configurado pelo usu�rio.
			MessageVO messageConfig = handler.getMessageVO();
			
			//Inicializa a vari�vel de messages com todas as mensagens de internacionaliza��o. 
			Map<String,String> messages = new HashMap<String, String>();
			
			
			
			//Itera��o de todas as classes mapeadas pelo usu�rio como controller
			for (MappingClassVO mappingClass : mappingList) {
				
				//Representa a classe do Controller mapeado pelo usu�rio
				Class controllerClass = Class.forName(mappingClass.getClassFqn());
				
				//Recupera o nome do mapeamento definido pelo usu�rio para a classe do controller
				ControllerMapping controllerMapping = (ControllerMapping) controllerClass.getAnnotation(ControllerMapping.class);
				//Nome mapeado no controller do usu�rio
				String controllerMappedName = controllerMapping.value();
				
				Object controllerInstance = CDI.current().select(controllerClass, new NamedAnnotation(controllerMappedName)).get();
				
				Method[] controllerMethods = controllerClass.getMethods();
				
				//Itera todos os m�todos mapeados na classe do usu�rio
				for(Method method: controllerMethods){
					
					//Busca de cada m�todo a annotation "RequestDefinition"
					RequestDefinition requestDefinition = method.getAnnotation(RequestDefinition.class);
					
					//Significa que o m�todo corrente foi mapeado pelo usu�rio
					if(requestDefinition != null){
						
						String mappedPath = requestDefinition.path();
						RequestType mappedRequestType = requestDefinition.requestType();
						String errorValidatorPath = requestDefinition.errorValidatorPath();
						boolean validator = requestDefinition.validator();
						
						//Significa que o caminho chamado � igual ao nome mapeado pelo usu�rio no m�todo em quest�o
						if(mappedPath.trim().equals(servletPathFormatted)){
							
							//Caso a requisi��o corrente n�o seja igual � requisi��o mapeada na annotation @RequestDefinition no m�todo do controller
							//do usu�rio ser� lan�ada a exce��o InvalidRequestTypeException
							bo.validateRequestType(mappedRequestType, currentRequestType);
								
							
							Class[] methodClasses = method.getParameterTypes();
							
							//Caso o usu�rio tenha mapeado os par�metros HttpServletRequest e HttpServletResponse em seu m�todo
							if(bo.isMappedMethod(methodClasses)){
								
								//Recupera um vetor com todos os campos criados pelo usu�rio
								Field[] classFields = controllerClass.getDeclaredFields();

								//Recupera todos os par�metros enviados na requisi��o do formul�rio
								Map<String,String[]> requestParams = request.getParameterMap();
								
								/**
								 * CICLO DE VIDA InitFormBeans
								 */
								
								//M�todo respons�vel por inicializar o atributo com a annotation @FormBean no controller do usu�rio
								bo.initFormBeanFields(classFields, requestParams, controllerInstance);
							
								
								//Caso essa flag seja verdadeira as fases SingleValidator e GroupValidator passaram com sucesso
								boolean isPhaseValid = true;
								
								//Caso essa flag seja true, o usu�rio deseja realizar a valida��o do formul�rio,
								//caso contr�rio, ele deseja pular os ciclos de vida SingleVAlidator, GroupValidator e NotMappingValidator
								if(validator){
									
									/**
									 * Controle de Internacionaliza��o para as fases de Valida��o
									 */
									
									//Quando o usu�rio define as configura��es de internacionliza��o, a vari�vel de mensagem buscar� no arquivo do usu�rio.
									if(messageConfig != null){
									
										messages = bo.getMapMessages(locale, messageConfig);
										
									}
									
									//Caso o usu�rio tenha configurado o mapeamento do arquivo de internacionaliza��o, para cada nova requisi��o
									//ser� gerado uma vari�vel de mensagens 
									if(messageConfig != null){
									
										request.setAttribute(messageConfig.getVar(), messages);
										
									}
									
									//Flag que determina se ciclo SingleValidator e NotMappingValidator executaram sem nenhuma falha de valida��o
									boolean isValidSingleNotMappingValidator = true;
									
									/**
									 * CICLO DE VIDA NotMappingValidator
									 */
									
									try{
										
										//Fase de valida��o n�o mapeada dos campos do formulario (NotMappingValidator)
										bo.validateNotMapping(classFields, controllerInstance, request.getParameterMap(), messages);
									
									}catch(NotMappingValidatorException a){
										
										request.setAttribute(Constantes.NOT_MAPPING_VALIDATOR_ATTRIBUTE.toString(), a.getMessageList());
										isValidSingleNotMappingValidator = false;
										isPhaseValid = false;
									}
									
									/**
									 * CICLO DE VIDA SingleValidator
									 */
									
									try{
										
										//Fase de valida��o individual dos campos do formulario (SingleValidator)
										bo.validateSingleFields(classFields, controllerInstance, messages);
									
									}catch(SingleValidatorException a){
										
										request.setAttribute(Constantes.SINGLE_VALIDATOR_ATTRIBUTE.toString(), a.getFieldMessageList());
										isValidSingleNotMappingValidator = false;
										isPhaseValid = false;
									}
									
									
							
									//Caso a o ciclos SingleValidator e NotMappingValidator obtenham sucesso, ser� realizado a valida��o em grupo dos campos,
									//caso contr�rio n�o ser� chamado o ciclo GroupValidator
									if(isValidSingleNotMappingValidator){
										
										/**
										 * CICLO DE VIDA GroupValidator
										 */
										
										try{
								
											//Fase de valida��o em grupo dos campos do formulario (GroupValidator)
											bo.validateGroupFields(classFields, controllerInstance, messages);
									
										}catch(GroupValidatorException a){
											
											request.setAttribute(Constantes.GROUP_VALIDATOR_ATTRIBUTE.toString(), a.getMessageList());
											isPhaseValid = false;
										}
										
									}
									
								}
								
								
								//Caso a fase SingleValidator ou GroupValidator sejam falsas o m�todo do usu�rio n�o ser� executado
								//e a p�gina de erro definida pelo us�ario ser� chamada
								if(!isPhaseValid){
									
									//Caso o usu�rio n�o passe o caminho do redirecionamento do erro em casos de erros de valida��o a exce��o ErrorValidatorPathRequiredException ser� lan�ada
									bo.validateErroValidatorPath(errorValidatorPath);
								
									/**
									 * CICLO DE VIDA Render View
									 */
									
									//Redireciona a navega��o do usu�rio de acordo com o retorno do m�todo mapeado com a annotation @RequestDefinition
									RequestDispatcher rec = request.getRequestDispatcher(errorValidatorPath);
									rec.forward(request, response);		
								}
								//Caso a fase SingleValidator e GroupValidator sejam verdadeiras o m�todo do usu�rio poder� ser executado
								else{	
									
									
									//Chama o m�todo mapeado pelo usu�rio
									Object methodReturn = method.invoke(controllerInstance, request, response);
									
									/**
									 * Internacionaliza��o - Dever� ser chamado ap�s o m�todo do usu�rio, pois se o mesmo mudar o Locale, na
									 * mesma requisi��o j� ser� poss�vel visualizar a internacionaliza��o
									 */
									
									//Atualiza o Locale da aplica��o. Quando n�o definido, o locale default ser� o do navegador do usu�rio, quando definido ser� o Locale da 
									//classe LocaleConfig definido pelo usu�rio
									locale = localeConfig.getLocale() != null ? localeConfig.getLocale() : request.getLocale();

									
									//Quando o usu�rio define as configura��es de internacionliza��o, a vari�vel de mensagem buscar� no arquivo do usu�rio.
									if(messageConfig != null){
									
										messages = bo.getMapMessages(locale, messageConfig);
										
									}
									
									//Caso o usu�rio tenha configurado o mapeamento do arquivo de internacionaliza��o, para cada nova requisi��o
									//ser� gerado uma vari�vel de mensagens 
									if(messageConfig != null){
									
										request.setAttribute(messageConfig.getVar(), messages);
										
									}
									
									//Cria um Map com todos os atributos declarados e j� atribu�dos pelo usu�rio com a mesma inst�ncia utilizada para chamar o m�todo no controller
									Map<String,Object> fieldMap = bo.getFieldValues(classFields, controllerInstance);
									
									//Envia para a requisi��o um Map com o nome do controller mapeado pelo usu�rio para que possa
									//ser poss�vel acessar os atributos do controller. Exemplo na JSP: ${cliente.nome}
									request.setAttribute(controllerMappedName, fieldMap);
									
									
									//Caso o usu�rio possua um retorno e esse retorno seja uma String, ela ser� utilizada para realizar a navega��o para a p�gina desejada pelo usu�rio
									if(methodReturn != null && methodReturn instanceof String){
									
										/**
										 * CICLO DE VIDA Render View
										 */
										
										//Redireciona a navega��o do usu�rio de acordo com o retorno do m�todo mapeado com a annotation @RequestDefinition
										RequestDispatcher rec = request.getRequestDispatcher((String)methodReturn);
										rec.forward(request, response);		
										
									}
									
								}
								
								
							}
							
							
						}
						
					}
					
				}
				
			}
			
			
		} catch (Exception e){
				e.printStackTrace();
		}
		
		
		
	}
	
}
