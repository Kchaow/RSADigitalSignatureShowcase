import * as util from "./util.js";
import messagingTopicCallback from "./messagingTopicCallback.js";

export default async function connectionsTopicCallback(message, stompClient) {
    // called when the client receives a STOMP message from the server
    let sessionId = util.getJsessionId();
    console.log('Message received');
    let connectionStatus = await JSON.parse(message.body);
    console.log(`received connection status: ${connectionStatus.status}`);
    console.log(util.CONNECTION_REQUEST);
    if (connectionStatus.status == util.CONNECTION_REQUEST && confirm(`Принять запрос на подключение от ${connectionStatus.userId}?`)) {
        sendConnectionAccept(connectionStatus, stompClient);
    }
    else if (connectionStatus.status == util.CONNECTION_ACCEPT) {
        sendConnectionConfirm(connectionStatus, stompClient);
        subscribeToMessageTopic(connectionStatus, sessionId, stompClient, true);
        getKeysOfConnected();
    }
    else if (connectionStatus.status == util.CONNECTION_CONFIRM) {
        subscribeToMessageTopic(connectionStatus, sessionId, stompClient, false);
        getKeysOfConnected();
    }
};

function sendConnectionAccept(connectionStatus, stompClient) {
    connectionStatus.status = util.CONNECTION_ACCEPT;
    stompClient.publish({
        destination: "/app/requestConnection",
        body: JSON.stringify(connectionStatus)
    });
}

function subscribeToMessageTopic(connectionStatus, sessionId, stompClient, isSessionIdFirst) {
    document.querySelector('.connected').textContent = connectionStatus.userId;
    if (isSessionIdFirst) {
        util.setCurrentMessageTopic(`/topic/messages/${sessionId + document.querySelector('.connected').textContent}`);
    } else {
        util.setCurrentMessageTopic(`/topic/messages/${document.querySelector('.connected').textContent + sessionId}`);
    }
    stompClient.subscribe(util.getCurrentMessageTopic(), messagingTopicCallback);
}

function sendConnectionConfirm(connectionStatus, stompClient) {
    connectionStatus.status = util.CONNECTION_CONFIRM;
    stompClient.publish({
        destination: "/app/requestConnection",
        body: JSON.stringify(connectionStatus)
    });
}

async function getKeysOfConnected() {
    let url = `${window.location.origin}/connected-keys`;
    let response = await fetch(url);
    if (response.status == 200) {
        let keys = await response.json();
        document.querySelector('#connectedPublic').textContent = keys.publicKey;
        document.querySelector('#connectedN').textContent = keys.primesMultiplication;

        document.querySelector('[name=KillConnection]').disabled = false;
        document.querySelector(`[name='send']`).disabled = false;
        document.querySelector(`[name='sign']`).disabled = false;
        document.querySelector(`[name='encrypt']`).disabled = false;
    } else {
        console.log('failed to get public key');
        console.log( await response.text());
    }
}