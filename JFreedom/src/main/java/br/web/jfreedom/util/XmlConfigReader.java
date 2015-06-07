package br.web.jfreedom.util;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import br.web.jfreedom.enumerator.XmlConfigTags;
import br.web.jfreedom.vo.MappingClassVO;
import br.web.jfreedom.vo.MessageVO;

public class XmlConfigReader extends DefaultHandler{

	private XmlConfigTags tags;
	private List<MappingClassVO> mappingList;
	private MappingClassVO mappingClass;
	private MessageVO messageVO;
	
	{
		mappingList = new ArrayList<MappingClassVO>();
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {

		if(qName.trim().equalsIgnoreCase(XmlConfigTags.MAPPING.toString())){
			tags = XmlConfigTags.MAPPING;
		}
	
		if(qName.trim().equalsIgnoreCase(XmlConfigTags.CLASS.toString())){
			
			mappingClass = new MappingClassVO();
			
			tags = XmlConfigTags.CLASS;
		}
		
		if(qName.trim().equalsIgnoreCase(XmlConfigTags.MESSAGE_CONFIG.toString())){
			
			messageVO = new MessageVO();
			
			tags = XmlConfigTags.MESSAGE_CONFIG;
		}
		
		if(qName.trim().equalsIgnoreCase(XmlConfigTags.PATH.toString())){
			
			tags = XmlConfigTags.PATH;
		}

		if(qName.trim().equalsIgnoreCase(XmlConfigTags.VAR.toString())){
			
			tags = XmlConfigTags.VAR;
		}
		
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
	
	}
	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
	
		String value = new String(ch,start,length);
		
		if(tags == XmlConfigTags.CLASS){
			
			mappingClass.setClassFqn(value);
			mappingList.add(mappingClass);
			
			tags = null;
		}
	
		if(tags == XmlConfigTags.PATH){
			
			messageVO.setPath(value);
		
			tags = null;
		}

		if(tags == XmlConfigTags.VAR){
			
			messageVO.setVar(value);
		
			tags = null;
		}

	}

	public List<MappingClassVO> getMappingList() {
		return mappingList;
	}

	public MessageVO getMessageVO() {
		return messageVO;
	}

	
	
}
