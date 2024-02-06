//guimain.js

const msgArea = document.getElementById("messageArea");
const capacityText = document.getElementById("cdStored");
console.log("msgArea=" + msgArea)

var socket = connect();

//function to get the payload of a message
function getPayload(inputString) {
    // Use a regular expression to match text between parentheses
    var match = inputString.match(/\((.*?)\)/);

    // Check if there is a match and return the content between parentheses, split by commas
    if (match && match[1]) {
        var textInsideParentheses = match[1];
        var textList = textInsideParentheses.split(',').map(function(item) {
            return item.trim(); // Trim to remove leading and trailing whitespaces
        });
        return textList;
    } else {
        return [];
    }
}

//function to get the sender of a message
function getSender(inputString) {
    // Use a regular expression to match text before the first opening parenthesis
    var match = inputString.match(/^(.*?)(?=\()/);

    // Check if there is a match and return the content before the parenthesis
    if (match && match[1]) {
        return match[1];
    } else {
        return "No sender found";
    }
}


function connect() {
    const host     = document.location.host;
    const pathname = document.location.pathname;
    const addr     = "ws://" + host + pathname + "accessgui";
    console.log("connect addr="+addr)
    // Assicura che sia aperta un unica connessione
    if (socket !== undefined && socket.readyState !== WebSocket.CLOSED) {
        alert("WARNING: Connessione WebSocket già stabilita");
    }
    socket = new WebSocket(addr); // CONNESSIONE

    socket.onopen = function (event) {
        //addTo_msgArea("Connessione " + addr + " ok");
        showMsg("Connessione " + addr + " ok");
    };
    socket.onerror = function (event) {
        //addTo_msgArea("ERROR " + event.data);
        showMsg("ERROR " + event.data);
    };
    socket.onmessage = function (event) { // RICEZIONE
        //let [type, payload, info] = event.data.split("/");
        console.log("main onmessage event=" + event.data)
        showMsg( `${event.data}`)
    };
    return socket;
}


function submitDepositRequest() {
     console.log("submitRequest "+ depositRequestInput.value);
    sendMessage("request/storerequest/storerequest(" + depositRequestInput.value + ")"); //arriva a ApplGuiCore via WSHandler
    depositRequestInput.value = "";
}
function submitTicketRequest() {
     console.log("submitRequest "+ ticketRequestInput.value);
    sendMessage("request/sendticket/sendticket(" + ticketRequestInput.value + ")"); //arriva a ApplGuiCore via WSHandler
    ticketRequestInput.value = "";
}

function submitRequestInfo() {
     console.log("submitRequestInfo " );
    sendMessage("requestInfo/actors"  ); //arriva a ApplGuiCore via WSHandler

}

function submitTheCmd() {
      //console.log("submitCmd "+ cmdInput.value);
      sendMessage("cmd/" + cmdInput.value);
      cmdInput.value = "";
}

function submitTheExit() {
      sendMessage("exit/ok"  );
      cmdInput.value = "";
}

function sendMessage(message) {
    console.log("sendMessage "+ message);
    socket.send(message);
}

function showMsg(message) {
  switch (getSender(message)) {
      case "fridgeservice" :
        if (getPayload(message)[0] == "chargeTaken")
            msgArea.innerHTML +=  '- Charge Taken of ticket: ' + getPayload(message)[1] + '\n';
        else
            capacityText.innerText =  getPayload(message)[1];
          break;
      case "loadaccepted" :
          msgArea.innerHTML +=  '- The Store request has been accepted. Your ticket number is: ' + getPayload(message)[0] + '\n'
          break;
      case "loadrefused" :
                msgArea.innerHTML +=  '- The Store request has been refused (not enough space) \n'
                break;
      case "ticketaccepted" :
                msgArea.innerHTML +=  '- The ticket ' +getPayload(message)[0] + ' has been accepted. Taking charge...\n'
                break;
      case "ticketrefused" :
                if (getPayload(message)[0] == "expired")
                    msgArea.innerHTML +=  '- The ticket has been refused (expired)\n'
                else
                     msgArea.innerHTML +=  '- The ticket number is inexistent \n'
                break;
      default :
          console.log("Not handled message");
    }
}

function clear_messageArea(){
    msgArea.innerHTML = ""
}
function exit(){

}