package br.web.jfreedom.enumerator;

public enum Constantes {

	CONFIG_PATH(){
		public String toString() {
			return "/WEB-INF/user-config.xml";
		};
	},
	//Nome do atributo enviado para a JSP. Atrav�s desse atributo as tags Messages e Message saber�o 
	//quais mensagens dever�o aprensentar na tela
	SINGLE_VALIDATOR_ATTRIBUTE(){
		@Override
		public String toString() {
			return "singleValidator";
		}
	},
	//Nome to atributo enviado para a JSP. Atrav�s desse atributo a tag Messages exibir� as mensagens de 
	//valida��o em grupo
	GROUP_VALIDATOR_ATTRIBUTE(){
		@Override
		public String toString() {
			return "groupValidator";
		}
	},
	//Nome to atributo enviado para a JSP. Atrav�s desse atributo a tag Messages exibir� as mensagens de 
	//valida��o para campos n�o mapeados
	NOT_MAPPING_VALIDATOR_ATTRIBUTE(){
		@Override
		public String toString() {
			return "notMappingValidator";
		}
	}
	
	
}
