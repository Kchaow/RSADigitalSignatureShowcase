export default async function sign() {
    console.log('signing...');
    let url = `${window.location.origin}/sign`;
    let inputArea = document.querySelector('.inputmessagearea');
    let signInput = document.querySelector('#signInput');
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
        console.log('signing finished');
    } else {
        console.log('sign failed');
    }
}