package br.com.davidalain.passeiocavalo.solucao;

import br.com.davidalain.passeiocavalo.model.Posicao;
import br.com.davidalain.passeiocavalo.model.Tabuleiro;

import java.util.ArrayList;
import java.util.List;

public record Solucao2(
        Tabuleiro tabuleiro
) {

    //===================================================================================
    //===================================================================================

    public List<Posicao> encontrarPasseioDoCavalo2(Posicao inicio) {
        List<Posicao> caminho = new ArrayList<>();
        boolean[][] visitado = new boolean[tabuleiro.dimensao()][tabuleiro.dimensao()];

        // Inicializa com a posição inicial
        caminho.add(inicio);
        visitado[inicio.linha()][inicio.coluna()] = true;

        if (encontrarPasseioRecursivo(inicio, caminho, visitado, 1)) {
            return caminho;
        }
        return null;
    }


    private boolean encontrarPasseioRecursivo(Posicao atual, List<Posicao> caminho,
                                              boolean[][] visitado, int movimentos) {
        // Se já visitamos todas as casas, encontramos uma solução
        if (movimentos == tabuleiro.dimensao() * tabuleiro.dimensao()) {
            return true;
        }

        List<Posicao> proximosMovimentos = tabuleiro.grafoMovimentos().get(atual);

        // Ordena os movimentos usando a heurística de Warnsdorff
        proximosMovimentos.sort((p1, p2) -> {
            long movs1 = contarMovimentosDisponiveis(p1, visitado);
            long movs2 = contarMovimentosDisponiveis(p2, visitado);
            return Long.compare(movs1, movs2);
        });

        // Tenta cada movimento possível
        for (Posicao proxima : proximosMovimentos) {
            if (!visitado[proxima.linha()][proxima.coluna()]) {
                // Tenta este movimento
                visitado[proxima.linha()][proxima.coluna()] = true;
                caminho.add(proxima);

                if (encontrarPasseioRecursivo(proxima, caminho, visitado, movimentos + 1)) {
                    return true;
                }

                // Se não funcionou, desfaz o movimento (backtracking)
                visitado[proxima.linha()][proxima.coluna()] = false;
                caminho.remove(caminho.size() - 1);
            }
        }

        return false;
    }

    private int contarMovimentosDisponiveis(Posicao posicao, boolean[][] visitado) {
        return (int) tabuleiro.grafoMovimentos().get(posicao).stream()
                .filter(p -> !visitado[p.linha()][p.coluna()])
                .count();
    }

}
