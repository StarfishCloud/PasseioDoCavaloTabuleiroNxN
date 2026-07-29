package br.com.davidalain.passeiocavalo.model;

import java.util.ArrayList;
import java.util.List;

public record Posicao(int linha, int coluna) implements Comparable<Posicao> {

    @Override
    public int compareTo(Posicao o) {
        int comp = Integer.compare(this.linha, o.linha);
        if (comp == 0) {
            return Integer.compare(this.coluna, o.coluna);
        }
        return comp;
    }

    // Metodo que retorna todas as possíveis posições do movimento do cavalo
    public List<Posicao> movimentosPossiveisCavalo(int dimensaoTabuleiro) {
        List<Posicao> movimentos = new ArrayList<>();

        // Os 8 possíveis movimentos do cavalo
        int[][] deslocamentos = {
                {-2, -1}, {-2, 1},  // Dois movimentos para cima
                {2, -1}, {2, 1},   // Dois movimentos para baixo
                {-1, -2}, {1, -2},  // Dois movimentos para esquerda
                {-1, 2}, {1, 2}    // Dois movimentos para direita
        };

        // Verifica cada possível movimento
        for (int[] deslocamento : deslocamentos) {
            int novaLinha = this.linha + deslocamento[0];
            if (!Posicao.indiceValido(novaLinha, dimensaoTabuleiro))
                continue;

            int novaColuna = this.coluna + deslocamento[1];
            if (!Posicao.indiceValido(novaColuna, dimensaoTabuleiro))
                continue;

            // Se a nova posição é válida, adiciona à lista de movimentos possíveis
            movimentos.add(new Posicao(novaLinha, novaColuna));
        }

        return movimentos;
    }

    /**
     * Calculates the mirrored position of the current position on a board with the given dimension.
     * <p>
     * The mirrored position is determined based on the center of the board. For positions beyond the
     * center, the mirrored position is calculated by reflecting the row and column indices.
     * <p>
     * resultado = 6 - linha - 1
     * linha = 0, resultado = 0
     * linha = 1, resultado = 1
     * linha = 2, resultado = 2
     * linha = 3, resultado = 2 //6 - 3 - 1 = 2
     * linha = 4, resultado = 1 //6 - 4 - 1 = 1
     * linha = 5, resultado = 0 //6 - 5 - 1 = 0
     * <p>
     * resultado = 5 - linha - 1
     * linha = 0, resultado = 0
     * linha = 1, resultado = 1
     * linha = 2, resultado = 2 //5 - 2 - 1 = 2
     * linha = 3, resultado = 1 //5 - 3 - 1 = 1
     * linha = 4, resultado = 0 //5 - 4 - 1 = 0
     *
     * @param dimensaoTabuleiro the dimension of the square board (number of rows and columns)
     * @return a new Posicao object representing the mirrored position
     */
    public Posicao posicaoEspelhoOriginal(int dimensaoTabuleiro) {

        int indiceMax = dimensaoTabuleiro % 2 == 0 ?
                (dimensaoTabuleiro / 2) :
                (dimensaoTabuleiro + 1) / 2;

        int linhaEspelho = this.linha;
        int colunaEspelho = this.coluna;

        if (this.linha >= indiceMax)
            linhaEspelho = (dimensaoTabuleiro - this.linha - 1);

        if (this.coluna >= indiceMax)
            colunaEspelho = (dimensaoTabuleiro - this.coluna - 1);

        if (linhaEspelho > colunaEspelho)
            return new Posicao(colunaEspelho, linhaEspelho);
        else
            return new Posicao(linhaEspelho, colunaEspelho);
    }

    public static boolean posicaoValida(int novaLinha, int novaColuna, int dimensao) {
        return indiceValido(novaLinha, dimensao) &&
                indiceValido(novaColuna, dimensao);
    }

    private static boolean indiceValido(int indice, int dimensao) {
        return ((indice >= 0) && (indice < dimensao));
    }

    public String toString2() {
        return "Posicao{" +
                "linha=" + linha +
                ", coluna=" + coluna +
                '}';
    }

    @Override
    public String toString() {
        return "p[" + linha + "," + coluna + "]";
    }
}