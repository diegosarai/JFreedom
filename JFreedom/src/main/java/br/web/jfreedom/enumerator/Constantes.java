package br.web.jfreedom.enumerator;

public enum Constantes {

	CONFIG_PATH(){
		public String toString() {
			return "/WEB-INF/user-config.xml";
		};
	},
	//Nome do atributo enviado para a JSP. Através desse atributo as tags Messages e Message saberão 
	//quais mensagens deverão aprensentar na tela
	SINGLE_VALIDATOR_ATTRIBUTE(){
		@Override
		public String toString() {
			return "singleValidator";
		}
	},
	//Nome to atributo enviado para a JSP. Através desse atributo a tag Messages exibirá as mensagens de 
	//validação em grupo
	GROUP_VALIDATOR_ATTRIBUTE(){
		@Override
		public String toString() {
			return "groupValidator";
		}
	},
	//Nome to atributo enviado para a JSP. Através desse atributo a tag Messages exibirá as mensagens de 
	//validação para campos não mapeados
	NOT_MAPPING_VALIDATOR_ATTRIBUTE(){
		@Override
		public String toString() {
			return "notMappingValidator";
		}
	}
	
	
}
