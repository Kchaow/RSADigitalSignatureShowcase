import { setCurrentMessageTopic } from "./util.js";

export default async function disconnectCallback(message) {
    let jsonMessage = await JSON.parse(message.body);
    let initiatorId = jsonMessage.text;
    document.querySelector('#connectedPublic').textContent = '';
    document.querySelector('#connectedN').textContent = '';
    document.querySelector('[name="sign"]').disabled = true;
    document.querySelector('[name="encrypt"]').disabled = true;
    document.querySelector('[name="send"]').disabled = true;
    document.querySelector('[name=KillConnection]').disabled = true;
    setCurrentMessageTopic('');
    alert(`${initiatorId} разорвал соединение`);
    console.log('user was disconnected');
}