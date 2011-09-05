package de.andreasschmitt.export.exporter

import groovy.xml.MarkupBuilder
import org.codehaus.groovy.grails.orm.hibernate.cfg.GrailsHibernateUtil

/**
 * @author Andreas Schmitt
 * @author Alexei Bratuhin
 * 
 */
class DefaultXMLExporter extends AbstractExporter {
	
	protected void exportData(OutputStream outputStream, List data, List fields) throws ExportingException{
		try {
			// Get stream writer considering charsets
			Writer writer = getOutputStreamWriter(outputStream)
			def builder = new MarkupBuilder(writer)
			
			if(data.size() > 0){
				int depth = 1
				
				// Depth for building XML tree
				if(getParameters().containsKey("depth")){
					try {
						depth = new Integer(getParameters().get("depth") + "")
					}
					catch (Exception e){
						depth = 1
					}
				}			
				
				// Root element name defaults to object class name
				String rootElement = "${properCase(data[0]?.getClass()?.simpleName)}s"
				
				// Root element name
				if(getParameters().containsKey("xml.root")){
					// Set root element name and start building
					rootElement = getParameters().get("xml.root")
				}
				
				// Start building
				build(rootElement, builder, data, fields, depth)	
			}
			
			writer.flush()
		}
		catch(Exception e){
			throw new ExportingException("Error during export", e)
		}
	}
	
	private String properCase(String value){
		if(value?.length() >= 2){
			return "${value[0].toLowerCase()}${value.substring(1)}"	
		}
		
		return value?.toLowerCase()
	}
	
	private void build(String node, builder, Collection data, List fields, int depth){
		if(depth >= 0 && data.size() > 0){
			//Root element
			builder."${properCase(node)}"{
				//Iterate through data
				data.each { object ->
					//This fixes an issue with Hibernate proxies
					def className = GrailsHibernateUtil.unwrapIfProxy(object).getClass()?.simpleName
					//Object element
					"${properCase(className)}"(id: object?.id){
						//Object attributes
						fields.each { field ->
							String elementName = getLabel(field)
							
							Object value = getValue(object, field)
							
							// Check whether the domain class specifies attributes to export
							if(object.metaClass.hasProperty(object, 'exportables')){
								// Check whether current field in list of exportables
								if (field in object.getClass().exportables){
									if(value instanceof Collection){
										if(value.size() > 0){
											this.build(field, builder, value, ExporterUtil.getFields(value.toArray()[0]), depth - 1)
										}
										else {
											"${elementName}"()
										}
									}
									else {
										"${elementName}"(value?.toString())
									}
								}
							}
						}	
					}
				}
			}
		}
	}
}