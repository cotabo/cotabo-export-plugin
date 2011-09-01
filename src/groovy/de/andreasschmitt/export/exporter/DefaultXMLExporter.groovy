package de.andreasschmitt.export.exporter

import groovy.xml.MarkupBuilder

/**
 * @author Andreas Schmitt
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
		if(depth >= 0 && data.size() > 0 && node != 'transients'){
			//Root element
			builder."${properCase(node)}"{
				//Iterate through data
				data.each { object ->
					//Object element
					"${properCase(object?.getClass()?.simpleName)}"(id: object?.id){
						//Object attributes
						fields.each { field ->
							String elementName = getLabel(field)
							
							if(elementName == '$const$0') return true;
							
							Object value = getValue(object, field)
							
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