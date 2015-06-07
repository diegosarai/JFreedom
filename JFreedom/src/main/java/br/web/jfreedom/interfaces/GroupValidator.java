package br.web.jfreedom.interfaces;

import java.util.List;
import java.util.Map;

public interface GroupValidator<T>{

	boolean isValid(T formBean, Map<String,String> messages);
	List<String> getMessageList();
}
