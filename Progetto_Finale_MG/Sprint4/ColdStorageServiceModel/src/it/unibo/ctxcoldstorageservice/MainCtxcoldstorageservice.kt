/* Generated by AN DISI Unibo */ 
package it.unibo.ctxcoldstorageservice
import it.unibo.kactor.QakContext
import it.unibo.kactor.sysUtil
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
	//System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "ERROR");
	QakContext.createContexts(
	        "localhost", this, "coldstorageservice.pl", "sysRules.pl", "ctxcoldstorageservice"
	)
	//JAN Facade
	unibo.servicefacade24.Servicefacade24Application.main( arrayOf<String>() );
	//JAN24 Display
}

