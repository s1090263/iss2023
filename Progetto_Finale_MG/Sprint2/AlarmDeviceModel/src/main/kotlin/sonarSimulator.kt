import it.unibo.kactor.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import unibo.basicomm23.interfaces.IApplMessage
import unibo.basicomm23.utils.CommUtils

/*
-------------------------------------------------------------------------------------------------
sonarSimulator.kt
-------------------------------------------------------------------------------------------------
 */

class sonarSimulator ( name : String ) : ActorBasic( name ) {
	init{
		//autostart
		runBlocking{  autoMsg("simulatorstart","do") }
	}
//@kotlinx.coroutines.ObsoleteCoroutinesApi

    override suspend fun actorBody(msg : IApplMessage){
  		println("$tt $name | received  $msg "  )
		if( msg.msgId() == "simulatorstart") startDataReadSimulation(   )
     }
  	
//@kotlinx.coroutines.ObsoleteCoroutinesApi

	suspend fun startDataReadSimulation(    ){
		//generate the distances to use (it goes from 80 to 20 and from 20 to 60)
		val data = generateSequence(80) { it - 5 }
        .takeWhile { it >= 20 }
        .toList() +
            generateSequence(20) { it + 5 }
                .takeWhile { it <= 40 }
                .toList() +
		generateSequence(40) { it + 5 }
			.takeWhile { it <= 35 }
			.toList() +
		generateSequence(35) { it + 5 }
			.takeWhile { it <= 60 }
			.toList()

		var i = 0
			while( i < 23 ){
 	 			val m1 = "distance( ${data.elementAt(i)} )"
				i++
 				val event = CommUtils.buildEvent( name,"sonardistance",m1)
  				emitLocalStreamEvent( event )
 				println("$tt $name | generates $event")
 				//emit(event)  //APPROPRIATE ONLY IF NOT INCLUDED IN A PIPE
 				delay( 1000 )
  			}			
			terminate()
	}

} 

//@kotlinx.coroutines.ObsoleteCoroutinesApi
//
//fun main() = runBlocking{
// //	val startMsg = MsgUtil.buildDispatch("main","start","start","datasimulator")
//	val consumer  = dataConsumer("dataconsumer")
//	val simulator = sonarSimulator( "datasimulator" )
//	val filter    = dataFilter("datafilter", consumer)
//	val logger    = dataLogger("logger")
//	simulator.subscribe( logger ).subscribe( filter ).subscribe( consumer ) 
//	MsgUtil.sendMsg("start","start",simulator)
//	simulator.waitTermination()
// } 