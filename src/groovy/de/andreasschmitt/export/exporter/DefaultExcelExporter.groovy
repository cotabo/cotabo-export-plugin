package de.andreasschmitt.export.exporter

import de.andreasschmitt.export.builder.ExcelBuilder

/**
 * @author Andreas Schmitt
 * 
 */
class DefaultExcelExporter extends AbstractExporter {

	protected void exportData(OutputStream outputStream, List data, List fields) throws ExportingException{
		try {
			def builder = new ExcelBuilder()
			
			builder {
				workbook(outputStream: outputStream){
					sheet(name: getParameters().get("title") ?: "Export", widths: getParameters().get("column.widths")){
						//Default format
						format(name: "header"){
							font(name: "arial", bold: true)
						}
						
						//Create header
						fields.eachWithIndex { field, index ->
							String value = getLabel(field)
							cell(row: 0, column: index, value: value, format: "header")	
						}
						
						//Rows
						data.eachWithIndex { object, k ->
							fields.eachWithIndex { field, i ->
								Object value = getValue(object, field)
								cell(row: k + 1, column: i, value: value)
							}
						}
					}
				}
			}
			
			builder.write()
		}
		catch(Exception e){
			throw new ExportingException("Error during export", e)
		}
	}
	
}