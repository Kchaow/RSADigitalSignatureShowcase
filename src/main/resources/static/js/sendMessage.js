import { getCurrentMessageTopic, getEncryptedText } from "./util.js";

export default async function sendMessage(sessionId, stompClient) {
    let inputArea = document.querySelector('.inputmessagearea');
    let messageWindow = document.querySelector('.messagewindow');
    let sign = document.querySelector('#signInput');
    let hash;

    let url = `${window.location.origin}/hash`;
    let response = await fetch(url, {
        headers: {
            'Content-Type': 'application/json;charset=utf-8'
        },
        method: 'POST',
        body: JSON.stringify({ text: getEncryptedText() })
    });
    if (response.status == 200) {
        hash = (await response.json()).text;
    } else {
        console.log(`failed to get hash`);
    }

    messageWindow.insertAdjacentHTML('beforeend', 
    `
        <div class="usersmessage receiver-message">
            <div class="receiver-messagestruct">
                <p class="connected"></p>
                <div class="message receiver">
                    <div class="content">
                        <div class="content-pairs">
                            <p class="lab">Подпись сообщения:</p>
                            <p class="scroll">${sign.value}</p>
                        </div>
                        <div class="content-pairs">
                            <p class="lab">Хэш сообщения:</p>
                            <p class="scroll">${hash}</p>
                        </div>
                        <div class="content-pairs">
                            <p class="lab">Текст сообщения:</p>
                            <p class="scroll">${getEncryptedText()}</p>
                        </div>
                        <div class="content-pairs">
                            <p class="lab">Зашифрованное сообщения:</p>
                            <p class="scroll">${inputArea.value}</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    `
    );
    let message = {
        id: sessionId,
        text: inputArea.value,
        sign: sign.value
    };
    stompClient.publish({
        destination: getCurrentMessageTopic(),
        body: JSON.stringify(message)
    });
}