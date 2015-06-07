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
			
			//Caso o caminho passado esteja vazio, não há porque continuar porque nenhum método poderá ser chamado
			if(Util.isEmpty(servletPath)){
				
				return;
			}
			
			//Recupera o Locale da aplicação. Quando não definido, o locale default será o do navegador do usuário, quando definido será o Locale da 
			//classe LocaleConfig definido pelo usuário
			Locale locale = localeConfig.getLocale() != null ? localeConfig.getLocale() : request.getLocale();

			
			
			servletPathFormatted = bo.removePathExtension(servletPath);
			
			String filePath = getServletContext().getRealPath(Constantes.CONFIG_PATH.toString());
			
			//Lê o conteúdo do arquivo de configurações definido pelo usuário (user-config.xml)
			String fileContent = Util.readFile(filePath);
			
			//Cria um handler para o arquivo xml de configuração. Através dele é possível recuperar todas as configurações necessários do JFreedom
			XmlConfigReader handler = bo.getXmlHandler(fileContent);
			
			//Recupera todas as classes que estão mapeadas como controller pelo usuário
			List<MappingClassVO> mappingList  = handler.getMappingList();
			
			//Recupera as configurações de internacionalização do framework JFreedom através do arquivo xml configurado pelo usuário.
			MessageVO messageConfig = handler.getMessageVO();
			
			//Inicializa a variável de messages com todas as mensagens de internacionalização. 
			Map<String,String> messages = new HashMap<String, String>();
			
			
			
			//Iteração de todas as classes mapeadas pelo usuário como controller
			for (MappingClassVO mappingClass : mappingList) {
				
				//Representa a classe do Controller mapeado pelo usuário
				Class controllerClass = Class.forName(mappingClass.getClassFqn());
				
				//Recupera o nome do mapeamento definido pelo usuário para a classe do controller
				ControllerMapping controllerMapping = (ControllerMapping) controllerClass.getAnnotation(ControllerMapping.class);
				//Nome mapeado no controller do usuário
				String controllerMappedName = controllerMapping.value();
				
				Object controllerInstance = CDI.current().select(controllerClass, new NamedAnnotation(controllerMappedName)).get();
				
				Method[] controllerMethods = controllerClass.getMethods();
				
				//Itera todos os métodos mapeados na classe do usuário
				for(Method method: controllerMethods){
					
					//Busca de cada método a annotation "RequestDefinition"
					RequestDefinition requestDefinition = method.getAnnotation(RequestDefinition.class);
					
					//Significa que o método corrente foi mapeado pelo usuário
					if(requestDefinition != null){
						
						String mappedPath = requestDefinition.path();
						RequestType mappedRequestType = requestDefinition.requestType();
						String errorValidatorPath = requestDefinition.errorValidatorPath();
						boolean validator = requestDefinition.validator();
						
						//Significa que o caminho chamado é igual ao nome mapeado pelo usuário no método em questão
						if(mappedPath.trim().equals(servletPathFormatted)){
							
							//Caso a requisição corrente não seja igual à requisição mapeada na annotation @RequestDefinition no método do controller
							//do usuário será lançada a exceção InvalidRequestTypeException
							bo.validateRequestType(mappedRequestType, currentRequestType);
								
							
							Class[] methodClasses = method.getParameterTypes();
							
							//Caso o usuário tenha mapeado os parâmetros HttpServletRequest e HttpServletResponse em seu método
							if(bo.isMappedMethod(methodClasses)){
								
								//Recupera um vetor com todos os campos criados pelo usuário
								Field[] classFields = controllerClass.getDeclaredFields();

								//Recupera todos os parâmetros enviados na requisição do formulário
								Map<String,String[]> requestParams = request.getParameterMap();
								
								/**
								 * CICLO DE VIDA InitFormBeans
								 */
								
								//Método responsável por inicializar o atributo com a annotation @FormBean no controller do usuário
								bo.initFormBeanFields(classFields, requestParams, controllerInstance);
							
								
								//Caso essa flag seja verdadeira as fases SingleValidator e GroupValidator passaram com sucesso
								boolean isPhaseValid = true;
								
								//Caso essa flag seja true, o usuário deseja realizar a validação do formulário,
								//caso contrário, ele deseja pular os ciclos de vida SingleVAlidator, GroupValidator e NotMappingValidator
								if(validator){
									
									/**
									 * Controle de Internacionalização para as fases de Validação
									 */
									
									//Quando o usuário define as configurações de internacionlização, a variável de mensagem buscará no arquivo do usuário.
									if(messageConfig != null){
									
										messages = bo.getMapMessages(locale, messageConfig);
										
									}
									
									//Caso o usuário tenha configurado o mapeamento do arquivo de internacionalização, para cada nova requisição
									//será gerado uma variável de mensagens 
									if(messageConfig != null){
									
										request.setAttribute(messageConfig.getVar(), messages);
										
									}
									
									//Flag que determina se ciclo SingleValidator e NotMappingValidator executaram sem nenhuma falha de validação
									boolean isValidSingleNotMappingValidator = true;
									
									/**
									 * CICLO DE VIDA NotMappingValidator
									 */
									
									try{
										
										//Fase de validação não mapeada dos campos do formulario (NotMappingValidator)
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
										
										//Fase de validação individual dos campos do formulario (SingleValidator)
										bo.validateSingleFields(classFields, controllerInstance, messages);
									
									}catch(SingleValidatorException a){
										
										request.setAttribute(Constantes.SINGLE_VALIDATOR_ATTRIBUTE.toString(), a.getFieldMessageList());
										isValidSingleNotMappingValidator = false;
										isPhaseValid = false;
									}
									
									
							
									//Caso a o ciclos SingleValidator e NotMappingValidator obtenham sucesso, será realizado a validação em grupo dos campos,
									//caso contrário não será chamado o ciclo GroupValidator
									if(isValidSingleNotMappingValidator){
										
										/**
										 * CICLO DE VIDA GroupValidator
										 */
										
										try{
								
											//Fase de validação em grupo dos campos do formulario (GroupValidator)
											bo.validateGroupFields(classFields, controllerInstance, messages);
									
										}catch(GroupValidatorException a){
											
											request.setAttribute(Constantes.GROUP_VALIDATOR_ATTRIBUTE.toString(), a.getMessageList());
											isPhaseValid = false;
										}
										
									}
									
								}
								
								
								//Caso a fase SingleValidator ou GroupValidator sejam falsas o método do usuário não será executado
								//e a página de erro definida pelo usúario será chamada
								if(!isPhaseValid){
									
									//Caso o usuário não passe o caminho do redirecionamento do erro em casos de erros de validação a exceção ErrorValidatorPathRequiredException será lançada
									bo.validateErroValidatorPath(errorValidatorPath);
								
									/**
									 * CICLO DE VIDA Render View
									 */
									
									//Redireciona a navegação do usuário de acordo com o retorno do método mapeado com a annotation @RequestDefinition
									RequestDispatcher rec = request.getRequestDispatcher(errorValidatorPath);
									rec.forward(request, response);		
								}
								//Caso a fase SingleValidator e GroupValidator sejam verdadeiras o método do usuário poderá ser executado
								else{	
									
									
									//Chama o método mapeado pelo usuário
									Object methodReturn = method.invoke(controllerInstance, request, response);
									
									/**
									 * Internacionalização - Deverá ser chamado após o método do usuário, pois se o mesmo mudar o Locale, na
									 * mesma requisição já será possível visualizar a internacionalização
									 */
									
									//Atualiza o Locale da aplicação. Quando não definido, o locale default será o do navegador do usuário, quando definido será o Locale da 
									//classe LocaleConfig definido pelo usuário
									locale = localeConfig.getLocale() != null ? localeConfig.getLocale() : request.getLocale();

									
									//Quando o usuário define as configurações de internacionlização, a variável de mensagem buscará no arquivo do usuário.
									if(messageConfig != null){
									
										messages = bo.getMapMessages(locale, messageConfig);
										
									}
									
									//Caso o usuário tenha configurado o mapeamento do arquivo de internacionalização, para cada nova requisição
									//será gerado uma variável de mensagens 
									if(messageConfig != null){
									
										request.setAttribute(messageConfig.getVar(), messages);
										
									}
									
									//Cria um Map com todos os atributos declarados e já atribuídos pelo usuário com a mesma instância utilizada para chamar o método no controller
									Map<String,Object> fieldMap = bo.getFieldValues(classFields, controllerInstance);
									
									//Envia para a requisição um Map com o nome do controller mapeado pelo usuário para que possa
									//ser possível acessar os atributos do controller. Exemplo na JSP: ${cliente.nome}
									request.setAttribute(controllerMappedName, fieldMap);
									
									
									//Caso o usuário possua um retorno e esse retorno seja uma String, ela será utilizada para realizar a navegação para a página desejada pelo usuário
									if(methodReturn != null && methodReturn instanceof String){
									
										/**
										 * CICLO DE VIDA Render View
										 */
										
										//Redireciona a navegação do usuário de acordo com o retorno do método mapeado com a annotation @RequestDefinition
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
