package br.web.jfreedom.bo;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import br.web.jfreedom.annotation.FieldMapping;
import br.web.jfreedom.annotation.FormBean;
import br.web.jfreedom.annotation.GroupClass;
import br.web.jfreedom.annotation.NotMappingClass;
import br.web.jfreedom.annotation.SingleClass;
import br.web.jfreedom.enumerator.RequestType;
import br.web.jfreedom.exception.ErrorValidatorPathRequiredException;
import br.web.jfreedom.exception.GroupValidatorException;
import br.web.jfreedom.exception.GroupValidatorInterfaceException;
import br.web.jfreedom.exception.InvalidMappedTypeFieldException;
import br.web.jfreedom.exception.InvalidParametersException;
import br.web.jfreedom.exception.InvalidRequestTypeException;
import br.web.jfreedom.exception.NotMappingValidatorException;
import br.web.jfreedom.exception.NotMappingValidatorInterfaceException;
import br.web.jfreedom.exception.SingleValidatorException;
import br.web.jfreedom.exception.SingleValidatorInterfaceException;
import br.web.jfreedom.interfaces.GroupValidator;
import br.web.jfreedom.interfaces.NotMappingValidator;
import br.web.jfreedom.interfaces.SingleValidator;
import br.web.jfreedom.util.Util;
import br.web.jfreedom.util.XmlConfigReader;
import br.web.jfreedom.vo.MessageVO;

public class JFreedomBO {

	/**
	 * Respons�vel por criar um Map com todas as mensagens internacionalizadas atrav�s do arquivo de configura��o do usu�rio. (Exemplo: messages_pt.properties)
	 * 
	 */
	public Map<String,String> getMapMessages(Locale locale, MessageVO messageConfig){
		
		Map<String,String> messages = new HashMap<String,String>();
		
		ResourceBundle bundle = ResourceBundle.getBundle(messageConfig.getPath(), locale);
		
		Iterator<String> messageIterator = bundle.keySet().iterator();
		
		while(messageIterator.hasNext()){
			
			String key = messageIterator.next();
			
			messages.put(key, bundle.getString(key));
		}
		
		return messages;
	}
	
	/**
	 * M�todo respons�vel por validar todos os campos do FormBean de forma n�o mapeada.
	 * De acordo com a valida��o estabelecida, caso algum campo n�o passe na valida��o ser� gerado uma mensagem de erro e o fluxo n�o chegar� 
	 * no m�todo mapeado pelo usu�rio.
	 * � poss�vel validar mais de um FormBean no mesmo controller.
	 * 
	 * Esse m�todo equivale ao ciclo de vida NotMappingValidator
	 * 
	 */
	public void validateNotMapping(Field[] classFields, Object controllerInstance, Map<String,String[]> parameterMap, Map<String,String> messages) throws InstantiationException, IllegalAccessException, GroupValidatorException, NotMappingValidatorInterfaceException, NotMappingValidatorException{
		
			NotMappingValidatorException exception = new NotMappingValidatorException();
			
		
				//Itera todos os campos declarados no controller do usu�rio
				for(Field controllerField: classFields){
					
					controllerField.setAccessible(true);
					
					Class fieldClass = controllerField.getType();
					
					//Recupera a annotation do campo 
					FormBean formBean = (FormBean) fieldClass.getAnnotation(FormBean.class);
					
					//Significa que o campo corrente � um FormBean com campos mapeados
					if(formBean != null){
						
						NotMappingClass notMappingClass = (NotMappingClass) fieldClass.getAnnotation(NotMappingClass.class);
						
						//Caso o usu�rio tenha utilizado a annotation NotMappingClass para valida��o de campos n�o mapeados
						if(notMappingClass != null){
							
							//Recupera a classe de valida��o n�o mapeada (NotMappingValidator) definida pelo usu�rio para o FormBean corrente
							Class notMappingValidatorClass = notMappingClass.value();
							
							boolean isNotMappingValidatorValid = false;
							
							//Recupera todas as interfaces mapeadas na classe de valida��o n�o mapeada (NotMappingValidator) definida pelo usu�rio
							Class[] interfacesClasses = notMappingValidatorClass.getInterfaces();
							
							/**
							 * Identifica se a classe de valida��o definida pelo o usu�rio implementa a interface NotMappingValidator.
							 * Caso a classe de valida��o n�o implemente a interface NotMappingValidator a exce��o NotMappingValidatorInterfaceException ser� lan�ada 
							 */
							for(Class interfaceClass: interfacesClasses){
							
								if(interfaceClass == NotMappingValidator.class){
									
									isNotMappingValidatorValid = true;
									break;
								}
							}
							
							//Essa exce��o ser� lan�ada quando o usu�rio n�o implementar a interface NotMappingValidator em sua classe de valida��o
							if(!isNotMappingValidatorValid){
								
								throw new NotMappingValidatorInterfaceException();
							}
							
							NotMappingValidator notMappingValidator = (NotMappingValidator) notMappingValidatorClass.newInstance();
							
							if(!notMappingValidator.isValid(parameterMap,messages)){
							
								exception.addMessageList(notMappingValidator.getMessageList());
							}
						}
						
						
					}
				}
				
				/**
				 * Caso alguma valida��o submetida pelo usu�rio n�o passou, ser� lan�ada a exce��o GroupValidatorException com a lista de mensagens de erros
				 */
				if(!exception.isEmptyMessageList()){
					
					throw exception;
				}
	}
	
