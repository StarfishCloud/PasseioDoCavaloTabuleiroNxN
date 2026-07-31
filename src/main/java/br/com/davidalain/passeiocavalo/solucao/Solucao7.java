package br.com.davidalain.passeiocavalo.solucao;

import br.com.davidalain.passeiocavalo.model.Posicao;
import br.com.davidalain.passeiocavalo.model.Tabuleiro;
import br.com.davidalain.passeiocavalo.model.TreeNode;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public record Solucao7(
        Tabuleiro tabuleiro
) implements Solucao {

    public int encontrarPasseioDoCavalo7(Posicao posicaoInicial) {
        int movimentosRealizados = 1;
        final TreeNode<Posicao> treeNodeInicial = new TreeNode<Posicao>(posicaoInicial, movimentosRealizados, null);

        if (tabuleiro.dimensao() * tabuleiro.dimensao() > 63) {
            throw new IllegalArgumentException(
                    "This implementations supports a path with maximum of 63 visited positions, " +
                            "but the given board requires " + (tabuleiro.dimensao() * tabuleiro.dimensao()) + ".");
        }

        final TreeNode<Posicao> treeNodeInicialRegistrado = tabuleiro.mapaPosicaoInicialTreeNode().get(posicaoInicial);
        if (treeNodeInicialRegistrado != null) {
            tabuleiro.mapaPosicaoInicialTreeNode().put(posicaoInicial, treeNodeInicial);
        }

        //Se já calculou a posição espelho original, usa-o
        final Posicao posicaoInicialEspelhoOriginal = posicaoInicial.posicaoEspelhoOriginal(tabuleiro.dimensao());
        final Integer quantidadeSolucoesEspelhoOriginal = tabuleiro.mapaPosicaoInicialQuantidadeSolucoes().get(posicaoInicialEspelhoOriginal);
        if (quantidadeSolucoesEspelhoOriginal != null) {
            tabuleiro.mapaPosicaoInicialQuantidadeSolucoes().put(posicaoInicial, quantidadeSolucoesEspelhoOriginal);

            return quantidadeSolucoesEspelhoOriginal;
        }
        // Começa a quantidade com zero e calcula o total de soluções
        else {
            tabuleiro.mapaPosicaoInicialQuantidadeSolucoes().put(posicaoInicial, 0);

            // Inicializa com a posição inicial
            long bitMaskVisitados = Tabuleiro.marcarPosicaoVisitada(0L, tabuleiro.dimensao(), posicaoInicial);

            encontrarPasseioRecursivo7(treeNodeInicial, bitMaskVisitados, movimentosRealizados);
        }
        return tabuleiro.mapaPosicaoInicialQuantidadeSolucoes().get(posicaoInicial);
    }

    private void encontrarPasseioRecursivo7(TreeNode<Posicao> currentNode, long bitMaskVisitados, int movimentosRealizados) {
        // Se já visitamos todas as casas, encontramos uma solução
        if (movimentosRealizados == tabuleiro.dimensao() * tabuleiro.dimensao()) {

            TreeNode<Posicao> currToPrint = currentNode;
            while ((currToPrint != null) && (currToPrint.parentNode != null)) {
                currToPrint = currToPrint.parentNode;
            }

            final Posicao primeiraPosicao = currToPrint.value;
            tabuleiro.mapaPosicaoInicialQuantidadeSolucoes().merge(primeiraPosicao, 1, Integer::sum);

            //System.out.println("==========================");
            //System.out.println("Posição Inicial: " + primeiraPosicao +
            //        ", Quantidade de Soluções Encontradas (até o momento): " + this.mapaPosicaoInicialQuantidadeSolucoes.get(primeiraPosicao));

            return;
        }

        final List<Posicao> proximosMovimentos = tabuleiro.grafoMovimentos().get(currentNode.value);

        // Paralelizar apenas no início da árvore
        final boolean usarParalelo = movimentosRealizados < (tabuleiro.dimensao() * tabuleiro.dimensao()) / 4;

        final Stream<Posicao> stream = usarParalelo ?
                proximosMovimentos.parallelStream() :
                proximosMovimentos.stream();

        stream.forEach(proximaPosicao -> {
            if (!Tabuleiro.isPosicaoVisitada(bitMaskVisitados, tabuleiro.dimensao(), proximaPosicao)) {

                final TreeNode<Posicao> childNode = currentNode.addChild(proximaPosicao);
                long bitMaskVisitadosNovo = Tabuleiro.marcarPosicaoVisitada(bitMaskVisitados, tabuleiro.dimensao(), proximaPosicao);

                encontrarPasseioRecursivo7(childNode, bitMaskVisitadosNovo, movimentosRealizados + 1);
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

    }

    @Override
    public int calcular() {

        //Primeiro calcula as posições "espelhos originais"
        IntStream.range(0, tabuleiro.dimensao() * tabuleiro.dimensao())
                .mapToObj(i -> tabuleiro.getPosicaoOrdenada(i).posicaoEspelhoOriginal(tabuleiro.dimensao()))
                .distinct()
                .parallel()
                .forEach(this::encontrarPasseioDoCavalo7);

        //Depois calcula todas as posições, reaproveitando as posições espelho originais já calculadas
        IntStream.range(0, tabuleiro.dimensao() * tabuleiro.dimensao())
                .sorted()
                .parallel()
                .mapToObj(tabuleiro::getPosicaoOrdenada)
                .forEach(this::encontrarPasseioDoCavalo7);

        //Soma os resultados
        return tabuleiro.getMapaPosicaoInicialQuantidadeSolucoes()
                .values()
                .stream()
                .mapToInt(Integer::intValue)
                .sum();

    }
}
