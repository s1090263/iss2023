/* Generated by AN DISI Unibo */ 
package it.unibo.transporttrolley

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
	
class Transporttrolley ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "s0"
	}
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		val interruptedStateTransitions = mutableListOf<Transition>()
				return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						CommUtils.outyellow("$name | START, engage basicrobot")
						request("engage", "engage(transporttrolley,330)" ,"basicrobot" )  
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition(edgeName="s02",targetState="waitCmd",cond=whenReply("engagedone"))
				}	 
				state("waitCmd") { //this:State
					action { //it:State
						CommUtils.outyellow("$name | waiting for cmd...")
						updateResourceRep( "$name(waiting) "  
						)
						 CommUtils.waitTheUser("$name wait cmd. Pleas HIT ") 
						forward("gomoveToIndoor", "gomoveToIndoor(26)" ,"transporttrolley" ) 
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition(edgeName="t03",targetState="moveToIndoor",cond=whenDispatch("gomoveToIndoor"))
				}	 
				state("moveToIndoor") { //this:State
					action { //it:State
						CommUtils.outgreen("$name | moveToIndoor")
						request("moverobot", "moverobot(0,4)" ,"basicrobot" )  
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition(edgeName="t04",targetState="loadTheCharge",cond=whenReply("moverobotdone"))
				}	 
				state("loadTheCharge") { //this:State
					action { //it:State
						updateResourceRep( "$name(loading)"  
						)
						CommUtils.outgreen("$name | loading charge ...")
						 CommUtils.waitTheUser("$name loading charge. Please HIT ")  
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition( edgeName="goto",targetState="moveToColdRoom", cond=doswitch() )
				}	 
				state("moveToColdRoom") { //this:State
					action { //it:State
						request("moverobot", "moverobot(4,3)" ,"basicrobot" )  
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition(edgeName="t05",targetState="storeTheCharge",cond=whenReply("moverobotdone"))
				}	 
				state("storeTheCharge") { //this:State
					action { //it:State
						updateResourceRep( "$name(storing)"  
						)
						CommUtils.outgreen("$name | storing charge ...")
						 CommUtils.waitTheUser("$name storing charge. Please HIT") 
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition( edgeName="goto",targetState="moveToHome", cond=doswitch() )
				}	 
				state("moveToHome") { //this:State
					action { //it:State
						CommUtils.outgreen("$name | movetoHome ...")
						request("moverobot", "moverobot(0,0)" ,"basicrobot" )  
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition(edgeName="t06",targetState="trolleyAtHome",cond=whenReply("moverobotdone"))
				}	 
				state("trolleyAtHome") { //this:State
					action { //it:State
						CommUtils.outgreen("$name | trolleyAtHome ... ")
						forward("setdirection", "dir(down)" ,"basicrobot" ) 
						updateResourceRep( "$name(athome)"  
						)
						delay(1000) 
						forward("disengage", "disengage(transporttrolley)" ,"basicrobot" ) 
						delay(1000) 
						 System.exit(0)  
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
				}	 
			}
		}
}