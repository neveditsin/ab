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

        SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        InputStream schemaLocation = Validator.class.getResourceAsStream("/com/nc/resources/servmon.xsd");
        
       
        Schema sch = factory.newSchema(new StreamSource(schemaLocation));    
        javax.xml.validation.Validator validator = sch.newValidator();        
        Source source = new StreamSource(xmlPath);
        validator.validate(source);        
    }
}
