import { getCurrentMessageTopic } from "./util.js";

export default function sendMessage(sessionId, stompClient) {
    let inputArea = document.querySelector('.inputmessagearea');
    let messageWindow = document.querySelector('.messagewindow');
    messageWindow.insertAdjacentHTML('beforeend', 
    `
        <div class="yourmessagebox">
                <div class="yourmessagestruct">
                    <div>
                        <p>You: &nbsp&nbsp&nbsp&nbsp<p>
                    </div>
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
        text: inputArea.value,
        sign: document.querySelector('#signInput').value
    };
    stompClient.publish({
        destination: getCurrentMessageTopic(),
        body: JSON.stringify(message)
    });
}