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
                            <p class="connected"></p>
                            <p id="message-sender-${messageId}" style="margin: 5px 0px 5px 5px;">1</p>
                            <div class="message">
                                <div class="content">
                                    <div class="content-pairs">
                                        <p class="lab">Статус проверки подписи: </p>
                                        <p style="flex: 5; margin-top: p;" id="sign-verification-status-${messageId}">Проверена</p>
                                    </div>
                                    <div class="content-pairs">
                                        <p class="lab">Подпись сообщения:</p>
                                        <p class="scroll" id="message-sign-${messageId}">${messageBody.sign}</p>
                                    </div>
                                    <div class="content-pairs">
                                        <p class="lab">Хэш сообщения:</p>
                                        <p class="scroll" id="message-hash-${messageId}"></p>
                                    </div>
                                    <div class="content-pairs">
                                        <p class="lab">Текст сообщения:</p>
                                        <p class="scroll" id="message-text-${messageId}">${messageBody.text}</p>
                                    </div>
                                </div>
                                <div class="operations">
                                    <button class="messageoperation" name="decryptm-0">Расшифровать</button>
                                    <button class="messageoperation" name="veryficate-0">Проверить подпись</button>
                                </div>
                            </div>
                        </div>
                    </div>
        `
        );
        let decipherButton = document.querySelector(`[name='decryptm-${messageId}']`);
        decipherButton.addEventListener('click', async () => decipher(messageId));

        let veryficateButton = document.querySelector(`[name='veryficate-${messageId}']`);
        veryficateButton.addEventListener('click', async () => veryficateSign(messageId));
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

async function veryficateSign(messageId) {
    await signCheck(messageId);
    await getMessageHash(messageId);

    let verificationStatus = document.querySelector(`#sign-verification-status-${messageId}`);
    if (document.querySelector(`#message-hash-${messageId}`).textContent 
            == document.querySelector(`#message-sign-${messageId}`).textContent) {
        verificationStatus.textContent = 'Подпись верна';
    } else {
        verificationStatus.textContent = 'Подпись неверна';
    }


}

async function signCheck(messageId) {
    let signEl = document.querySelector(`#message-sign-${messageId}`);
    let requestMessage = {
        text: signEl.textContent
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
        signEl.textContent = (await response.json()).text;
    } else {
        console.log('sign check failed');
    }
}

async function getMessageHash(messageId) {
    let requestMessage = {
        text: document.querySelector(`#message-text-${messageId}`).textContent
    }
    let url = 'http://localhost:8080/hash';
    let response = await fetch(url, {
        headers: {
            'Content-Type': 'application/json;charset=utf-8'
        },
        method: 'POST',
        body: JSON.stringify(requestMessage)
    });
    if (response.status == 200) {
        document.querySelector(`#message-hash-${messageId}`).textContent = (await response.json()).text;
    } else {
        console.log(`failed to get hash for message ${messageId}`);
    }
}