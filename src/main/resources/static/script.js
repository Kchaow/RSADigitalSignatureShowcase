function getRandomInt(min, max) {
    const minCeiled = Math.ceil(min);
    const maxFloored = Math.floor(max);
    return Math.floor(Math.random() * (maxFloored - minCeiled) + minCeiled); // The maximum is exclusive and the minimum is inclusive
  }

window.onload = async function () {
        let cookies = document.cookie.split(';');
        let sessionId;
        let messageTopic;
        let connectedPublicKey;
        let connectedN;
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

        const connectionsCallback = async function (message) {
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

                let url = `http://localhost:8080/connected-keys`;
                let response = await fetch(url);
                if (response.status == 200) {
                    let keys = await response.json();
                    connectedPublicKey = keys.publicKey;
                    connectedN = keys.primesMultiplication;
                    document.querySelector('#connectedPublic').textContent = connectedPublicKey;
                    document.querySelector('#connectedN').textContent = connectedN;
                } else {
                    console.log('failed to get public key');
                    console.log( await response.json());
                }
            }
            else if (connectionStatus.status == 'connectionConfirm') {
                document.querySelector('.connected').textContent = connectionStatus.userId;
                messageTopic = `/topic/messages/${connectionStatus.userId + sessionId}`;
                client.subscribe(messageTopic, messagingCallback);

                let url = `http://localhost:8080/connected-keys`;
                let response = await fetch(url);
                if (response.status == 200) {
                    let keys = await response.json();
                    connectedPublicKey = keys.publicKey;
                    connectedN = keys.primesMultiplication;
                    document.querySelector('#connectedPublic').textContent = connectedPublicKey;
                    document.querySelector('#connectedN').textContent = connectedN;
                } else {
                    console.log('failed to get public key');
                }
            }
        };
        const messagingCallback = function (message) {
            let messageId = getRandomInt(1, 100000);
            let messageWindow = document.querySelector('.messagewindow');
            let messageBody = JSON.parse(message.body);
            if (messageBody.id != sessionId) {
                messageWindow.insertAdjacentHTML('beforeend', 
                `
                <div class="usersmessage">
                            <div class="messagestruct">
                                <div><b>&nbsp&nbsp&nbsp&nbsp ID: <b></div>
                                <p class="connected"></p>
                                <div class="message">
                                    <div class="content message-${messageId}">
                                        ${messageBody.text}
                                    </div>
                                    <div class="operations">
                                        <button class="messageoperation" name="decryptm-${messageId}">Расшифровать</button>
                                        <button class="messageoperation" name="veryficate-${messageId}">Проверить подпись</button>
                                    </div>
                                </div>
                            </div>
                        </div>
                `
                );
                let decipherButton = document.querySelector(`[name='decryptm-${messageId}']`);
                let messageDiv = document.querySelector(`.message-${messageId}`);
                decipherButton.addEventListener('click', async () => {
                    let url = 'http://localhost:8080/decipher';
                    let responseRequestMessage = {
                        text: messageDiv.textContent
                    };
                    let response = await fetch(url, {
                        headers: {
                            'Content-Type': 'application/json;charset=utf-8'
                          },
                        method: 'POST',
                        body: JSON.stringify(responseRequestMessage)
                    });
                    if (response.status == 200) {
                        document.querySelector(`.message-${messageId}`).textContent = (await response.json()).text;
                    } else {
                        console.log('decipher message failed');
                    }
                });
                let veryficateButton = document.querySelector(`[name='veryficate-${messageId}']`);
                veryficateButton.addEventListener('click', async () => {
                    let requestMessage = {
                        text: messageDiv.textContent
                    };
                    let keys = {
                        privateKey: connectedPublicKey,
                        publicKey: null,
                        primesMultiplication: connectedN
                    }
                    let messageWithRsa = {
                        responseRequestMessage: requestMessage,
                        rsaKeys: keys
                    };
                    let url = 'http://localhost:8080/decipher-by-keys';
                    let response = await fetch(url, {
                        headers: {
                            'Content-Type': 'application/json;charset=utf-8'
                          },
                        method: 'POST',
                        body: JSON.stringify(messageWithRsa)
                    });
                    if (response.status == 200) {
                        document.querySelector(`.message-${messageId}`).textContent = (await response.json()).text;
                    } else {
                        console.log('sign check failed');
                    }
                });
                messageId += 1;
            }
            console.log('Message received');
            console.log(message.body);
        }

        let input = document.querySelector('#connectionLine');
        let button = document.querySelector(`[name='StartConnection']`);
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
        let primeGen = document.querySelector(`[name='generatePrime']`);
        primeGen.addEventListener("click", async () => {
            console.log('generating rsa...')
            let url = `http://localhost:8080/rsa`;
            let response = await fetch(url, {
                method: "POST"
            });
            if (response.status == 204) {
                console.log('rsa generated');
                
                url = `http://localhost:8080/primes`;
                response = await fetch(url);
                let result = await response.json();
                document.querySelector('.p-output').textContent = result.p;
                document.querySelector('.q-output').textContent = result.q;
            } else {
                console.log('rsa generating failed');
            }
        });

        let sendButton = document.querySelector(`[name='send']`);
        sendButton.addEventListener("click", () =>{
            let inputArea = document.querySelector('.inputmessagearea');
            let messageWindow = document.querySelector('.messagewindow');
            messageWindow.insertAdjacentHTML('beforeend', 
            `
            <div class="yourmessagebox">
                        <div class="yourmessagestruct">
                            <div><b>You: &nbsp&nbsp&nbsp&nbsp<b></div>
                            <div class="yourmessage">
                                <div class="operations">
                                    <div class="yourincryptedmessage">
                                        ${inputArea.value}
                                    </div>
                                </div>
                                <div class="content">
                                    ${""}
                                </div>
                            </div>
                        </div>
                    </div>
            `
            );
            let message = {
                id: sessionId,
                text: inputArea.value
            };
            client.publish({
                destination: messageTopic,
                body: JSON.stringify(message)
            });
        });

        let checkPrime = document.querySelector(`[name='checkPrime']`);
        checkPrime.addEventListener('click', async () => {
            let p = document.querySelector('.p-output').textContent;
            let q = document.querySelector('.q-output').textContent;
            let url = `http://localhost:8080/primes-check`;
            let rsaPrimes = {
                "p": p,
                "q": q
            };
            let response = await fetch(url, {
                method: "POST",
                headers: {
                    'Content-Type': 'application/json;charset=utf-8'
                  },
                body: JSON.stringify(rsaPrimes)
            });
            let result = await response.json();
            document.querySelector('.p-testoutput').textContent = result.p;
            document.querySelector('.q-testoutput').textContent = result.q;
        })

        let generateKeys = document.querySelector(`[name='generateKeys']`);
        generateKeys.addEventListener('click', async () =>{
            let url = `http://localhost:8080/keys`;
            let response = await fetch(url);
            if (response.status == 200) {
                let result = await response.json();
                document.querySelector('#e').textContent = result.publicKey;
                document.querySelector('#d').textContent = result.privateKey;
                document.querySelector('#n1').textContent = result.primesMultiplication;
                document.querySelector('#n2').textContent = result.primesMultiplication;
            } else {
                console.log('failed to get keys');
            }
        });

        let signButton = document.querySelector(`[name='sign']`);
        signButton.addEventListener('click', async () => {
            console.log('signing...');
            let url = `http://localhost:8080/sign`;
            let inputArea = document.querySelector('.inputmessagearea');
            let responseRequestMessage = {
                text: inputArea.value
            };
            let response = await fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json;charset=utf-8'
                  },
                body: JSON.stringify(responseRequestMessage)
            });
            if (response.status == 200) {
                let result = await response.json();
                inputArea.value = result.text;
                console.log('signing finished');
            } else {
                console.log('sign failed');
            }
        });

        let encryptButton = document.querySelector(`[name='encrypt']`);
        encryptButton.addEventListener('click', async () => {
            console.log('encrypting...');
            let url = `http://localhost:8080/encrypt`;
            let inputArea = document.querySelector('.inputmessagearea');
            let responseRequestMessage = {
                text: inputArea.value
            };
            let rsaKeys = {
                privateKey: null,
                publicKey: connectedPublicKey,
                primesMultiplication: connectedN
            };
            let messageForEncrypt = {
                responseRequestMessage: responseRequestMessage,
                rsaKeys: rsaKeys
            };
            let response = await fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json;charset=utf-8'
                  },
                body: JSON.stringify(messageForEncrypt)
            });
            if (response.status == 200) {
                let result = await response.json();
                inputArea.value = result.text;
                console.log('encrypting finished');
            } else {
                console.log('encrypt failed')
            }
        });

        client.activate();
}