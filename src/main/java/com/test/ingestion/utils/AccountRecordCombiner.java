package com.test.ingestion.utils;

import java.util.Properties;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

public class AccountRecordCombiner {

	public static void main(String[] args) {
		
		//logger = ExecutionUtil.getBaseLogger();
		//execProps = ExecutionManager.getCurrent().getProperties();

		String operation = "I";
		Element megaDocRoot = new Element("AccountMegaDoc");
		Document outputDoc = new Document();
		
		Properties props = null;
		
		for( int i = 0; i < 1; i++ ) {
			//InputStream is = dataContext.getStream(i);
			SAXBuilder builder = new SAXBuilder();
			//Document inputDoc = builder.build(is);

			//List<Element> childElems = inputDoc.getRootElement().getChildren();
			///inputDoc.getRootElement().setAttribute(new Attribute("isParent","true"));
			//inputDoc.getRootElement().addContent(new Element("operation").addContent(operation));
			
			//for (Element childElem : childElems) {
			//	megaDocRoot.addContent(childElem);
			//}
			//inputDoc = outputDoc;
			//props = dataContext.getProperties(i);
		}
		
		outputDoc.setRootElement(megaDocRoot);
		XMLOutputter outputter = new XMLOutputter();
		//dataContext.storeStream(new ByteArrayInputStream(outputter.outputString(outputDoc).getBytes("UTF-8")), props);	
	}
	
}
