/* Generated by AN DISI Unibo */ 
package it.unibo.warningdevice

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

//User imports JAN2024

class Warningdevice ( name: String, scope: CoroutineScope, isconfined: Boolean=false  ) : ActorBasicFsm( name, scope, confined=isconfined ){

	override fun getInitialState() : String{
		return "s0"
	}
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		//val interruptedStateTransitions = mutableListOf<Transition>()
		 var process = Runtime.getRuntime().exec("python LedOff.py")  
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						CommUtils.outmagenta("$name - START")
						CoapObserverSupport(myself, "192.168.1.7","9990","ctxcoldstorageservice","transporttrolley","coapUpdate")
						CoapObserverSupport(myself, "192.168.1.7","9990","ctxcoldstorageservice","fridgeservice","coapUpdate")
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition( edgeName="goto",targetState="observing", cond=doswitch() )
				}	 
				state("observing") { //this:State
					action { //it:State
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition(edgeName="t02",targetState="doObserve",cond=whenDispatch("coapUpdate"))
				}	 
				state("doObserve") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("coapUpdate(RESOURCE,VALUE)"), Term.createTerm("coapUpdate(transporttrolley,ARG)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 var Message : String = "${payloadArg(1)}"
											   var Payload : String = SystemUtilities.getPayloadArgs(Message)[0]
								if(  Payload == "waitingRequest" || Payload == "atHome" 
								 ){CommUtils.outmagenta("$name - LED OFF")
								
													process.destroy()
													process = Runtime.getRuntime().exec("python LedOff.py") 
								updateResourceRep( "warningdevice(off)"  
								)
								}
								if(  Payload == "movingToIndoor" || Payload == "loadingCharge" || 
												Payload == "chargeTaken" || Payload == "movingToColdRoom"|| Payload == "depositingLoad"
												 || Payload == "MovingToHome"  
								 ){CommUtils.outmagenta("$name - LED BLINKING")
								
														process.destroy()
														process = Runtime.getRuntime().exec("python LedBlink.py") 
								updateResourceRep( "warningdevice(blink)"  
								)
								}
						}
						if( checkMsgContent( Term.createTerm("coapUpdate(RESOURCE,VALUE)"), Term.createTerm("coapUpdate(fridgeservice,ARG)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 var Message : String = "${payloadArg(1)}"
											   var Payload : String = SystemUtilities.getPayloadArgs(Message)[0]
								if(  Payload == "stoppingTrolley" 
								 ){CommUtils.outmagenta("$name - LED ON")
								
												process.destroy()
												process = Runtime.getRuntime().exec("python LedOn.py") 
								updateResourceRep( "warningdevice(on)"  
								)
								}
								if(  Payload == "resumingTrolley" 
								 ){CommUtils.outmagenta("$name - LED BLINKING")
								
													process.destroy()
													process = Runtime.getRuntime().exec("python LedBlink.py") 
								updateResourceRep( "warningdevice(blink)"  
								)
								}
						}
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition( edgeName="goto",targetState="observing", cond=doswitch() )
				}	 
			}
		}
} 
