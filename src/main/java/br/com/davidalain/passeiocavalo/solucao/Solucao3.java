package br.com.davidalain.passeiocavalo.solucao;

import br.com.davidalain.passeiocavalo.model.Posicao;
import br.com.davidalain.passeiocavalo.model.Tabuleiro;
import br.com.davidalain.passeiocavalo.model.TreeNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public record Solucao3(
        Tabuleiro tabuleiro
) {

    //===================================================================================
    //===================================================================================

    public TreeNode<Posicao> encontrarPasseioDoCavalo3(Posicao inicio) {
        int movimentos = 1;
        final TreeNode<Posicao> initialNode = new TreeNode<>(inicio, movimentos, null);
        boolean[][] visitados = new boolean[tabuleiro.dimensao()][tabuleiro.dimensao()];

        // Inicializa com a posição inicial
        visitados[inicio.linha()][inicio.coluna()] = true;

        if (encontrarPasseioRecursivo3(initialNode, visitados, movimentos)) {
            return initialNode;
        }
        return null;
    }

    private boolean encontrarPasseioRecursivo3(TreeNode<Posicao> currentNode, boolean[][] visitados, int movimentos) {
        // Se já visitamos todas as casas, encontramos uma solução
        if (movimentos == tabuleiro.dimensao() * tabuleiro.dimensao()) {
            System.out.println("==========================");

            final List<Posicao> caminho = new ArrayList<>();
            TreeNode<Posicao> currToPrint = currentNode;

            while (currToPrint != null) {
                caminho.add(currToPrint.value);
                currToPrint = currToPrint.parentNode;
            }

            Collections.reverse(caminho);

            final Posicao primeiraPosicao = caminho.get(0);
            int quantidadeSolucoes = 1 + this.tabuleiro.mapaPosicaoInicialQuantidadeSolucoes().getOrDefault(primeiraPosicao, 0);
            this.tabuleiro.mapaPosicaoInicialQuantidadeSolucoes().put(primeiraPosicao, quantidadeSolucoes);

            System.out.println("Posição Inicial: " + primeiraPosicao + " - Quantidade de Soluções Encontradas (até o momento): " + quantidadeSolucoes);
            //System.out.println("Solução mais recente: " + caminho.stream().map(Object::toString).collect(Collectors.joining(" -> ")));

            return true;
        }

        final List<Posicao> proximosMovimentos = tabuleiro.grafoMovimentos().get(currentNode.value);

        //((movimentos % 2 == 0) ?
        (proximosMovimentos.parallelStream())
                //        :
                //        (proximosMovimentos.stream())
                //)
                .forEach(proximaPosicao -> {
                    if (!visitados[proximaPosicao.linha()][proximaPosicao.coluna()]) {

                        final TreeNode<Posicao> childNode = currentNode.addChild(proximaPosicao);

                        boolean[][] visitadosNovo = new boolean[tabuleiro.dimensao()][tabuleiro.dimensao()];
                        for (int i = 0; i < tabuleiro.dimensao(); i++) {
                            visitadosNovo[i] = Arrays.copyOf(visitados[i], tabuleiro.dimensao());
                        }
                        visitadosNovo[proximaPosicao.linha()][proximaPosicao.coluna()] = true;

                        encontrarPasseioRecursivo3(childNode, visitadosNovo, movimentos + 1);
                    }
                });


        //System.out.println("#########################");
        if (currentNode.depth < (tabuleiro.dimensao() * tabuleiro.dimensao() - 2) &&
                currentNode.childNodes.isEmpty() &&
                currentNode.parentNode != null) {
            //System.out.println("currentNode.parentNode.deleteChild(" + currentNode + ")");
            currentNode.parentNode.deleteChild(currentNode);
        }

        return false;
    }
}
