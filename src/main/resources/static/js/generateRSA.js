export default async function generateRSA() {
    console.log('generating rsa...')
    let url = `${window.location.origin}/rsa`;
    let response = await fetch(url, {
         method: "POST"
    });
    if (response.status == 204) {
        console.log('rsa generated');
                
        url = `${window.location.origin}/primes`;
        response = await fetch(url);
        let result = await response.json();
        document.querySelector('.p-output').textContent = result.p;
        document.querySelector('.q-output').textContent = result.q;

        document.querySelector(`[name='checkPrime']`).disabled = false;
        document.querySelector(`[name='generateKeys']`).disabled = false;
    } else {
        console.log('rsa generating failed');
    }
}