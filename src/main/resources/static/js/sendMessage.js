import { getCurrentMessageTopic } from "./util.js";

export default function sendMessage(sessionId, stompClient) {
    let inputArea = document.querySelector('.inputmessagearea');
    let messageWindow = document.querySelector('.messagewindow');
    messageWindow.insertAdjacentHTML('beforeend', 
    `
        <div class="usersmessage receiver-message">
            <div class="receiver-messagestruct">
                <p class="connected"></p>
                <div class="message receiver">
                    <div class="content">
                        <div class="content-pairs">
                            <p class="lab">Подпись сообщения:</p>
                            <p class="scroll"></p>
                        </div>
                        <div class="content-pairs">
                            <p class="lab">Хэш сообщения:</p>
                            <p class="scroll"></p>
                        </div>
                        <div class="content-pairs">
                            <p class="lab">Текст сообщения:</p>
                            <p class="scroll"></p>
                        </div>
                        <div class="content-pairs">
                            <p class="lab">Зашифрованное сообщения:</p>
                            <p class="scroll"></p>
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
        sign: document.querySelector('#signInput').value
    };
    stompClient.publish({
        destination: getCurrentMessageTopic(),
        body: JSON.stringify(message)
    });
}