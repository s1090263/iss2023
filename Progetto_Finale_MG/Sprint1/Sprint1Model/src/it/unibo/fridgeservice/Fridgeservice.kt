/* Generated by AN DISI Unibo */ 
package it.unibo.fridgeservice

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
	
class Fridgeservice ( name: String, scope: CoroutineScope , bho: Boolean ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "so"
	}
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		val interruptedStateTransitions = mutableListOf<Transition>()
		
				val MAXW = 100 //max storable kg in the ColdRoom
				val TICKETTIME = 20 //seconds of ticket validity
				var CurrentlyStored : Float = 0f //kg stored in the ColdRoom	
				val openRequestList =  mutableListOf<Triple<Int, Float, Long>?>()	//structure to mantain the ticket requests that are open (<Ticket number, KG, EmissionTime>)
				var ticketValue = 0 //incrementing ticket value
				return { //this:ActionBasciFsm
				state("so") { //this:State
					action { //it:State
						delay(1000) 
						delegate("chargetaken", "serviceaccessgui") 
						CommUtils.outblue("$name - START")
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition( edgeName="goto",targetState="waitRequest", cond=doswitch() )
				}	 
				state("waitRequest") { //this:State
					action { //it:State
						CommUtils.outblue("$name - waiting for requests...")
						updateResourceRep( "fridgeservice(waiting requests)"  
						)
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition(edgeName="t019",targetState="handleRequest",cond=whenRequest("storerequest"))
					transition(edgeName="t020",targetState="handleTicket",cond=whenRequest("sendticket"))
				}	 
				state("handleRequest") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("storerequest(KG)"), Term.createTerm("storerequest(KG)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								if(  payloadArg(0).toFloat() < MAXW - CurrentlyStored  
								 ){ val Ticket= ticketValue
													ticketValue = ticketValue + 1
								CommUtils.outblue("$name - accepting request of ${payloadArg(0)} Kg, returning ticket: $Ticket")
								updateResourceRep( "fridgeservice(accepting request)"  
								)
								answer("storerequest", "loadaccepted", "loadaccepted($Ticket)","serviceaccessgui"   )  
								 CurrentlyStored += payloadArg(0).toFloat()  
								CommUtils.outblue("$name - After the load, there will be $CurrentlyStored Kg out of $MAXW in the ColdRoom")
								 openRequestList.add(Triple(Ticket, payloadArg(0).toFloat() , System.currentTimeMillis()))  
								}
								else
								 {CommUtils.outblue("$name - refusing request of ${payloadArg(0)} Kg (Not enough room) ")
								 updateResourceRep( "fridgeservice(refusing request)"  
								 )
								 answer("storerequest", "loadrefused", "loadrefused(_)","serviceaccessgui"   )  
								 }
						}
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition( edgeName="goto",targetState="waitRequest", cond=doswitch() )
				}	 
				state("handleTicket") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("sendticket(TICKET)"), Term.createTerm("sendticket(TICKET)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 
												val Ticket = payloadArg(0).toInt()
												val request = openRequestList.find { it?.first == Ticket }			
												val elapsedTime = (System.currentTimeMillis() - request!!.third) / 1000 //elapsed time in seconds			
												val Kg = request.second //load of this request
								if(  elapsedTime <= TICKETTIME  
								 ){CommUtils.outblue("$name - accepting ticket $Ticket of request for $Kg Kg. Asking trolley to take charge")
								updateResourceRep( "fridgeservice(accepting ticket)"  
								)
								request("takecharge", "takecharge($Ticket)" ,"transporttrolley" )  
								}
								else
								 {CommUtils.outblue("$name - refusing ticket $Ticket of request for $Kg Kg (ticket expired)")
								 updateResourceRep( "fridgeservice(refusing ticket)"  
								 )
								 answer("sendticket", "ticketrefused", "ticketrefused(_)","serviceaccessgui"   )  
								  CurrentlyStored -= Kg  
								 }
								 openRequestList.remove(request)  
						}
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition( edgeName="goto",targetState="waitRequest", cond=doswitch() )
				}	 
			}
		}
}
