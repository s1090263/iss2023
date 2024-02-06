package unibo.servicestatusgui;

import unibo.basicomm23.utils.CommUtils;

import java.util.*;

/*
Logica applicativa (domain core) della gui
Creata da ServiceFacadeController usando FacadeBuilder
 */
public class ApplguiCore {
    private   ActorOutIn outinadapter;
    private  String destActor = "";
    private int refusedRequests = 0;

    public ApplguiCore( ActorOutIn outinadapter ) {
        this.outinadapter = outinadapter;
        ApplSystemInfo.setup();
        destActor         = ApplSystemInfo.applActorName;
    }

    //returns payload of message as a list
    public static List<String> getPayload(String input) {
        List<String> resultList = new ArrayList<>();

        int startIndex = input.indexOf('(');
        int endIndex = input.lastIndexOf(')');

        if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
            String contentBetweenParentheses = input.substring(startIndex + 1, endIndex);
            String[] tokens = contentBetweenParentheses.split("\\s*,\\s*"); // Split by commas, trimming whitespace

            for (String token : tokens) {
                resultList.add(token);
            }
        }

        return resultList;
    }

    //get sender of request
    public static String getSender(String input) {
        int indexOfParenthesis = input.indexOf("(");

        if (indexOfParenthesis != -1) {
            return input.substring(0, indexOfParenthesis).trim();
        } else {
            // Return the original string if no parenthesis found
            return input.trim();
        }
    }

    //Chiamato da CoapObserver
    public void handleMsgFromActor(String msg, String requestId) {
        //CommUtils.outcyan("AGC | hanldeMsgFromActor " + msg + " requestId=" + requestId) ;
        if (Objects.equals(getSender(msg), "transporttrolley")){ //handle messages from transport trolley
            updateMsg(msg);
        }
        if (Objects.equals(getPayload(msg).get(0), "currentlyStored")) { //handle the currentlyStored message
            updateMsg(msg);
        }
        if (Objects.equals(getPayload(msg).get(0), "refusingRequest")){ //handle the refusingRequest message
            refusedRequests +=1;
            updateMsg("fridgeservice(refusingRequest," + refusedRequests +")");
        }
    }
    public void handleReplyMsg( String msg) { //IApplMessage msg
        //CommUtils.outred("AGC | handleReplyMsg " + msg  ) ;
        updateMsg( msg  );
    }

    public void updateMsg( String msg ) {
        //CommUtils.outblue("AGC updateMsg " + msg);
        outinadapter.sendToAll(msg);
    }

    //Handling Messages from the FacadeGUI
    public void handleWsMsg(String msg ) {
        //CommUtils.outcyan("AGC | handleWsMsg msg " + msg  );
        String[] parts = msg.split("/");
        String message = parts[0];
        String msgID = parts[1];
        String msgContent = "";
        if (parts.length > 2)
            msgContent = parts[2];
        //CommUtils.outcyan("AGC | handleWsMsg " + message  );

        switch (message) {
             case "request":
                dorequest(msgID, msgContent );
                break;
            case "cmd":
                docmd(msgContent );
                break;
            case "requestInfo":
                dorequestInfo();
                break;
            case "exit":
                System.exit(0);
                break;
            default:
                break;
        }
    }
 

    private void dorequest(String reqid, String reqcontent) {
        outinadapter.dorequest(
        "gui",destActor,reqid,reqcontent);
    }
    private void docmd(String payload ) {
        //outinadapter.docmd(payload );
    }
    private void dorequestInfo(  ) {

        //ApplSystemInfo.setup();
        List<String> actorNames = ApplSystemInfo.getActorNamesInApplCtx();
        //CommUtils.outgreen (" --- ServiceFacadeController | actorNames= "+actorNames  );

        FacadeBuilder.wsHandler.sendToAll(
                "ACTORS:" + actorNames.toString() +
                " interacting with:" + ApplSystemInfo.applActorName);

    }
}
