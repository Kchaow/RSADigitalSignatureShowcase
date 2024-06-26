export default async function getKeys() {
    let url = `${window.location.origin}/keys`;
    let response = await fetch(url);
    if (response.status == 200) {
        let result = await response.json();
        document.querySelector('#e').textContent = result.publicKey;
        document.querySelector('#d').textContent = result.privateKey;
        document.querySelector('#n1').textContent = result.primesMultiplication;
        document.querySelector('#n2').textContent = result.primesMultiplication;

        document.querySelector(`[name='StartConnection']`).disabled = false;
    } else {
        console.log('failed to get keys');
    }
}