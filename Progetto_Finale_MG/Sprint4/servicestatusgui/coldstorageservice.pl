%====================================================================================
% coldstorageservice description   
%====================================================================================
request( engage, engage(OWNER,STEPTIME) ).
dispatch( disengage, disengage(ARG) ).
request( moverobot, moverobot(TARGETX,TARGETY) ).
dispatch( setrobotstate, setpos(X,Y,D) ).
dispatch( setdirection, dir(D) ).
event( alarm, alarm(X) ).
request( storerequest, storerequest(KG) ).
request( sendticket, sendticket(TICKET) ).
request( takecharge, takecharge(TICKET) ).
dispatch( tryagain, tryagain(ARG) ).
dispatch( stoptrolley, stoptrolley(ARG) ).
dispatch( resumetrolley, resumetrolley(ARG) ).
%====================================================================================
context(ctxcoldstorageservice, "localhost",  "TCP", "9990").
context(ctxbasicrobot, "127.0.0.1",  "TCP", "8020").
 qactor( basicrobot, ctxbasicrobot, "external").
  qactor( transporttrolley, ctxcoldstorageservice, "it.unibo.transporttrolley.Transporttrolley").
 static(transporttrolley).
  qactor( fridgeservice, ctxcoldstorageservice, "it.unibo.fridgeservice.Fridgeservice").
 static(fridgeservice).
