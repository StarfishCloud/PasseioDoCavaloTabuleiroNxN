package br.com.davidalain.passeiocavalo.solucao;

import br.com.davidalain.passeiocavalo.model.Posicao;
import br.com.davidalain.passeiocavalo.model.Tabuleiro;

import java.util.*;
import java.util.stream.Collectors;

public record Solucao1(
        Tabuleiro tabuleiro
) implements Solucao {

    public List<Posicao> encontrarPasseioDoCavalo(Posicao inicio) {
        List<Posicao> caminho = new ArrayList<>();
        Set<Posicao> visitados = new HashSet<>();

        // PriorityQueue ordenada pelo número de movimentos disponíveis (heurística de Warnsdorff)
        PriorityQueue<Posicao> fila = new PriorityQueue<>((p1, p2) -> {
            List<Posicao> movs1 = tabuleiro.grafoMovimentos().get(p1);
            List<Posicao> movs2 = tabuleiro.grafoMovimentos().get(p2);
            return Integer.compare(
                    (int) movs1.stream().filter(p -> !visitados.contains(p)).count(),
                    (int) movs2.stream().filter(p -> !visitados.contains(p)).count()
            );
        });

        // Começar pela posição inicial
        fila.offer(inicio);
        caminho.add(inicio);
        visitados.add(inicio);

        // Total de posições a visitar
        int totalPosicoes = tabuleiro.dimensao() * tabuleiro.dimensao();

        while (!fila.isEmpty() && visitados.size() < totalPosicoes) {
            Posicao atual = fila.poll();
            List<Posicao> movimentosPossiveis = tabuleiro.grafoMovimentos().get(atual);

            // Ordena movimentos pela heurística de Warnsdorff (menos movimentos disponíveis primeiro)
            List<Posicao> movimentosOrdenados = movimentosPossiveis.stream()
                    .filter(p -> !visitados.contains(p))
                    .sorted((p1, p2) -> {
                        long movs1 = tabuleiro.grafoMovimentos().get(p1).stream()
                                .filter(p -> !visitados.contains(p)).count();
                        long movs2 = tabuleiro.grafoMovimentos().get(p2).stream()
                                .filter(p -> !visitados.contains(p)).count();
                        return Long.compare(movs1, movs2);
                    })
                    .collect(Collectors.toList());

            // Tenta cada movimento possível
            for (Posicao proximaPosicao : movimentosOrdenados) {
                if (!visitados.contains(proximaPosicao)) {
                    visitados.add(proximaPosicao);
                    caminho.add(proximaPosicao);
                    fila.offer(proximaPosicao);
                    break; // Encontrou um movimento válido, continua com a próxima posição
                }
            }
        }

        // Verifica se encontrou um caminho completo
        if (caminho.size() != totalPosicoes) {
            return null; // Não foi possível encontrar um caminho completo
        }

        return caminho;
    }

    @Override
    public int calcular() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
