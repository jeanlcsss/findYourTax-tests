function calcularFrete() {
    const nome = document.getElementById('nome').value;
    const nomeDestinatario = document.getElementById('nomeDestinatario').value;
    const cepOrigem = document.getElementById('cepOrigem').value;
    const cepDestino = document.getElementById('cepDestino').value;
    const peso = document.getElementById('peso').value;

    const data = {
        nome,
        nomeDestinatario,
        cepOrigem,
        cepDestino,
        peso: parseFloat(peso)
    };

    fetch('http://localhost:8080/encomendas/calcular', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    })
    .then(response => response.json())
    .then(data => {
        const resultado = document.getElementById('resultado');
        resultado.innerHTML = `
            <p>Data de Entrega: ${data.dataEntrega}</p>
            <p>Valor do Frete: R$ ${data.valorFrete.toFixed(2)}</p>
        `;
    })
    .catch(error => {
        console.error('Erro ao calcular frete:', error);
    });
}

document.querySelector('.button').addEventListener('click', calcularFrete);
