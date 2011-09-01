package de.andreasschmitt.export

import de.andreasschmitt.export.exporter.Exporter
import de.andreasschmitt.export.exporter.ExportingException
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import javax.servlet.http.*

class ExportService {

    boolean transactional = true
    
    def exporterFactory

    public void export(String type, OutputStream outputStream, List objects, Map formatters, Map parameters) throws ExportingException {
    	export(type, outputStream, objects, null, null, formatters, parameters)
    }
    
    public void export(String type, OutputStream outputStream, List objects, List fields, Map labels, Map formatters, Map parameters) throws ExportingException {
    	Exporter exporter = exporterFactory.createExporter(type, fields, labels, formatters, parameters)
    	exporter.export(outputStream, objects)
    }
   
    public void export(String type, HttpServletResponse response, String filename, String extension, List objects, Map formatters, Map parameters) throws ExportingException {
    	export(type, response, filename, extension, objects, null, null, formatters, parameters)
    }

    public void export(String type, HttpServletResponse response, String filename, String extension, List objects, List fields, Map labels, Map formatters, Map parameters) throws ExportingException {
    	// Setup response
    	response.contentType = ConfigurationHolder.config.grails.mime.types[type]
		response.setHeader("Content-disposition", "attachment; filename=${filename}.${extension}")
    	
    	Exporter exporter = exporterFactory.createExporter(type, fields, labels, formatters, parameters)    	
    	exporter.export(response.outputStream, objects)
    }    

}
