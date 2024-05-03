import { setEncryptedText } from "./util.js";

export default async function encrypt() {
    console.log('encrypting...');
    let url = `http://localhost:8080/encrypt`;
    let inputArea = document.querySelector('.inputmessagearea');
    setEncryptedText(inputArea.value);
    let responseRequestMessage = {
        text: inputArea.value
    };
    let rsaKeys = {
        privateKey: null,
        publicKey: document.querySelector('#connectedPublic').textContent,
        primesMultiplication: document.querySelector('#connectedN').textContent
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
}