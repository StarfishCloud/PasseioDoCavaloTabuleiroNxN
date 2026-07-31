package br.com.davidalain.passeiocavalo.solucao;

import br.com.davidalain.passeiocavalo.model.Posicao;
import br.com.davidalain.passeiocavalo.model.Tabuleiro;
import br.com.davidalain.passeiocavalo.model.TreeNode;

import java.util.List;
import java.util.stream.Stream;

public record Solucao6(
        Tabuleiro tabuleiro
) implements Solucao {

    public TreeNode<Posicao> encontrarPasseioDoCavalo6(Posicao posicaoInicial) {
        int movimentos = 1;
        final TreeNode<Posicao> treeNodeInicial = new TreeNode<Posicao>(posicaoInicial, movimentos, null);

        if (tabuleiro.dimensao() * tabuleiro.dimensao() > 63) {
            throw new IllegalArgumentException("This implementations supports a maximum of 63 visited positions, " +
                    "but the given board requires " + (tabuleiro.dimensao() * tabuleiro.dimensao()) + ".");
        }

        tabuleiro.mapaPosicaoInicialQuantidadeSolucoes().put(posicaoInicial, 0);

        // Inicializa com a posição inicial
        long bitMaskVisitados = Tabuleiro.marcarPosicaoVisitada(0L, tabuleiro.dimensao(), posicaoInicial);

        if (encontrarPasseioRecursivo6(treeNodeInicial, bitMaskVisitados, movimentos)) {
            return treeNodeInicial;
        }
        return null;
    }

    private boolean encontrarPasseioRecursivo6(TreeNode<Posicao> currentNode, long bitMaskVisitados, int movimentos) {
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

        final List<Posicao> proximosMovimentos = tabuleiro.grafoMovimentos().get(currentNode.value);

        // Paralelizar apenas no início da árvore
        final boolean usarParalelo = movimentos < (tabuleiro.dimensao() * tabuleiro.dimensao()) / 4;

        final Stream<Posicao> stream = usarParalelo ?
                proximosMovimentos.parallelStream() :
                proximosMovimentos.stream();

        stream.forEach(proximaPosicao -> {
            if (!Tabuleiro.isPosicaoVisitada(bitMaskVisitados, tabuleiro.dimensao(), proximaPosicao)) {

                final TreeNode<Posicao> childNode = currentNode.addChild(proximaPosicao);
                long bitMaskVisitadosNovo = Tabuleiro.marcarPosicaoVisitada(bitMaskVisitados, tabuleiro.dimensao(), proximaPosicao);

                encontrarPasseioRecursivo6(childNode, bitMaskVisitadosNovo, movimentos + 1);
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

    @Override
    public int calcular() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
