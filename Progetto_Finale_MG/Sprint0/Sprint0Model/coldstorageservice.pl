%====================================================================================
% coldstorageservice description   
%====================================================================================
request( engage, engage(OWNER,STEPTIME) ).
dispatch( disengage, disengage(ARG) ).
request( storerequest, storerequest(KG) ).
request( sendticket, sendticket(TICKET) ).
%====================================================================================
context(ctxcoldstorageservice, "localhost",  "TCP", "9990").
context(ctxbasicrobot, "127.0.0.1",  "TCP", "8020").
 qactor( basicrobot, ctxbasicrobot, "external").
  qactor( serviceaccessgui, ctxcoldstorageservice, "it.unibo.serviceaccessgui.Serviceaccessgui").
  qactor( transporttrolley, ctxcoldstorageservice, "it.unibo.transporttrolley.Transporttrolley").
  qactor( fridgeservice, ctxcoldstorageservice, "it.unibo.fridgeservice.Fridgeservice").
