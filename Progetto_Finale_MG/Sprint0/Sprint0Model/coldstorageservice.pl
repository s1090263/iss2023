%====================================================================================
% coldstorageservice description   
%====================================================================================
request( engage, engage(OWNER,STEPTIME) ).
dispatch( disengage, disengage(ARG) ).
dispatch( cmd, cmd(MOVE) ).
dispatch( end, end(ARG) ).
request( step, step(TIME) ).
request( doplan, doplan(PATH,OWNER,STEPTIME) ).
request( moverobot, moverobot(TARGETX,TARGETY) ).
dispatch( setrobotstate, setpos(X,Y,D) ).
dispatch( setdirection, dir(D) ).
request( getrobotstate, getrobotstate(ARG) ).
request( storerequest, storerequest(kg) ).
dispatch( gomoveToIndoor, gomoveToIndoor(_) ).
dispatch( coapUpdate, coapUpdate(SOURCE,ARG) ).
dispatch( stop, stop(_) ).
dispatch( resume, resume(_) ).
%====================================================================================
context(ctxcoldstorageservice, "localhost",  "TCP", "9990").
context(ctxbasicrobot, "127.0.0.1",  "TCP", "8090").
context(ctxrasp, "localhost",  "TCP", "8070").
context(ctxdriver, "localhost",  "TCP", "8060").
 qactor( basicrobot, ctxbasicrobot, "external").
  qactor( fridgeservice, ctxcoldstorageservice, "it.unibo.fridgeservice.Fridgeservice").
  qactor( serviceaccessgui, ctxcoldstorageservice, "it.unibo.serviceaccessgui.Serviceaccessgui").
  qactor( alarmdevice, ctxrasp, "it.unibo.alarmdevice.Alarmdevice").
  qactor( fridgetruckdriver, ctxdriver, "it.unibo.fridgetruckdriver.Fridgetruckdriver").
  qactor( transporttrolley, ctxcoldstorageservice, "it.unibo.transporttrolley.Transporttrolley").
