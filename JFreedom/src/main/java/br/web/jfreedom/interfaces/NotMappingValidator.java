package br.web.jfreedom.interfaces;

import java.util.List;
import java.util.Map;

public interface NotMappingValidator {

	boolean isValid(Map<String,String[]> parameterMap, Map<String,String> messages);
	List<String> getMessageList();
}
