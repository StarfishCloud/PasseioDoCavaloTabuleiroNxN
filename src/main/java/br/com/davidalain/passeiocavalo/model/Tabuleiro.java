package br.com.davidalain.passeiocavalo.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public record Tabuleiro(
        int dimensao,
        Posicao[][] tabuleiro,
        Map<Posicao, List<Posicao>> grafoMovimentos,

        Map<Posicao, Integer> mapaPosicaoInicialQuantidadeSolucoes,
        Map<Posicao, TreeNode<Posicao>> mapaPosicaoInicialTreeNode
) {

    public Tabuleiro(
            int dimensao,
            Posicao[][] tabuleiro,
            Map<Posicao, List<Posicao>> grafoMovimentos,
            Map<Posicao, Integer> mapaPosicaoInicialQuantidadeSolucoes,
            Map<Posicao, TreeNode<Posicao>> mapaPosicaoInicialTreeNode
    ) {
        if (dimensao < 1 || dimensao > 8) {
            throw new IllegalArgumentException("Dimensão " + dimensao + " inválida. Utilize valores no intervalo: 1 <= dimensao <= 8.");
        }

        this.dimensao = dimensao;
        this.tabuleiro = tabuleiro;
        this.grafoMovimentos = grafoMovimentos;
        this.mapaPosicaoInicialQuantidadeSolucoes = mapaPosicaoInicialQuantidadeSolucoes;
        this.mapaPosicaoInicialTreeNode = mapaPosicaoInicialTreeNode;
    }

    // Construtor
    public Tabuleiro(int dimensao) {
        this(
                dimensao,
                Tabuleiro.gerarTabuleiro(dimensao),
                new HashMap<>(),
                new ConcurrentHashMap<>(),
                new ConcurrentHashMap<>()
        );

        this.grafoMovimentos.putAll(
                Tabuleiro.criarGrafoMovimentosCavalo(this.dimensao, this.tabuleiro)
        );
    }

    // Inicializa o tabuleiro criando posições
    private static Posicao[][] gerarTabuleiro(int dimensao) {
        Posicao[][] tabuleiro = new Posicao[dimensao][dimensao];
        for (int i = 0; i < dimensao; i++) {
            for (int j = 0; j < dimensao; j++) {
                tabuleiro[i][j] = new Posicao(i, j);
            }
        }
        return tabuleiro;
    }

//    // Retorna a posição em determinada coordenada
//    public Posicao getPosicao(int linha, int coluna) {
//        if (!Posicao.posicaoValida(linha, coluna, this.dimensao)) {
//            throw new IllegalArgumentException("Posição inválida");
//        }
//        return tabuleiro[linha][coluna];
//    }

    // Retorna a posição ordenada
    public Posicao getPosicaoOrdenada(int numOrdem) {
        if (numOrdem < 0 || numOrdem >= (this.dimensao * this.dimensao))
            throw new IllegalArgumentException("Posição inválida");

        int linha = numOrdem / this.dimensao;
        int coluna = numOrdem % this.dimensao;

        if (!Posicao.posicaoValida(linha, coluna, this.dimensao)) {
            throw new IllegalArgumentException("Posição inválida");
        }
        return tabuleiro[linha][coluna];
    }

    // Metodo para imprimir o tabuleiro (útil para debug)
    public void imprimirTabuleiro() {
        for (int i = 0; i < dimensao; i++) {
            for (int j = 0; j < dimensao; j++) {
                System.out.print("(" + tabuleiro[i][j].linha() +
                        "," + tabuleiro[i][j].coluna() + ") ");
            }
            System.out.println();
        }
    }

    public Map<Posicao, Integer> getMapaPosicaoInicialQuantidadeSolucoes() {
        return mapaPosicaoInicialQuantidadeSolucoes;
    }

    public Map<Posicao, TreeNode<Posicao>> getMapaPosicaoInicialTreeNode() {
        return mapaPosicaoInicialTreeNode;
    }

    // Metodo para gerar e armazenar todos os movimentos possíveis do cavalo
    private static Map<Posicao, List<Posicao>> criarGrafoMovimentosCavalo(int dimensao, Posicao[][] tabuleiro) {
        Map<Posicao, List<Posicao>> todosMovimentosCavalo = new HashMap<>();

        // Percorre todas as posições do tabuleiro
        for (int i = 0; i < dimensao; i++) {
            for (int j = 0; j < dimensao; j++) {
                Posicao posicaoAtual = tabuleiro[i][j];
                // Calcula os movimentos possíveis para a posição atual
                List<Posicao> movimentos = posicaoAtual.movimentosPossiveisCavalo(dimensao);
                // Armazena no HashMap
                todosMovimentosCavalo.put(posicaoAtual, movimentos);
            }
        }

        return todosMovimentosCavalo;
    }

    // Metodo auxiliar para verificar e definir uma posição no BitSet
    public static boolean isPosicaoVisitada(long bitMaskVisitados, int dimensao, Posicao posicao) {
        return (bitMaskVisitados & (1L << (posicao.linha() * dimensao + posicao.coluna()))) != 0;
    }

    public static long marcarPosicaoVisitada(long bitMaskVisitados, int dimensao, Posicao posicao) {
        bitMaskVisitados |= (1L << (posicao.linha() * dimensao + posicao.coluna()));
        return bitMaskVisitados;
    }

}