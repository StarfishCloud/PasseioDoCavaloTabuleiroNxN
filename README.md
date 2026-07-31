# **Problema do Passeio do Cavalo**

O **Problema do Passeio do Cavalo** é um problema clássico da matemática e da ciência da computação,
relacionado ao movimento do cavalo no tabuleiro de xadrez.

Dado um tabuleiro de xadrez (_normalmente de 8x8 casas_), o problema pergunta:

&nbsp;&nbsp;&nbsp;&nbsp;É possível mover um cavalo pelo tabuleiro de forma que ele visite cada casa exatamente uma vez?

Esse percurso é chamado de passeio do cavalo ([Knight's tour](https://en.wikipedia.org/wiki/Knight%27s_tour)).

O código deste repositório fornece diferentes implementações em **Java** desenvolvidas pelo [Prof. David Alain do Nascimento](https://github.com/davidalain) que calcula a quantidade de caminhos possíveis
do passeio do cavalo de um tabuleiro de xadrez de dimensão **N**x**N** utilizando algumas técnicas de programação paralela e concorrente.

A quantidade de soluções desse problema é conhecido na literatura. A proposta deste repositório é exemplificar as técnicas que podem ser utilizadas para implementar a solução do problema.

Fonte: [Number of tours - Knight's tour](https://en.wikipedia.org/wiki/Knight%27s_tour#Number_of_tours)

| N | Quantidade de caminhos possíveis |
| - | -------------------------------- |
| 1	| 1                                |
| 2	| 0                                |
| 3	| 0                                |
| 4	| 0                                |
| 5	| 1 728                            |
| 6	| 6 637 920                        |
| 7	| 165 575 218 320                  |
| 8	| 19 591 828 170 979 904           |

## **Como utilizar a API**

Para testar o sistema, você pode realizar chamadas HTTP nos endpoints abaixo, dependendo de onde o serviço estiver rodando.

### 1. Ambiente
* **BASE_URL** `http://{HOST}:8080/api/passeiocavalo`

### 2. Executar o cálculo
Utilize este endpoint para disparar o processamento. O sistema calculará o total de soluções possíveis de forma síncrona.

* **Endpoint:** `POST {BASE_URL}/calcular`
* **Corpo da requisição (JSON):**
```json
{
  "dimensao": 5,
  "token": "seu_token_aqui"
}
```


* **Formato das respostas de sucesso (JSON):**
```json
{
  "status": "Sucesso",
  "totalSolucoes": 1728,
  "dimensaoTabuleiro": "5x5",
  "tempoExecucaoMs": 159
}
```

* **Formato das respostas de erro (JSON):**
```json
{
  "erro": "Mensagem de erro"
}
```

### 3. Verificação da saúde da aplicação
Utilize este endpoint para validar se o servidor está respondendo.

* **Endpoint:** `GET {BASE_URL}/health`
* **Resposta esperada:**
```json
{
  "status": "UP"
}
```
