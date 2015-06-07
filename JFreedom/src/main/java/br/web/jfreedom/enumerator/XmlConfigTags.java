package br.web.jfreedom.enumerator;

public enum XmlConfigTags {

	MAPPING(){
		@Override
		public String toString() {
			return "mapping";
		}
	}, CLASS(){
		@Override
		public String toString() {
			return "class";
		}
	}, MESSAGE_CONFIG(){
		@Override
		public String toString() {
			return "message-config";
		}
	}, PATH(){
		@Override
		public String toString() {
			return "path";
		}
	}, VAR(){
		@Override
		public String toString() {
			return "var";
		}
	}
}