	/**
	 * M�todo respons�vel por validar todos os campos do FormBean em grupo.
	 * De acordo com a valida��o estabelecida, caso algum campo n�o passe na valida��o ser� gerado uma mensagem de erro e o fluxo n�o chegar� 
	 * no m�todo mapeado pelo usu�rio.
	 * � poss�vel validar mais de um FormBean no mesmo controller.
	 * 
	 * Esse m�todo equivale ao ciclo de vida GroupValidator
	 * 
	 */
	public void validateGroupFields(Field[] classFields, Object controllerInstance, Map<String,String> messages) throws InstantiationException, IllegalAccessException, GroupValidatorException{
		
			GroupValidatorException exception = new GroupValidatorException();
			
		
				//Itera todos os campos declarados no controller do usu�rio
				for(Field controllerField: classFields){
					
					controllerField.setAccessible(true);
					
					Class fieldClass = controllerField.getType();
					
					//Recupera a annotation do campo 
					FormBean formBean = (FormBean) fieldClass.getAnnotation(FormBean.class);
					
					//Significa que o campo corrente � um FormBean com campos mapeados
					if(formBean != null){
						
						//Recupera a inst�ncia do FormBean criada pelo CDI.
						Object formBeanInstance = controllerField.get(controllerInstance);
						
						GroupClass groupClass = (GroupClass) fieldClass.getAnnotation(GroupClass.class);
						
						//Caso o usu�rio tenha utilizado a annotation GroupClass para valida��o de campos em grupo
						if(groupClass != null){
							
							//Recupera a classe de valida��o em grupo definida pelo usu�rio para o FormBean corrente
							Class groupValidatorClass = groupClass.value();
							
							boolean isGroupValidatorValid = false;
							
							//Recupera todas as interfaces mapeadas na classe de valida��o de grupo definida pelo usu�rio
							Class[] interfacesClasses = groupValidatorClass.getInterfaces();
							
							/**
							 * Identifica se a classe de valida��o definida pelo o usu�rio implementa a interface GroupValidator.
							 * Caso a classe de valida��o n�o implemente a interface GroupValidator a exce��o GroupValidatorInterfaceException ser� lan�ada 
							 */
							for(Class interfaceClass: interfacesClasses){
							
								if(interfaceClass == GroupValidator.class){
									
									isGroupValidatorValid = true;
									break;
								}
							}
							
							//Essa exce��o ser� lan�ada quando o usu�rio n�o implementar a interface GroupValidator em sua classe de valida��o
							if(!isGroupValidatorValid){
								
								throw new GroupValidatorInterfaceException();
							}
							
							GroupValidator groupValidator = (GroupValidator) groupValidatorClass.newInstance();
							
							if(!groupValidator.isValid(formBeanInstance, messages)){
							
								exception.addMessageList(groupValidator.getMessageList());
							}
						}
						
						
					}
				}
				
				/**
				 * Caso alguma valida��o submetida pelo usu�rio n�o passou, ser� lan�ada a exce��o GroupValidatorException com a lista de mensagens de erros
				 */
				if(!exception.isEmptyMessageList()){
					
					throw exception;
				}
	}
	
