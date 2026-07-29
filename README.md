# **Problema do Passeio do Cavalo**

O **Problema do Passeio do Cavalo** é um problema clássico da matemática e da ciência da computação, 
relacionado ao movimento do cavalo no tabuleiro de xadrez.

Dado um tabuleiro de xadrez (_normalmente de 8x8 casas_), o problema pergunta:

    É possível mover um cavalo pelo tabuleiro de forma que ele visite cada casa exatamente uma vez?

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
