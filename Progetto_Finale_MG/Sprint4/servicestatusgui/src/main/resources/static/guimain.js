//guimain.js

const msgArea = document.getElementById("messageArea");
const capacityText = document.getElementById("cdStored");
const maxCapacityText = document.getElementById("cdMax");
const stateText = document.getElementById("state");
const refusedText = document.getElementById("refused");
const positionText = document.getElementById("position");
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
  console.log("received: " + message)
  switch (getSender(message)) {
      case "fridgeservice" :
        if (getPayload(message)[0] == "refusingRequest")
            refusedText.innerText =  getPayload(message)[1];
        else{
            capacityText.innerText =  getPayload(message)[1];
            maxCapacityText.innerText = getPayload(message)[2];
           }
          break;
        case "transporttrolley" :
                if (getPayload(message)[0] == "position")
                    positionText.innerText =  "[" + getPayload(message)[1] + "," + getPayload(message)[2] + "]";
                else
                    stateText.innerText =  getPayload(message)[0];
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