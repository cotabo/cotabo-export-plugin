package de.andreasschmitt.export.exporter

import au.com.bytecode.opencsv.CSVWriter

/**
 * @author Andreas Schmitt
 * 
 */
class DefaultCSVExporter extends AbstractExporter {
	
	char separator = ','
	char quoteCharacter = '"'
	String lineEnd = CSVWriter.DEFAULT_LINE_END
	
	protected void exportData(OutputStream outputStream, List data, List fields) throws ExportingException{
		try {
			if(getParameters().containsKey("separator")){
				separator = getParameters().get("separator")
			}
			if(getParameters().containsKey("quoteCharacter")){
				quoteCharacter = getParameters().get("quoteCharacter")
			}
            if(getParameters().containsKey("lineEnd")){
                lineEnd = getParameters().get("lineEnd")
            }
			
			// Get stream writer considering charsets
			CSVWriter writer = new CSVWriter(getOutputStreamWriter(outputStream), separator, quoteCharacter, lineEnd)
			
			//Create header
			List header = []
			fields.each { field ->
				String value = getLabel(field)
				header.add(value)
			}
			writer.writeNext(header as String[])
			
			//Rows
			data.each { object ->
				List row = []
				
				fields.each { field ->
					String value = getValue(object, field)?.toString()
					row.add(value)
				}
				
				writer.writeNext(row as String[])
			}
			
			writer.flush()
		}
		catch(Exception e){
			throw new ExportingException("Error during export", e)
		}
	}
}