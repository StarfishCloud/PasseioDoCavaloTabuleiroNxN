package br.com.davidalain.passeiocavalo.solucao;

import br.com.davidalain.passeiocavalo.model.Posicao;
import br.com.davidalain.passeiocavalo.model.Tabuleiro;
import br.com.davidalain.passeiocavalo.model.TreeNode;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public record Solucao4(
        Tabuleiro tabuleiro
) {

    //===================================================================================
    //===================================================================================

    public TreeNode<Posicao> encontrarPasseioDoCavalo4(Posicao posicaoInicial) {
        int movimentos = 1;
        final TreeNode<Posicao> treeNodeInicial = new TreeNode<>(posicaoInicial, movimentos, null);
        boolean[][] visitados = new boolean[tabuleiro.dimensao()][tabuleiro.dimensao()];

        tabuleiro.mapaPosicaoInicialQuantidadeSolucoes().put(posicaoInicial, 0);

        // Inicializa com a posição inicial
        visitados[posicaoInicial.linha()][posicaoInicial.coluna()] = true;

        if (encontrarPasseioRecursivo4(treeNodeInicial, visitados, movimentos)) {
            return treeNodeInicial;
        }
        return null;
    }

    private boolean encontrarPasseioRecursivo4(TreeNode<Posicao> currentNode, boolean[][] visitados, int movimentos) {
        // Se já visitamos todas as casas, encontramos uma solução
        if (movimentos == tabuleiro.dimensao() * tabuleiro.dimensao()) {

            TreeNode<Posicao> currToPrint = currentNode;
            while ((currToPrint != null) && (currToPrint.parentNode != null)) {
                currToPrint = currToPrint.parentNode;
            }

            final Posicao primeiraPosicao = currToPrint.value;
            tabuleiro.mapaPosicaoInicialQuantidadeSolucoes().merge(primeiraPosicao, 1, Integer::sum);

            //System.out.println("==========================");
            //System.out.println("Posição Inicial: " + primeiraPosicao +
            //        ", Quantidade de Soluções Encontradas (até o momento): " + this.mapaPosicaoInicialQuantidadeSolucoes.get(primeiraPosicao));

            return true;
        }

        final List<Posicao> proximosMovimentos =  tabuleiro.grafoMovimentos().get(currentNode.value);

        // Paralelizar apenas no início da árvore
        final boolean usarParalelo = movimentos < (tabuleiro.dimensao() * tabuleiro.dimensao()) / 4;

        final Stream<Posicao> stream = usarParalelo ?
                proximosMovimentos.parallelStream() :
                proximosMovimentos.stream();

        stream.forEach(proximaPosicao -> {
            if (!visitados[proximaPosicao.linha()][proximaPosicao.coluna()]) {

                final TreeNode<Posicao> childNode = currentNode.addChild(proximaPosicao);

                final boolean[][] visitadosNovo = new boolean[tabuleiro.dimensao()][tabuleiro.dimensao()];
                for (int i = 0; i < tabuleiro.dimensao(); i++) {
                    visitadosNovo[i] = Arrays.copyOf(visitados[i], tabuleiro.dimensao());
                }
                visitadosNovo[proximaPosicao.linha()][proximaPosicao.coluna()] = true;

                encontrarPasseioRecursivo4(childNode, visitadosNovo, movimentos + 1);
            }
        });

        //Se chegou aqui, então:
        //  (1) é um nó intermediário, ou
        //  (2) é um nó folha de um caminho sem solução

        // Se (2), remove o nó da árvore para liberar memória
        if ((currentNode.childNodes.isEmpty()) && (currentNode.parentNode != null)) {
            //System.out.println("currentNode.parentNode.deleteChild(" + currentNode + ")");
            currentNode.parentNode.deleteChild(currentNode);
        }


        return false;
    }

    private boolean[][] cloneVisitados(boolean[][] visitados) {
        boolean[][] visitadosNovo = new boolean[tabuleiro.dimensao()][tabuleiro.dimensao()];
        for (int i = 0; i < tabuleiro.dimensao(); i++) {
            visitadosNovo[i] = Arrays.copyOf(visitados[i], tabuleiro.dimensao());
        }
        return visitadosNovo;
    }

}
