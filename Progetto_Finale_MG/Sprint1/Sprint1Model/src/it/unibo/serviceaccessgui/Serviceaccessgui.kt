/* Generated by AN DISI Unibo */ 
package it.unibo.serviceaccessgui

import it.unibo.kactor.*
import alice.tuprolog.*
import unibo.basicomm23.*
import unibo.basicomm23.interfaces.*
import unibo.basicomm23.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import it.unibo.kactor.sysUtil.createActor   //Sept2023
	
class Serviceaccessgui ( name: String, scope: CoroutineScope, bho: Boolean  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "so"
	}
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		val interruptedStateTransitions = mutableListOf<Transition>()
				return { //this:ActionBasciFsm
				state("so") { //this:State
					action { //it:State
						CommUtils.outmagenta("$name - START")
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition( edgeName="goto",targetState="sendRequest", cond=doswitch() )
				}	 
				state("sendRequest") { //this:State
					action { //it:State
						delay(3000) 
						CommUtils.outmagenta("$name - Sending store request: 30 Kg")
						request("storerequest", "storerequest(30)" ,"fridgeservice" )  
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition(edgeName="t00",targetState="sendTicket",cond=whenReply("loadaccepted"))
					transition(edgeName="t01",targetState="endWork",cond=whenReply("loadrefused"))
				}	 
				state("sendTicket") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("loadaccepted(TICKET)"), Term.createTerm("loadaccepted(TICKET)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								CommUtils.outmagenta("$name - Moving to the INDOOR")
								delay(4000) 
								 val Ticket="${payloadArg(0)}"  
								CommUtils.outmagenta("$name - Sending ticket: $Ticket")
								request("sendticket", "sendticket($Ticket)" ,"fridgeservice" )  
						}
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition(edgeName="t02",targetState="endWork",cond=whenReply("chargetaken"))
					transition(edgeName="t03",targetState="endWork",cond=whenReply("ticketrefused"))
				}	 
				state("endWork") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("loadrefused(_)"), Term.createTerm("loadrefused(_)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								CommUtils.outmagenta("$name - END WORK: load got refused")
						}
						if( checkMsgContent( Term.createTerm("ticketrefused(_)"), Term.createTerm("ticketrefused(_)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								CommUtils.outmagenta("$name - END WORK: ticket got refused")
						}
						if( checkMsgContent( Term.createTerm("chargetaken(_)"), Term.createTerm("chargetaken(_)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								CommUtils.outmagenta("$name - END WORK: food load taken")
						}
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
				}	 
			}
		}
}
