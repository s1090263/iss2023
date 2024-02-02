%====================================================================================
% alarmdevice description   
%====================================================================================
event( obstacle, obstacle(D) ).
event( free, free(D) ).
dispatch( stoptrolley, stoptrolley(ARG) ).
dispatch( resumetrolley, resumetrolley(ARG) ).
dispatch( coapUpdate, coapUpdate(RESOURCE,VALUE) ).
%====================================================================================
context(ctxalarmdevice, "localhost",  "TCP", "9980").
context(ctxcoldstorageservice, "127.0.0.1",  "TCP", "9990").
 qactor( fridgeservice, ctxcoldstorageservice, "external").
  qactor( transporttrolley, ctxcoldstorageservice, "external").
  qactor( sonar, ctxalarmdevice, "sonarSimulator").
 static(sonar).
  qactor( datacleaner, ctxalarmdevice, "rx.dataCleaner").
 static(datacleaner).
  qactor( distancefilter, ctxalarmdevice, "rx.distanceFilter").
 static(distancefilter).
  qactor( alarmdevice, ctxalarmdevice, "it.unibo.alarmdevice.Alarmdevice").
 static(alarmdevice).
  qactor( warningdevice, ctxalarmdevice, "it.unibo.warningdevice.Warningdevice").
 static(warningdevice).
