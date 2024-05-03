export default async function checkPrimes() {
    let p = document.querySelector('.p-output').textContent;
    let q = document.querySelector('.q-output').textContent;
    let url = `${window.location.origin}/primes-check`;
    let rsaPrimes = {
        "p": p,
        "q": q
    };
    let response = await fetch(url, {
        method: "POST",
        headers: {
            'Content-Type': 'application/json;charset=utf-8'
            },
        body: JSON.stringify(rsaPrimes)
    });
    let result = await response.json();
    document.querySelector('.p-testoutput').textContent = result.p;
    document.querySelector('.q-testoutput').textContent = result.q;
}