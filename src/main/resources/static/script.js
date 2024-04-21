window.onload = function () {
        let cookies = document.cookie.split(';');
        let sessionId;
        let messageTopic;
        for (let i = 0; i < cookies.length; i++) {
            let cookie = cookies[i].split('=');
            if (cookie[0] == 'JSESSIONID') {
                sessionId = cookie[1];
                break;
            }
        }

        const client = new StompJs.Client({
            brokerURL: 'ws://localhost:8080/ws',
            debug: function (str) {
                console.log(str);
            },
            reconnectDelay: 15000,
            heartbeatIncoming: 4000,
            heartbeatOutgoing: 4000,
        });

        client.onConnect = function (frame) {
            // Do something, all subscribes must be done is this callback
            // This is needed because this will be executed after a (re)connect
            const subscription = client.subscribe(`/topic/connections/${sessionId}`, connectionsCallback);
        };

        client.onStompError = function (frame) {
            // Will be invoked in case of error encountered at Broker
            // Bad login/passcode typically will cause an error
            // Complaint brokers will set `message` header with a brief message. Body may contain details.
            // Compliant brokers will terminate the connection after any error
            console.log('Broker reported error: ' + frame.headers['message']);
            console.log('Additional details: ' + frame.body);
        };

        const connectionsCallback = function (message) {
            // called when the client receives a STOMP message from the server
            console.log('Message received');
            let connectionStatus = JSON.parse(message.body);
            console.log(`received connection status: ${connectionStatus}`);
            if (connectionStatus.status == 'connectionRequest' && confirm(`Принять запрос на подключение от ${connectionStatus.userId}?`)) {
                connectionStatus.status = 'connectionAccept';
                client.publish({
                    destination: "/app/requestConnection",
                    body: JSON.stringify(connectionStatus)
                });
            } 
            else if (connectionStatus.status == 'connectionAccept') {
                connectionStatus.status = 'connectionConfirm';
                client.publish({
                    destination: "/app/requestConnection",
                    body: JSON.stringify(connectionStatus)
                });
                document.querySelector('.connected').textContent = connectionStatus.userId;
                messageTopic = `/topic/messages/${sessionId + connectionStatus.userId}`;
                client.subscribe(messageTopic, messagingCallback);
            }
            else if (connectionStatus.status == 'connectionConfirm') {
                document.querySelector('.connected').textContent = connectionStatus.userId;
                messageTopic = `/topic/messages/${connectionStatus.userId + sessionId}`;
                client.subscribe(messageTopic, messagingCallback);
            }
        };

        const messagingCallback = function (message) {
            console.log('Message received');
            console.log(message.body);
        }

        let input = document.querySelector('.idInput');
        let button = document.querySelector('.myButton');
        button.addEventListener("click", () => {
            console.log('send');
            console.log(input.value);
            let connectionStatus = {
                "userId": input.value,
                "status": "connectionRequest"
            }
            client.publish({
                destination: '/app/requestConnection',
                body: JSON.stringify(connectionStatus)
            });
        });
        let genButton = document.querySelector('.genButton');
        genButton.addEventListener("click", async () => {
            console.log('generating rsa...')
            let url = `http://localhost:8080/rsa`;
            let response = await fetch(url, {
                method: "POST"
            });
            if (response.status == 204) {
                console.log('rsa generated');
            } else {
                console.log('rsa generating failed');
            }
        });
        let sendButton = document.querySelector('.sender');
        sendButton.addEventListener("click", () =>{
            client.publish({
                destination: messageTopic,
                body: "testing"
            });
        });

        client.activate();
}