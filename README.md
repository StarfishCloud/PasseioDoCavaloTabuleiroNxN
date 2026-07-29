# **Problema do Passeio do Cavalo**

O **Problema do Passeio do Cavalo** é um problema clássico da matemática e da ciência da computação, 
relacionado ao movimento do cavalo no tabuleiro de xadrez.

Dado um tabuleiro de xadrez (_normalmente de 8x8 casas_), o problema pergunta:

&nbsp;&nbsp;&nbsp;&nbsp;É possível mover um cavalo pelo tabuleiro de forma que ele visite cada casa exatamente uma vez?

Esse percurso é chamado de passeio do cavalo ([Knight's tour](https://en.wikipedia.org/wiki/Knight%27s_tour)).

O código deste repositório fornece uma implementação em **Java** que calcula a quantidade de caminhos possíveis 
do passeio do cavalo de um tabuleiro de xadrez de dimensão **N**x**N**.

---

## **Como utilizar a API**

Para testar o sistema, você pode realizar chamadas HTTP nos endpoints abaixo, dependendo de onde o serviço estiver rodando.

### 1. Ambientes de Teste
* **Local:** `http://localhost:8080/api/cavalo`
* **Produção (Kubernetes):** `https://passeio-cavalo.starfishcloud.com.br/api/cavalo`

### 2. Iniciar o cálculo
Utilize este endpoint para disparar o processamento. O sistema calculará o total de soluções possíveis de forma síncrona.

* **Endpoint:** `POST {BASE_URL}/calcular`
* **Corpo da requisição (JSON):**
    ```json
    {
      "dimensao": 8,
      "token": "seu_token_aqui"
    }
    ```

### 3. Verificação de prontidão
Utilize este endpoint para validar se o servidor está respondendo.

* **Endpoint:** `GET {BASE_URL}/health`
* **Resposta esperada:** `UP`

---