	/**
	 * M�todo respons�vel por validar todos os campos do FormBean de forma individual.
	 * De acordo com a valida��o estabelecida, caso algum campo n�o passe na valida��o ser� gerado uma mensagem de erro e o fluxo n�o chegar� 
	 * no m�todo mapeado pelo usu�rio
	 * � poss�vel validar mais de um FormBean no mesmo controller.
	 * 
	 * Esse m�todo equivale ao ciclo de vida SingleValidator
	 * 
	 */
	public void validateSingleFields(Field[] classFields, Object controllerInstance, Map<String,String> messages) throws InstantiationException, IllegalAccessException, SingleValidatorException{
		
		SingleValidatorException exception  = new SingleValidatorException();
	
		
		//Itera todos os campos declarados no controller do usu�rio
		for(Field controllerField: classFields){
			
			controllerField.setAccessible(true);
			
			Class fieldClass = controllerField.getType();
			
			//Recupera a annotation do campo 
			FormBean formBean = (FormBean) fieldClass.getAnnotation(FormBean.class);
			
			//Significa que o campo corrente � um FormBean com campos mapeados
			if(formBean != null){
				
				//O valor do objeto deve ser injetado por CDI
				Object formBeanInstance = controllerField.get(controllerInstance);
				
				//Significa que o campo n�o foi injetado, portanto est� nulo e n�o pode ser utilizado
				if(formBeanInstance != null){
					
					Field[] formBeanFields = fieldClass.getDeclaredFields();
					
					//Significa que existem campos na classe do FormBean
					if(formBeanFields != null){

						
						//Itera��o de todos os campos do FormBean
						for(Field field: formBeanFields){
							
							field.setAccessible(true);
							
							//Recupera o mapeamento do campo
							FieldMapping fieldMapping = field.getAnnotation(FieldMapping.class);
							//Recupera todas as annotations de valida��o individual para o campo corrente
							SingleClass[] singleClassList = field.getAnnotationsByType(SingleClass.class);
							
							//A valida��o individual dos campos s� pode ser feita para campos mapeados
							if(fieldMapping != null){
								
								String name = fieldMapping.name();
								
								//Em caso do campo estar mapeado. Quando o campo n�o estiver mapeado n�o ser� feito nenhuma valida��o
								if(singleClassList != null){
									
									//Itera todas as valida��es individuais
									for(SingleClass singleClass: singleClassList){
										
										//Recupera a classe respons�vel pela valida��o individual corrente
										Class singleValidatorClass = singleClass.value();
										//Recupera as informa��es das anota��es definidas pelo usu�rio
										String label = singleClass.label();
										
										
										Class[] interfacesClasses = singleValidatorClass.getInterfaces();
										
										boolean isValidatorValid = false;
										
										/**
										 * Identifica se a classe de valida��o definida pelo o usu�rio implementa a interface SingleValidator.
										 * Caso a classe de valida��o n�o implemente a interface SingleValidator a exce��o SingleValidatorInterfaceException ser� lan�ada 
										 */
										for(Class interfaceClass: interfacesClasses){
										
											if(interfaceClass == SingleValidator.class){
												
												isValidatorValid = true;
												break;
											}
										}
										
										//Essa exce��o ser� lan�ada quando o usu�rio n�o implementar a interface SingleValidator em sua classe de valida��o
										if(!isValidatorValid){
											
											throw new SingleValidatorInterfaceException();
										}
										
										SingleValidator singleValidator = (SingleValidator) singleValidatorClass.newInstance();
										
										String fieldValue = (String) field.get(formBeanInstance);
										
										//Caso a valida��o submetida pelo usu�rio n�o seja verdadeira, � necess�rio armazenar a lista de mensagens passada pelo usu�rio
										// para o campo corrente
										if(!singleValidator.isValid(fieldValue, label, messages)){
											
											exception.addMessageList(name,singleValidator.getMessageList());
										}
										
									}
									
								
								}
								
							}
							
							
						}
						
						
					}
				}
			}
		}
		
		/**
		 * Caso alguma valida��o submetida pelo usu�rio n�o passou, ser� lan�ada a exce��o SingleValidatorException com a lista de mensagens de erros
		 */
		if(!exception.isEmptyMessageList()){
			
			throw exception;
		}
		
		
	}
	
