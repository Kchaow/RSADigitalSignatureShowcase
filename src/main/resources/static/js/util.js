let currentMessageTopic;
let encryptedText;
export const CONNECTION_REQUEST = "CONNECTION_REQUEST";
export const CONNECTION_ACCEPT = "CONNECTION_ACCEPT";
export const CONNECTION_CONFIRM = "CONNECTION_CONFIRM";
export const CONNECTED = "CONNECTED";
export const NO_CONNECTION = "NO_CONNECTION";


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

export async function isConnectionValid() {
    let url = `${window.location.origin}/isConnectionValid`;
    let response = await fetch(url);
    if (response.ok) {
        let result = await response.json();
        // console.log(result);
        return (result.text === 'true');
    } else {
        console.log('failed to get isConnectionValid');
    }
}

export async function getMessagingTopic() {
    let url = `${window.location.origin}/messagingTopic`;
    let response = await fetch(url);
    if (response.ok) {
        let result = await response.json();
        return result.text;
    } else {
        console.log('failed to get messagingTopic');
    }
}