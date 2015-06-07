package br.web.jfreedom.config;

import java.io.Serializable;
import java.util.Locale;

import javax.enterprise.context.SessionScoped;

/**
 * Essa classe é responsável por gerenciar o Locale de toda a aplicação do usuário através do escopo de sessão
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
