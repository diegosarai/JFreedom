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
	 * Responsável por criar um Map com todas as mensagens internacionalizadas através do arquivo de configuração do usuário. (Exemplo: messages_pt.properties)
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
	 * Método responsável por validar todos os campos do FormBean de forma não mapeada.
	 * De acordo com a validação estabelecida, caso algum campo não passe na validação será gerado uma mensagem de erro e o fluxo não chegará 
	 * no método mapeado pelo usuário.
	 * É possível validar mais de um FormBean no mesmo controller.
	 * 
	 * Esse método equivale ao ciclo de vida NotMappingValidator
	 * 
	 */
	public void validateNotMapping(Field[] classFields, Object controllerInstance, Map<String,String[]> parameterMap, Map<String,String> messages) throws InstantiationException, IllegalAccessException, GroupValidatorException, NotMappingValidatorInterfaceException, NotMappingValidatorException{
		
			NotMappingValidatorException exception = new NotMappingValidatorException();
			
		
				//Itera todos os campos declarados no controller do usuário
				for(Field controllerField: classFields){
					
					controllerField.setAccessible(true);
					
					Class fieldClass = controllerField.getType();
					
					//Recupera a annotation do campo 
					FormBean formBean = (FormBean) fieldClass.getAnnotation(FormBean.class);
					
					//Significa que o campo corrente é um FormBean com campos mapeados
					if(formBean != null){
						
						NotMappingClass notMappingClass = (NotMappingClass) fieldClass.getAnnotation(NotMappingClass.class);
						
						//Caso o usuário tenha utilizado a annotation NotMappingClass para validação de campos não mapeados
						if(notMappingClass != null){
							
							//Recupera a classe de validação não mapeada (NotMappingValidator) definida pelo usuário para o FormBean corrente
							Class notMappingValidatorClass = notMappingClass.value();
							
							boolean isNotMappingValidatorValid = false;
							
							//Recupera todas as interfaces mapeadas na classe de validação não mapeada (NotMappingValidator) definida pelo usuário
							Class[] interfacesClasses = notMappingValidatorClass.getInterfaces();
							
							/**
							 * Identifica se a classe de validação definida pelo o usuário implementa a interface NotMappingValidator.
							 * Caso a classe de validação não implemente a interface NotMappingValidator a exceção NotMappingValidatorInterfaceException será lançada 
							 */
							for(Class interfaceClass: interfacesClasses){
							
								if(interfaceClass == NotMappingValidator.class){
									
									isNotMappingValidatorValid = true;
									break;
								}
							}
							
							//Essa exceção será lançada quando o usuário não implementar a interface NotMappingValidator em sua classe de validação
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
				 * Caso alguma validação submetida pelo usuário não passou, será lançada a exceção GroupValidatorException com a lista de mensagens de erros
				 */
				if(!exception.isEmptyMessageList()){
					
					throw exception;
				}
	}
	
	/**
	 * Método responsável por validar todos os campos do FormBean em grupo.
	 * De acordo com a validação estabelecida, caso algum campo não passe na validação será gerado uma mensagem de erro e o fluxo não chegará 
	 * no método mapeado pelo usuário.
	 * É possível validar mais de um FormBean no mesmo controller.
	 * 
	 * Esse método equivale ao ciclo de vida GroupValidator
	 * 
	 */
	public void validateGroupFields(Field[] classFields, Object controllerInstance, Map<String,String> messages) throws InstantiationException, IllegalAccessException, GroupValidatorException{
		
			GroupValidatorException exception = new GroupValidatorException();
			
		
				//Itera todos os campos declarados no controller do usuário
				for(Field controllerField: classFields){
					
					controllerField.setAccessible(true);
					
					Class fieldClass = controllerField.getType();
					
					//Recupera a annotation do campo 
					FormBean formBean = (FormBean) fieldClass.getAnnotation(FormBean.class);
					
					//Significa que o campo corrente é um FormBean com campos mapeados
					if(formBean != null){
						
						//Recupera a instância do FormBean criada pelo CDI.
						Object formBeanInstance = controllerField.get(controllerInstance);
						
						GroupClass groupClass = (GroupClass) fieldClass.getAnnotation(GroupClass.class);
						
						//Caso o usuário tenha utilizado a annotation GroupClass para validação de campos em grupo
						if(groupClass != null){
							
							//Recupera a classe de validação em grupo definida pelo usuário para o FormBean corrente
							Class groupValidatorClass = groupClass.value();
							
							boolean isGroupValidatorValid = false;
							
							//Recupera todas as interfaces mapeadas na classe de validação de grupo definida pelo usuário
							Class[] interfacesClasses = groupValidatorClass.getInterfaces();
							
							/**
							 * Identifica se a classe de validação definida pelo o usuário implementa a interface GroupValidator.
							 * Caso a classe de validação não implemente a interface GroupValidator a exceção GroupValidatorInterfaceException será lançada 
							 */
							for(Class interfaceClass: interfacesClasses){
							
								if(interfaceClass == GroupValidator.class){
									
									isGroupValidatorValid = true;
									break;
								}
							}
							
							//Essa exceção será lançada quando o usuário não implementar a interface GroupValidator em sua classe de validação
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
				 * Caso alguma validação submetida pelo usuário não passou, será lançada a exceção GroupValidatorException com a lista de mensagens de erros
				 */
				if(!exception.isEmptyMessageList()){
					
					throw exception;
				}
	}
	
	/**
	 * Mètodo responsável por validar todos os campos do FormBean de forma individual.
	 * De acordo com a validação estabelecida, caso algum campo não passe na validação será gerado uma mensagem de erro e o fluxo não chegará 
	 * no método mapeado pelo usuário
	 * É possível validar mais de um FormBean no mesmo controller.
	 * 
	 * Esse método equivale ao ciclo de vida SingleValidator
	 * 
	 */
	public void validateSingleFields(Field[] classFields, Object controllerInstance, Map<String,String> messages) throws InstantiationException, IllegalAccessException, SingleValidatorException{
		
		SingleValidatorException exception  = new SingleValidatorException();
	
		
		//Itera todos os campos declarados no controller do usuário
		for(Field controllerField: classFields){
			
			controllerField.setAccessible(true);
			
			Class fieldClass = controllerField.getType();
			
			//Recupera a annotation do campo 
			FormBean formBean = (FormBean) fieldClass.getAnnotation(FormBean.class);
			
			//Significa que o campo corrente é um FormBean com campos mapeados
			if(formBean != null){
				
				//O valor do objeto deve ser injetado por CDI
				Object formBeanInstance = controllerField.get(controllerInstance);
				
				//Significa que o campo não foi injetado, portanto está nulo e não pode ser utilizado
				if(formBeanInstance != null){
					
					Field[] formBeanFields = fieldClass.getDeclaredFields();
					
					//Significa que existem campos na classe do FormBean
					if(formBeanFields != null){

						
						//Iteração de todos os campos do FormBean
						for(Field field: formBeanFields){
							
							field.setAccessible(true);
							
							//Recupera o mapeamento do campo
							FieldMapping fieldMapping = field.getAnnotation(FieldMapping.class);
							//Recupera todas as annotations de validação individual para o campo corrente
							SingleClass[] singleClassList = field.getAnnotationsByType(SingleClass.class);
							
							//A validação individual dos campos só pode ser feita para campos mapeados
							if(fieldMapping != null){
								
								String name = fieldMapping.name();
								
								//Em caso do campo estar mapeado. Quando o campo não estiver mapeado não será feito nenhuma validação
								if(singleClassList != null){
									
									//Itera todas as validações individuais
									for(SingleClass singleClass: singleClassList){
										
										//Recupera a classe responsável pela validação individual corrente
										Class singleValidatorClass = singleClass.value();
										//Recupera as informações das anotações definidas pelo usuário
										String label = singleClass.label();
										
										
										Class[] interfacesClasses = singleValidatorClass.getInterfaces();
										
										boolean isValidatorValid = false;
										
										/**
										 * Identifica se a classe de validação definida pelo o usuário implementa a interface SingleValidator.
										 * Caso a classe de validação não implemente a interface SingleValidator a exceção SingleValidatorInterfaceException será lançada 
										 */
										for(Class interfaceClass: interfacesClasses){
										
											if(interfaceClass == SingleValidator.class){
												
												isValidatorValid = true;
												break;
											}
										}
										
										//Essa exceção será lançada quando o usuário não implementar a interface SingleValidator em sua classe de validação
										if(!isValidatorValid){
											
											throw new SingleValidatorInterfaceException();
										}
										
										SingleValidator singleValidator = (SingleValidator) singleValidatorClass.newInstance();
										
										String fieldValue = (String) field.get(formBeanInstance);
										
										//Caso a validação submetida pelo usuário não seja verdadeira, é necessário armazenar a lista de mensagens passada pelo usuário
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
		 * Caso alguma validação submetida pelo usuário não passou, será lançada a exceção SingleValidatorException com a lista de mensagens de erros
		 */
		if(!exception.isEmptyMessageList()){
			
			throw exception;
		}
		
		
	}
	
	/**
	 * Caso o usuário não passe o caminho do redirecionamento do erro em casos de erros de validação a exceção ErrorValidatorPathRequiredException será lançada
	 */
	public void validateErroValidatorPath(String errorValidatorPath){
		
		if(Util.isEmpty(errorValidatorPath)){
			
			throw new ErrorValidatorPathRequiredException();
		}
	}
	
	/**
	 * Método responsável por inicializar o atributo anotado com a annotation @FormBean no controller do usuário com todos os dados
	 * envidados pelo mesmo no formulário. O mapeamento dos campos é realizado através do atributo "name" passado no formulário html.
	 * 
	 */
	public void initFormBeanFields(Field[] classFields, Map<String,String[]> requestParams, Object controllerInstance) throws IllegalAccessException{
		
		//Itera todos os campos declarados no controller do usuário
		for(Field controllerField: classFields){
			
			controllerField.setAccessible(true);
			
			Class fieldClass = controllerField.getType();
			
			//Recupera a annotation do campo 
			FormBean formBean = (FormBean) fieldClass.getAnnotation(FormBean.class);
			
			//Significa que o campo corrente é um FormBean com campos mapeados
			if(formBean != null){
				
				//O valor do objeto deve ser injetado por CDI
				Object formBeanInstance = controllerField.get(controllerInstance);
				
				//Significa que o campo não foi injetado, portanto está nulo e não pode ser utilizado
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
								//Nome mapeado para o campo do usuário
								String mappedName = fieldMapping.name();
								
								//Iterador para rodar todos os parâmetros enviados na requisição do formulário
								Iterator<Map.Entry<String,String[]>> fieldIterator = requestParams.entrySet().iterator();
								
								//Itera todos os parâmetros enviados na requisição do formulário
								while(fieldIterator.hasNext()){
									
									Map.Entry<String, String[]> fieldParam = fieldIterator.next();
									
									//Caso um dos parâmetros recebidos tenha o nome mapeado em um dos campos do FormBean
									if(fieldParam.getKey().trim().equals(mappedName)){
										
										//Neste ponto é feito a análise se o usuário mapeou um único campo ou uma coleção de campos
									
										//Quando o usuário mapeou uma String será enviado ao mesmo o prímeiro índice do vetor de campos recebido
										if(formBeanField.getType() == String.class){
											
											formBeanField.set(formBeanInstance, fieldParam.getValue()[0]);
										}
										//Quando o usuário mapeou uma vetor de String será recebido o vetor de String por completo
										else if(formBeanField.getType() == String[].class){
											
											formBeanField.set(formBeanInstance, fieldParam.getValue());
										}
										//Caso o usuário tenha declarado o atributo como List, o mesmo será carregado com o vetor de string já no formato de lista
										else if(formBeanField.getType() == List.class){
											
											List<String> paramList = new ArrayList<String>();
											
											for(String fieldParamValue: fieldParam.getValue()){
												
												paramList.add(fieldParamValue);
											}
											
											formBeanField.set(formBeanInstance, paramList);
										}
										//Caso o usuário tenha declarado o atributo como ArrayList, o mesmo será carregado com o vetor de string já no formato de lista
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
					//Atribui ao campo mapeado com a annotation @FormBean no controller do usuário uma instância carregada da classe FormBean
					controllerField.set(controllerInstance, formBeanInstance);
					
				}
				
				
			}
		}
	}
	/**
	 * Recebe como entrada um vetor de Fields que são todos os campos declarados no Controller do usuário
	 * Cria um Map com todos os atributos e o devolve como retorno
	 * 
	 */
	public Map<String,Object> getFieldValues(Field[] fields, Object controllerInstance) throws IllegalArgumentException, IllegalAccessException{
		
		Map<String,Object> fieldMap = new HashMap<String,Object>();
		
		//Lista todos os atributos do controller para carregar o Map 
		for(Field field: fields){
			//Torna o campo acessível, mesmo que esteja privado
			field.setAccessible(true);
			fieldMap.put(field.getName(), field.get(controllerInstance));
		}
		
		return fieldMap;
	}
	
	/**
	 * Esse método verifica se os métodos mapeados pelo usuário do framework estão recebendo HttpServletRequest e HttpServletResponse
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
	 * Retorna true quando a requisição corrente é igual à requisição mapeada na annotation @RequestDefinition e false
	 * quando a requisição corrente não é igual à requisição mapeada na annotation @RequestDefinition.
	 * Caso a requisição corrente não seja igual à requisição mapeada na annotation @RequestDefinition no método do controller do usuário
	 * será lançada a exceção InvalidRequestTypeException
	 */
	public void validateRequestType(RequestType mappedRequestType, RequestType currentRequestType){
		
		if(mappedRequestType != currentRequestType){
			throw new InvalidRequestTypeException();
		}
		
	}
	
	/**
	 * Cria um handler do arquivo xml através do parse do SAX
	 */
	public XmlConfigReader getXmlHandler(String fileContent) throws ParserConfigurationException, SAXException, IOException{
		
		XmlConfigReader handler = new XmlConfigReader();
			
		Util.xmlSaxParse(handler, fileContent);
		
		return handler;
	}
	
	
	/**
	 * Encontra o path mapeado pelo usuário através do ServletPath enviado como parâmetro
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