	/**
	 * Caso o usu�rio n�o passe o caminho do redirecionamento do erro em casos de erros de valida��o a exce��o ErrorValidatorPathRequiredException ser� lan�ada
	 */
	public void validateErroValidatorPath(String errorValidatorPath){
		
		if(Util.isEmpty(errorValidatorPath)){
			
			throw new ErrorValidatorPathRequiredException();
		}
	}
	
	/**
	 * M�todo respons�vel por inicializar o atributo anotado com a annotation @FormBean no controller do usu�rio com todos os dados
	 * envidados pelo mesmo no formul�rio. O mapeamento dos campos � realizado atrav�s do atributo "name" passado no formul�rio html.
	 * 
	 */
	public void initFormBeanFields(Field[] classFields, Map<String,String[]> requestParams, Object controllerInstance) throws IllegalAccessException{
		
		//Itera todos os campos declarados no controller do usu�rio
		for(Field controllerField: classFields){
			
			controllerField.setAccessible(true);
			
			Class fieldClass = controllerField.getType();
			
			//Recupera a annotation do campo 
			FormBean formBean = (FormBean) fieldClass.getAnnotation(FormBean.class);
			
			//Significa que o campo corrente � um FormBean com campos mapeados
			if(formBean != null){
				
				//O valor do objeto deve ser injetado por CDI
				Object formBeanInstance = controllerField.get(controllerInstance);
				
				//Significa que o campo n�o foi injetado, portanto est� nulo e n�o pode ser utilizado
				if(formBeanInstance != null){
					
					Field[] formBeanFields = fieldClass.getDeclaredFields();
					
					//Significa que existem campos na classe do FormBean
					if(formBeanFields != null){
					
						//Itera todos os campos da annotation FormBean
						for(Field formBeanField: formBeanFields){
							
							formBeanField.setAccessible(true);
							
							FieldMapping fieldMapping = formBeanField.getAnnotation(FieldMapping.class);
							
							//Caso o campo do FormBean esteja mapeado
							if(fieldMapping != null){
								//Nome mapeado para o campo do usu�rio
								String mappedName = fieldMapping.name();
								
								//Iterador para rodar todos os par�metros enviados na requisi��o do formul�rio
								Iterator<Map.Entry<String,String[]>> fieldIterator = requestParams.entrySet().iterator();
								
								//Itera todos os par�metros enviados na requisi��o do formul�rio
								while(fieldIterator.hasNext()){
									
									Map.Entry<String, String[]> fieldParam = fieldIterator.next();
									
									//Caso um dos par�metros recebidos tenha o nome mapeado em um dos campos do FormBean
									if(fieldParam.getKey().trim().equals(mappedName)){
										
										//Neste ponto � feito a an�lise se o usu�rio mapeou um �nico campo ou uma cole��o de campos
									
										//Quando o usu�rio mapeou uma String ser� enviado ao mesmo o pr�meiro �ndice do vetor de campos recebido
										if(formBeanField.getType() == String.class){
											
											formBeanField.set(formBeanInstance, fieldParam.getValue()[0]);
										}
										//Quando o usu�rio mapeou uma vetor de String ser� recebido o vetor de String por completo
										else if(formBeanField.getType() == String[].class){
											
											formBeanField.set(formBeanInstance, fieldParam.getValue());
										}
										//Caso o usu�rio tenha declarado o atributo como List, o mesmo ser� carregado com o vetor de string j� no formato de lista
										else if(formBeanField.getType() == List.class){
											
											List<String> paramList = new ArrayList<String>();
											
											for(String fieldParamValue: fieldParam.getValue()){
												
												paramList.add(fieldParamValue);
											}
											
											formBeanField.set(formBeanInstance, paramList);
										}
										//Caso o usu�rio tenha declarado o atributo como ArrayList, o mesmo ser� carregado com o vetor de string j� no formato de lista
										else if(formBeanField.getType() == ArrayList.class){
											
											ArrayList<String> paramList = new ArrayList<String>();
											
											for(String fieldParamValue: fieldParam.getValue()){
												
												paramList.add(fieldParamValue);
											}
											
											formBeanField.set(formBeanInstance, paramList);
											
										}
										else{
											
											throw new InvalidMappedTypeFieldException();
										}
									}
								}
							}
						}
					}
					controllerField.setAccessible(true);
					//Atribui ao campo mapeado com a annotation @FormBean no controller do usu�rio uma inst�ncia carregada da classe FormBean
					controllerField.set(controllerInstance, formBeanInstance);
					
				}
				
				
			}
		}
	}
	/**
	 * Recebe como entrada um vetor de Fields que s�o todos os campos declarados no Controller do usu�rio
	 * Cria um Map com todos os atributos e o devolve como retorno
	 * 
	 */
	public Map<String,Object> getFieldValues(Field[] fields, Object controllerInstance) throws IllegalArgumentException, IllegalAccessException{
		
		Map<String,Object> fieldMap = new HashMap<String,Object>();
		
		//Lista todos os atributos do controller para carregar o Map 
		for(Field field: fields){
			//Torna o campo acess�vel, mesmo que esteja privado
			field.setAccessible(true);
			fieldMap.put(field.getName(), field.get(controllerInstance));
		}
		
		return fieldMap;
	}
	
