package br.web.jfreedom.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class Util {

	/**
	 * Método que verifica se uma string passada está vazia ou não
	 * @param field
	 * @return
	 */
	public static boolean isEmpty(String field){
		
		return field == null || field.trim().equals("");
	}
	/**
	 * Método responsável por ler arquivo passado através do atributo path
	 * 
	 * @param path Caminho do arquivo de configurações definido pelo usuário (user-config.xml)
	 * @return
	 * @throws IOException
	 */
	public static String readFile(String filePath) throws IOException{
	
		StringBuilder conteudoArquivo = new StringBuilder();
		
		try(BufferedReader leitor = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)))){
			
			String linha = null;
			
			while((linha = leitor.readLine()) != null){
				
				conteudoArquivo.append(linha);
			
			}
			
			
		}
		
		return conteudoArquivo.toString();
	}
	
	/**
	 * Leitura de um arquivo XML utilizando SAX
	 * 
	 * @param handler - Handler que irá gerenciar a leitura do arquivo XML pelo SAX
	 * @param fileContent - Conteúdo String do arquivo que será convertido para objeto através do parse.
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static void xmlSaxParse(DefaultHandler handler, String fileContent) throws ParserConfigurationException, SAXException, IOException{
		
		SAXParserFactory factory = SAXParserFactory.newInstance();
		
		SAXParser parser = factory.newSAXParser();
		XMLReader reader = parser.getXMLReader();
		
		reader.setContentHandler(handler);
		reader.parse(new InputSource(new StringReader(fileContent)));
		
	}
}
