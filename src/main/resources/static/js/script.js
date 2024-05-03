import connectionsTopicCallback from "./connectionsTopicCallback.js";
import {getJsessionId, getMessagingTopic, isConnectionValid, setCurrentMessageTopic} from "./util.js";
import sendConnectionRequest from "./sendConnectionRequest.js";
import generateRSA from "./generateRSA.js";
import sendMessage from "./sendMessage.js";
import checkPrimes from "./checkPrimes.js";
import getKeys from "./getKeys.js";
import sign from "./sign.js";
import encrypt from "./encrypt.js";
import messagingTopicCallback from "./messagingTopicCallback.js";

window.onload = async function () {
        let sessionId = getJsessionId();
        let isValid = await isConnectionValid();

        const client = new StompJs.Client({
            brokerURL: 'ws://localhost:8080/ws',
            debug: function (str) {
                console.log(str);
            },
            reconnectDelay: 15000,
            heartbeatIncoming: 4000,
            heartbeatOutgoing: 4000,
        });

        client.onConnect = async function (frame) {
            // Do something, all subscribes must be done is this callback
            // This is needed because this will be executed after a (re)connect
            const subscription = client.subscribe(`/topic/connections/${sessionId}`, (message) => connectionsTopicCallback(message, client));
            
            
            // console.log(`is valid: ${isConnectionValid}`);
            if (isValid) {
                let topic = await getMessagingTopic();
                client.subscribe(topic, messagingTopicCallback);
                setCurrentMessageTopic(topic);
                console.log('Connection with user recovered');
            } else {
                console.log('Connection with user already invalid');
            }
        };

        client.onStompError = function (frame) {
            // Will be invoked in case of error encountered at Broker
            // Bad login/passcode typically will cause an error
            // Complaint brokers will set `message` header with a brief message. Body may contain details.
            // Compliant brokers will terminate the connection after any error
            console.log('Broker reported error: ' + frame.headers['message']);
            console.log('Additional details: ' + frame.body);
        };

        let inputId = document.querySelector('#connectionLine');
        let sendConnectionRequestButton = document.querySelector(`[name='StartConnection']`);
        sendConnectionRequestButton.addEventListener("click", () => sendConnectionRequest(inputId.value, client));

        let primeGen = document.querySelector(`[name='generatePrime']`);
        primeGen.addEventListener("click", generateRSA);

        let sendButton = document.querySelector(`[name='send']`);
        sendButton.addEventListener("click", () => sendMessage(sessionId, client));
        
        let checkPrime = document.querySelector(`[name='checkPrime']`);
        checkPrime.addEventListener('click', checkPrimes)

        let generateKeys = document.querySelector(`[name='generateKeys']`);
        generateKeys.addEventListener('click', getKeys);

        let signButton = document.querySelector(`[name='sign']`);
        signButton.addEventListener('click', sign);

        let encryptButton = document.querySelector(`[name='encrypt']`);
        encryptButton.addEventListener('click', encrypt);

        if (document.querySelector('#userStatus').value != 'generated') {
            sendConnectionRequestButton.disabled = true;
            checkPrime.disabled = true;
            generateKeys.disabled = true;
            document.querySelector('[name=KillConnection]').disabled = true;
        }
        if (!isValid) {
            signButton.disabled = true;
            encryptButton.disabled = true;
            sendButton.disabled = true;
        }

        client.activate();
}