	/**
	 * Esse m�todo verifica se os m�todos mapeados pelo usu�rio do framework est�o recebendo HttpServletRequest e HttpServletResponse
	 */
	public boolean isMappedMethod(Class[] methodClasses){
		
		boolean flagRequest = false;
		boolean flagResponse = false;
		boolean flagMapped = false;
		
		for(Class methdoClass: methodClasses){
			
			if(methdoClass == HttpServletRequest.class){
				
				flagRequest = true;
			}
			
			if(methdoClass == HttpServletResponse.class){
				
				flagResponse = true;
			}
			
			if(flagRequest && flagResponse){
				flagMapped = true;
				break;
			}
		}
		
		if(!flagMapped){
			throw new InvalidParametersException();
		}
		
		return flagMapped;
	}
	
	/**
	 * Retorna true quando a requisi��o corrente � igual � requisi��o mapeada na annotation @RequestDefinition e false
	 * quando a requisi��o corrente n�o � igual � requisi��o mapeada na annotation @RequestDefinition.
	 * Caso a requisi��o corrente n�o seja igual � requisi��o mapeada na annotation @RequestDefinition no m�todo do controller do usu�rio
	 * ser� lan�ada a exce��o InvalidRequestTypeException
	 */
	public void validateRequestType(RequestType mappedRequestType, RequestType currentRequestType){
		
		if(mappedRequestType != currentRequestType){
			throw new InvalidRequestTypeException();
		}
		
	}
	
	/**
	 * Cria um handler do arquivo xml atrav�s do parse do SAX
	 */
	public XmlConfigReader getXmlHandler(String fileContent) throws ParserConfigurationException, SAXException, IOException{
		
		XmlConfigReader handler = new XmlConfigReader();
			
		Util.xmlSaxParse(handler, fileContent);
		
		return handler;
	}
	
	
	/**
	 * Encontra o path mapeado pelo usu�rio atrav�s do ServletPath enviado como par�metro
	 * 
	 * Exemplo de retorno: cliente.html
	 * 
	 */
	public String removePathExtension(String path){
		
		String formattedPath = path;
		
		if(path.indexOf(".") != -1){

			formattedPath = path.substring(0,path.lastIndexOf("."));
			
		}
		
		return formattedPath;
	}
}
