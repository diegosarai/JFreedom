package br.web.jfreedom.interfaces;

import java.util.List;
import java.util.Map;

/**
 * Essa interface deve ser implementada pela classe do usuário responsável pela validação individual dos campos do
 * FormBean. O FormBean é o atributo caracterizado pela annotation @FormBean.
 * @author Diego
 *
 * @param <T>
 */
public interface SingleValidator{

	boolean isValid(String field, String fieldLabel, Map<String,String> messages);
	List<String> getMessageList();
}
