import {getJsessionId, getCurrentMessageTopic, setCurrentMessageTopic} from "./util.js";
import messagingTopicCallback from "./messagingTopicCallback.js";

export default async function connectionsTopicCallback(message, stompClient) {
    // called when the client receives a STOMP message from the server
    let sessionId = getJsessionId();
    console.log('Message received');
    let connectionStatus = await JSON.parse(message.body);
    console.log(`received connection status: ${connectionStatus.status}`);

    if (connectionStatus.status == 'connectionRequest' && confirm(`Принять запрос на подключение от ${connectionStatus.userId}?`)) {
        sendConnectionAccept(connectionStatus, stompClient);
    }
    else if (connectionStatus.status == 'connectionAccept') {
        sendConnectionConfirm(connectionStatus, stompClient);
        subscribeToMessageTopic(connectionStatus, sessionId, stompClient, true);
        getKeysOfConnected();
    }
    else if (connectionStatus.status == 'connectionConfirm') {
        subscribeToMessageTopic(connectionStatus, sessionId, stompClient, false);
        getKeysOfConnected();
    }
};

function sendConnectionAccept(connectionStatus, stompClient) {
    connectionStatus.status = 'connectionAccept';
    stompClient.publish({
        destination: "/app/requestConnection",
        body: JSON.stringify(connectionStatus)
    });
}

function subscribeToMessageTopic(connectionStatus, sessionId, stompClient, isSessionIdFirst) {
    document.querySelector('.connected').textContent = connectionStatus.userId;
    if (isSessionIdFirst) {
        setCurrentMessageTopic(`/topic/messages/${sessionId + document.querySelector('.connected').textContent}`);
    } else {
        setCurrentMessageTopic(`/topic/messages/${document.querySelector('.connected').textContent + sessionId}`);
    }
    stompClient.subscribe(getCurrentMessageTopic(), messagingTopicCallback);
}

function sendConnectionConfirm(connectionStatus, stompClient) {
    connectionStatus.status = 'connectionConfirm';
    stompClient.publish({
        destination: "/app/requestConnection",
        body: JSON.stringify(connectionStatus)
    });
}

async function getKeysOfConnected() {
    let url = `http://localhost:8080/connected-keys`;
    let response = await fetch(url);
    if (response.status == 200) {
        let keys = await response.json();
        document.querySelector('#connectedPublic').textContent = keys.publicKey;
        document.querySelector('#connectedN').textContent = keys.primesMultiplication;
    } else {
        console.log('failed to get public key');
        console.log( await response.text());
    }
}