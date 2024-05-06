export default async function makeHash() {
    console.log('making hash...');
    let url = `${window.location.origin}/hash`;
    let inputArea = document.querySelector('.inputmessagearea');
    let signInput = document.querySelector('#hashInput');
    let responseRequestMessage = {
        text: inputArea.value
    };
    let response = await fetch(url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json;charset=utf-8'
          },
        body: JSON.stringify(responseRequestMessage)
    });
    if (response.status == 200) {
        let result = await response.json();
        signInput.value = result.text;
        console.log('hash created');
    } else {
        console.log('creating hash failed');
    }
}