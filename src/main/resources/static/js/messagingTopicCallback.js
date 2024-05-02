import { getRandomInt, getJsessionId } from "./util.js";

export default async function messagingTopicCallback(message) {
    let messageId = getRandomInt(1, 100000);
    let messageWindow = document.querySelector('.messagewindow');
    let messageBody = JSON.parse(message.body);
    let sessionId = getJsessionId();
    if (messageBody.id != sessionId) {
        messageWindow.insertAdjacentHTML('beforeend', 
        `
        <div class="usersmessage">
                    <div class="messagestruct">
                        <div><b>&nbsp&nbsp&nbsp&nbsp ID: <b></div>
                        <p class="connected"></p>
                        <div class="message">
                            <div class="content">
                                <p id="message-text-${messageId}">${messageBody.text}</p>
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
        decipherButton.addEventListener('click', async () => decipher(messageId));

        let veryficateButton = document.querySelector(`[name='veryficate-${messageId}']`);
        veryficateButton.addEventListener('click', async () => veryficateSign(messageDiv, messageId));
    }
    console.log('Message received');
    console.log(message.body);
}

async function decipher(messageId) {
    let messageText = document.querySelector(`#message-text-${messageId}`);
    let url = 'http://localhost:8080/decipher';
    console.log(`message to decipher: ${messageText.textContent}`);
    let responseRequestMessage = {
        text: messageText.textContent
    };
    let response = await fetch(url, {
        headers: {
            'Content-Type': 'application/json;charset=utf-8'
          },
        method: 'POST',
        body: JSON.stringify(responseRequestMessage)
    });
    if (response.status == 200) {
        messageText.textContent = (await response.json()).text;
    } else {
        console.log('decipher message failed');
    }
}

async function veryficateSign(messageDiv, messageId) {
    let requestMessage = {
        text: messageDiv.textContent
    };
    let keys = {
        privateKey: document.querySelector('#connectedPublic').textContent,
        publicKey: null,
        primesMultiplication: document.querySelector('#connectedN').textContent
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
}