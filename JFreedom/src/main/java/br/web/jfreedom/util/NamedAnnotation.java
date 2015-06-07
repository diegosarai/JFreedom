package br.web.jfreedom.util;

import javax.enterprise.util.AnnotationLiteral;

import br.web.jfreedom.annotation.ControllerMapping;

public class NamedAnnotation extends AnnotationLiteral<ControllerMapping> implements ControllerMapping {

	private final String value;

    public NamedAnnotation(final String value) {
        this.value = value;
    }

    public String value() {
       return value;
   }	
	
}
