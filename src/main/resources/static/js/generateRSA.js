export default async function generateRSA() {
    console.log('generating rsa...')
    let url = `http://localhost:8080/rsa`;
    let response = await fetch(url, {
         method: "POST"
    });
    if (response.status == 204) {
        console.log('rsa generated');
                
        url = `http://localhost:8080/primes`;
        response = await fetch(url);
        let result = await response.json();
        document.querySelector('.p-output').textContent = result.p;
        document.querySelector('.q-output').textContent = result.q;
    } else {
        console.log('rsa generating failed');
    }
}