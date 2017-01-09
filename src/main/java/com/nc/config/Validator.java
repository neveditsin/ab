package com.nc.config;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

public class Validator {
    public static void validate(String xmlPath) throws SAXException, IOException {

        // 1. Lookup a factory for the W3C XML Schema language
        SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        
        // 2. Compile the schema. 
        InputStream schemaLocation = Validator.class.getResourceAsStream("/com/nc/resources/servmon.xsd");
        
       
        Schema sch = factory.newSchema(new StreamSource(schemaLocation));
    
        // 3. Get a validator from the schema.
        javax.xml.validation.Validator validator = sch.newValidator();
        
        // 4. Parse the document you want to check.
        Source source = new StreamSource(xmlPath);
        
        // 5. Check the document

        validator.validate(source);

        
    }
}
