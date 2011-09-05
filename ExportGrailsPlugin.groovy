import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.apache.commons.logging.LogFactory

class ExportGrailsPlugin {
	def version = "1.1.1" // added by set-version
    def dependsOn = [:]
	def grailsVersion = "1.3 > *"

    def author = "Andreas Schmitt"
    def authorEmail = "andreas.schmitt.mi@gmail.com"
    def title = "Export functionality"
    def description = '''\
This plugin offers export functionality supporting different formats e.g. CSV, Excel, Open Document Spreadsheet, PDF and XML 
and can be extended to add additional formats. 
'''

    // URL to the plugin's documentation
    def documentation = "http://grails.org/Export+Plugin"

    def doWithSpring = {        
		//This is only necessary here, because later on log is injected by Spring
		def log = LogFactory.getLog(ExportGrailsPlugin)
		 
		"exporterFactory"(de.andreasschmitt.export.exporter.DefaultExporterFactory)
		
		try {				
			ExportConfig.exporters.each { key, value ->
		  		try {
		  			//Override default renderer configuration 
		  			if(ConfigurationHolder.config?.export."${key}"){
		  				value = ConfigurationHolder.config.export."${key}"
		  			}
		  			
		      		Class clazz = Class.forName(value, true, new GroovyClassLoader())
		      		
		      		//Add to spring
		      		"$key"(clazz)	
		  		}
		  		catch(ClassNotFoundException e){
		  			log.error("Couldn't find class: ${value}", e)
		  		}
			}			
		}
		catch(Exception e){
			log.error("Error initializing Export plugin", e)
		}
		catch(Error e){
			//Strange error which happens when using generate-all and hibernate.cfg
			log.error("Error initializing Export plugin")
		}
    }
   
    def doWithApplicationContext = { applicationContext ->
        // TODO Implement post initialization spring config (optional)		
    }

    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional)
    }
	                                      
    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
    }
	
    def onChange = { event ->
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }
}
