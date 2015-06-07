package br.web.jfreedom.config;

import java.io.Serializable;
import java.util.Locale;

import javax.enterprise.context.SessionScoped;

/**
 * Essa classe � respons�vel por gerenciar o Locale de toda a aplica��o do usu�rio atrav�s do escopo de sess�o
 * @author Diego
 *
 */
@SessionScoped
public class LocaleConfig implements Serializable{

	private Locale locale;

	//Atribui explicitamente o Locale default como nulo
	{
		locale = null;
	}
	
	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}
}
