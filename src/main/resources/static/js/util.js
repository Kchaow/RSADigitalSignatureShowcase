let currentMessageTopic;
let encryptedText;

export function getRandomInt(min, max) {
    const minCeiled = Math.ceil(min);
    const maxFloored = Math.floor(max);
    return Math.floor(Math.random() * (maxFloored - minCeiled) + minCeiled); // The maximum is exclusive and the minimum is inclusive
}

export function getJsessionId() {
    let cookies = document.cookie.split(';');
    let sessionId;
    for (let i = 0; i < cookies.length; i++) {
        let cookie = cookies[i].split('=');
        if (cookie[0] == 'JSESSIONID') {
            sessionId = cookie[1];
            break;
        }
    }
    return sessionId;
}

export function setCurrentMessageTopic(topic) {
    currentMessageTopic = topic;
}

export function getCurrentMessageTopic() {
    return currentMessageTopic;
}

export function getEncryptedText() {
    return encryptedText;
}

export function setEncryptedText(text) {
    encryptedText = text;
}
