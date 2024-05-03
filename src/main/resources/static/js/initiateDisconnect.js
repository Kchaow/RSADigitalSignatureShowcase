import { setCurrentMessageTopic } from "./util.js";

export default async function initiateDisconnect() {
    document.querySelector('#connectedPublic').textContent = '';
    document.querySelector('#connectedN').textContent = '';
    document.querySelector('[name="sign"]').disabled = true;
    document.querySelector('[name="encrypt"]').disabled = true;
    document.querySelector('[name="send"]').disabled = true;
    document.querySelector('[name=KillConnection]').disabled = true;
    setCurrentMessageTopic('');
    let url = `http://localhost:8080/disconnect`;
    let response = await fetch(url);
    if (response.ok) {
        console.log('user was disconnected');
    } else {
        console.log('failed to disconnect from user');
    }
}