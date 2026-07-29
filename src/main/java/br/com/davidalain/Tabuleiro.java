package br.com.davidalain;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class Tabuleiro {
    private final int dimensao;
    private final Posicao[][] tabuleiro;
    private final Map<Posicao, List<Posicao>> grafoMovimentos;

    private final Map<Posicao, Integer> mapaPosicaoInicialQuantidadeSolucoes;
    private final Map<Posicao, TreeNode<Posicao>> mapaPosicaoInicialTreeNode;

    // Construtor
    public Tabuleiro(int dimensao) {
        if (dimensao <= 0) {
            throw new IllegalArgumentException("Dimensão deve ser maior que zero");
        }

        this.dimensao = dimensao;
        this.tabuleiro = new Posicao[dimensao][dimensao];
        inicializarTabuleiro();

        this.grafoMovimentos = criarGrafoMovimentosCavalo();

        this.mapaPosicaoInicialQuantidadeSolucoes = new ConcurrentHashMap<>();
        this.mapaPosicaoInicialTreeNode = new ConcurrentHashMap<>();
    }

    // Inicializa o tabuleiro criando posições
    private void inicializarTabuleiro() {
        for (int i = 0; i < dimensao; i++) {
            for (int j = 0; j < dimensao; j++) {
                tabuleiro[i][j] = new Posicao(i, j);
            }
        }
    }

    // Verifica se uma posição está dentro dos limites do tabuleiro
    /*public boolean posicaoValida(int linha, int coluna) {
        return linha >= 0 && linha < dimensao && coluna >= 0 && coluna < dimensao;
    }*/

    // Retorna a posição em determinada coordenada
    public Posicao getPosicao(int linha, int coluna) {
        if (!Posicao.posicaoValida(linha, coluna, this.dimensao)) {
            throw new IllegalArgumentException("Posição inválida");
        }
        return tabuleiro[linha][coluna];
    }

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

    // Retorna a dimensão do tabuleiro
    public int getDimensao() {
        return dimensao;
    }

    // Metodo para imprimir o tabuleiro (útil para debug)
    public void imprimirTabuleiro() {
        for (int i = 0; i < dimensao; i++) {
            for (int j = 0; j < dimensao; j++) {
                System.out.print("(" + tabuleiro[i][j].getLinha() +
                        "," + tabuleiro[i][j].getColuna() + ") ");
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
    private Map<Posicao, List<Posicao>> criarGrafoMovimentosCavalo() {
        Map<Posicao, List<Posicao>> todosMovimentosCavalo = new HashMap<>();

        // Percorre todas as posições do tabuleiro
        for (int i = 0; i < dimensao; i++) {
            for (int j = 0; j < dimensao; j++) {
                Posicao posicaoAtual = tabuleiro[i][j];
                // Calcula os movimentos possíveis para a posição atual
                List<Posicao> movimentos = posicaoAtual.movimentosPossiveisCavalo(this.dimensao);
                // Armazena no HashMap
                todosMovimentosCavalo.put(posicaoAtual, movimentos);
            }
        }

        return todosMovimentosCavalo;
    }

    /*
    public List<Posicao> encontrarPasseioDoCavalo(Posicao inicio) {
        List<Posicao> caminho = new ArrayList<>();
        Set<Posicao> visitados = new HashSet<>();

        // PriorityQueue ordenada pelo número de movimentos disponíveis (heurística de Warnsdorff)
        PriorityQueue<Posicao> fila = new PriorityQueue<>((p1, p2) -> {
            List<Posicao> movs1 = this.grafoMovimentos.get(p1);
            List<Posicao> movs2 = this.grafoMovimentos.get(p2);
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
        int totalPosicoes = dimensao * dimensao;

        while (!fila.isEmpty() && visitados.size() < totalPosicoes) {
            Posicao atual = fila.poll();
            List<Posicao> movimentosPossiveis = grafoMovimentos.get(atual);

            // Ordena movimentos pela heurística de Warnsdorff (menos movimentos disponíveis primeiro)
            List<Posicao> movimentosOrdenados = movimentosPossiveis.stream()
                    .filter(p -> !visitados.contains(p))
                    .sorted((p1, p2) -> {
                        long movs1 = grafoMovimentos.get(p1).stream()
                                .filter(p -> !visitados.contains(p)).count();
                        long movs2 = grafoMovimentos.get(p2).stream()
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

    //===================================================================================
    //===================================================================================

    public List<Posicao> encontrarPasseioDoCavalo2(Posicao inicio) {
        List<Posicao> caminho = new ArrayList<>();
        boolean[][] visitado = new boolean[dimensao][dimensao];

        // Inicializa com a posição inicial
        caminho.add(inicio);
        visitado[inicio.getLinha()][inicio.getColuna()] = true;

        if (encontrarPasseioRecursivo(inicio, caminho, visitado, 1)) {
            return caminho;
        }
        return null;
    }


    private boolean encontrarPasseioRecursivo(Posicao atual, List<Posicao> caminho,
                                              boolean[][] visitado, int movimentos) {
        // Se já visitamos todas as casas, encontramos uma solução
        if (movimentos == dimensao * dimensao) {
            return true;
        }

        List<Posicao> proximosMovimentos = grafoMovimentos.get(atual);

        // Ordena os movimentos usando a heurística de Warnsdorff
        proximosMovimentos.sort((p1, p2) -> {
            long movs1 = contarMovimentosDisponiveis(p1, visitado);
            long movs2 = contarMovimentosDisponiveis(p2, visitado);
            return Long.compare(movs1, movs2);
        });

        // Tenta cada movimento possível
        for (Posicao proxima : proximosMovimentos) {
            if (!visitado[proxima.getLinha()][proxima.getColuna()]) {
                // Tenta este movimento
                visitado[proxima.getLinha()][proxima.getColuna()] = true;
                caminho.add(proxima);

                if (encontrarPasseioRecursivo(proxima, caminho, visitado, movimentos + 1)) {
                    return true;
                }

                // Se não funcionou, desfaz o movimento (backtracking)
                visitado[proxima.getLinha()][proxima.getColuna()] = false;
                caminho.remove(caminho.size() - 1);
            }
        }

        return false;
    }

    private int contarMovimentosDisponiveis(Posicao posicao, boolean[][] visitado) {
        return (int) grafoMovimentos.get(posicao).stream()
                .filter(p -> !visitado[p.getLinha()][p.getColuna()])
                .count();
    }

     */

    //===================================================================================
    //===================================================================================

    public TreeNode<Posicao> encontrarPasseioDoCavalo3(Posicao inicio) {
        int movimentos = 1;
        final TreeNode<Posicao> initialNode = new TreeNode<>(inicio, movimentos, null);
        boolean[][] visitados = new boolean[dimensao][dimensao];

        // Inicializa com a posição inicial
        visitados[inicio.linha][inicio.coluna] = true;

        if (encontrarPasseioRecursivo3(initialNode, visitados, movimentos)) {
            return initialNode;
        }
        return null;
    }

    private boolean encontrarPasseioRecursivo3(TreeNode<Posicao> currentNode, boolean[][] visitados, int movimentos) {
        // Se já visitamos todas as casas, encontramos uma solução
        if (movimentos == dimensao * dimensao) {
            System.out.println("==========================");

            final List<Posicao> caminho = new ArrayList<>();
            TreeNode<Posicao> currToPrint = currentNode;

            while (currToPrint != null) {
                caminho.add(currToPrint.value);
                currToPrint = currToPrint.parentNode;
            }

            Collections.reverse(caminho);

            final Posicao primeiraPosicao = caminho.get(0);
            int quantidadeSolucoes = 1 + this.mapaPosicaoInicialQuantidadeSolucoes.getOrDefault(primeiraPosicao, 0);
            this.mapaPosicaoInicialQuantidadeSolucoes.put(primeiraPosicao, quantidadeSolucoes);

            System.out.println("Posição Inicial: " + primeiraPosicao + " - Quantidade de Soluções Encontradas (até o momento): " + quantidadeSolucoes);
            //System.out.println("Solução mais recente: " + caminho.stream().map(Object::toString).collect(Collectors.joining(" -> ")));

            return true;
        }

        final List<Posicao> proximosMovimentos = grafoMovimentos.get(currentNode.value);

        //((movimentos % 2 == 0) ?
        (proximosMovimentos.parallelStream())
                //        :
                //        (proximosMovimentos.stream())
                //)
                .forEach(proximaPosicao -> {
                    if (!visitados[proximaPosicao.linha][proximaPosicao.coluna]) {

                        final TreeNode<Posicao> childNode = currentNode.addChild(proximaPosicao);

                        boolean[][] visitadosNovo = new boolean[dimensao][dimensao];
                        for (int i = 0; i < dimensao; i++) {
                            visitadosNovo[i] = Arrays.copyOf(visitados[i], dimensao);
                        }
                        visitadosNovo[proximaPosicao.linha][proximaPosicao.coluna] = true;

                        encontrarPasseioRecursivo3(childNode, visitadosNovo, movimentos + 1);
                    }
                });


        //System.out.println("#########################");
        if (currentNode.depth < (dimensao * dimensao - 2) &&
                currentNode.childNodes.isEmpty() &&
                currentNode.parentNode != null) {
            //System.out.println("currentNode.parentNode.deleteChild(" + currentNode + ")");
            currentNode.parentNode.deleteChild(currentNode);
        }

        return false;
    }

    //===================================================================================
    //===================================================================================

    public TreeNode<Posicao> encontrarPasseioDoCavalo4(Posicao posicaoInicial) {
        int movimentos = 1;
        final TreeNode<Posicao> treeNodeInicial = new TreeNode<>(posicaoInicial, movimentos, null);
        boolean[][] visitados = new boolean[dimensao][dimensao];

        this.mapaPosicaoInicialQuantidadeSolucoes.put(posicaoInicial, 0);

        // Inicializa com a posição inicial
        visitados[posicaoInicial.linha][posicaoInicial.coluna] = true;

        if (encontrarPasseioRecursivo4(treeNodeInicial, visitados, movimentos)) {
            return treeNodeInicial;
        }
        return null;
    }

    private boolean encontrarPasseioRecursivo4(TreeNode<Posicao> currentNode, boolean[][] visitados, int movimentos) {
        // Se já visitamos todas as casas, encontramos uma solução
        if (movimentos == dimensao * dimensao) {

            TreeNode<Posicao> currToPrint = currentNode;
            while ((currToPrint != null) && (currToPrint.parentNode != null)) {
                currToPrint = currToPrint.parentNode;
            }

            final Posicao primeiraPosicao = currToPrint.value;
            this.mapaPosicaoInicialQuantidadeSolucoes.merge(primeiraPosicao, 1, Integer::sum);

            //System.out.println("==========================");
            //System.out.println("Posição Inicial: " + primeiraPosicao +
            //        ", Quantidade de Soluções Encontradas (até o momento): " + this.mapaPosicaoInicialQuantidadeSolucoes.get(primeiraPosicao));

            return true;
        }

        final List<Posicao> proximosMovimentos = grafoMovimentos.get(currentNode.value);

        // Paralelizar apenas no início da árvore
        final boolean usarParalelo = movimentos < (dimensao * dimensao) / 4;

        final Stream<Posicao> stream = usarParalelo ?
                proximosMovimentos.parallelStream() :
                proximosMovimentos.stream();

        stream.forEach(proximaPosicao -> {
            if (!visitados[proximaPosicao.linha][proximaPosicao.coluna]) {

                final TreeNode<Posicao> childNode = currentNode.addChild(proximaPosicao);

                final boolean[][] visitadosNovo = new boolean[dimensao][dimensao];
                for (int i = 0; i < dimensao; i++) {
                    visitadosNovo[i] = Arrays.copyOf(visitados[i], dimensao);
                }
                visitadosNovo[proximaPosicao.linha][proximaPosicao.coluna] = true;

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
        boolean[][] visitadosNovo = new boolean[dimensao][dimensao];
        for (int i = 0; i < dimensao; i++) {
            visitadosNovo[i] = Arrays.copyOf(visitados[i], dimensao);
        }
        return visitadosNovo;
    }

    //===================================================================================
    //===================================================================================

    public TreeNode<Posicao> encontrarPasseioDoCavalo5(Posicao posicaoInicial) {
        int movimentos = 1;
        final TreeNode<Posicao> treeNodeInicial = new TreeNode<Posicao>(posicaoInicial, movimentos, null);

        this.mapaPosicaoInicialQuantidadeSolucoes.put(posicaoInicial, 0);

        if (dimensao * dimensao > 63) {
            throw new IllegalArgumentException("This implementations supports a maximum of 63 visited positions, but the given board requires " + (dimensao * dimensao) + ".");
        }

        // Inicializa com a posição inicial
        long bitMaskVisitados = marcarPosicaoVisitada(0L, posicaoInicial);

        if (encontrarPasseioRecursivo5(treeNodeInicial, bitMaskVisitados, movimentos)) {
            return treeNodeInicial;
        }
        return null;
    }

    private boolean encontrarPasseioRecursivo5(TreeNode<Posicao> currentNode, long bitMaskVisitados, int movimentos) {
        // Se já visitamos todas as casas, encontramos uma solução
        if (movimentos == dimensao * dimensao) {

            TreeNode<Posicao> currToPrint = currentNode;
            while ((currToPrint != null) && (currToPrint.parentNode != null)) {
                currToPrint = currToPrint.parentNode;
            }

            final Posicao primeiraPosicao = currToPrint.value;
            this.mapaPosicaoInicialQuantidadeSolucoes.merge(primeiraPosicao, 1, Integer::sum);

            //System.out.println("==========================");
            //System.out.println("Posição Inicial: " + primeiraPosicao +
            //        ", Quantidade de Soluções Encontradas (até o momento): " + this.mapaPosicaoInicialQuantidadeSolucoes.get(primeiraPosicao));

            return true;
        }

        final List<Posicao> proximosMovimentos = grafoMovimentos.get(currentNode.value);

        // Paralelizar apenas no início da árvore
        final boolean usarParalelo = movimentos < (dimensao * dimensao) / 4;

        final Stream<Posicao> stream = usarParalelo ?
                proximosMovimentos.parallelStream() :
                proximosMovimentos.stream();

        stream.forEach(proximaPosicao -> {
            if (!isPosicaoVisitada(bitMaskVisitados, proximaPosicao)) {

                final TreeNode<Posicao> childNode = currentNode.addChild(proximaPosicao);
                long bitMaskVisitadosNovo = marcarPosicaoVisitada(bitMaskVisitados, proximaPosicao);

                encontrarPasseioRecursivo5(childNode, bitMaskVisitadosNovo, movimentos + 1);
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

    // Metodo auxiliar para verificar e definir uma posição no BitSet
    private boolean isPosicaoVisitada(long visitados, Posicao posicao) {
        return (visitados & (1L << (posicao.linha * dimensao + posicao.coluna))) != 0;
    }

    private long marcarPosicaoVisitada(long visitados, Posicao posicao) {
        visitados |= (1L << (posicao.linha * dimensao + posicao.coluna));
        return visitados;
    }


    //===================================================================================
    //===================================================================================

    public TreeNode<Posicao> encontrarPasseioDoCavalo6(Posicao posicaoInicial) {
        int movimentos = 1;
        final TreeNode<Posicao> treeNodeInicial = new TreeNode<Posicao>(posicaoInicial, movimentos, null);

        if (dimensao * dimensao > 63) {
            throw new IllegalArgumentException("This implementations supports a maximum of 63 visited positions, but the given board requires " + (dimensao * dimensao) + ".");
        }

        this.mapaPosicaoInicialQuantidadeSolucoes.put(posicaoInicial, 0);

        // Inicializa com a posição inicial
        long bitMaskVisitados = marcarPosicaoVisitada(0L, posicaoInicial);

        if (encontrarPasseioRecursivo6(treeNodeInicial, bitMaskVisitados, movimentos)) {
            return treeNodeInicial;
        }
        return null;
    }

    private boolean encontrarPasseioRecursivo6(TreeNode<Posicao> currentNode, long bitMaskVisitados, int movimentos) {
        // Se já visitamos todas as casas, encontramos uma solução
        if (movimentos == dimensao * dimensao) {

            TreeNode<Posicao> currToPrint = currentNode;
            while ((currToPrint != null) && (currToPrint.parentNode != null)) {
                currToPrint = currToPrint.parentNode;
            }

            final Posicao primeiraPosicao = currToPrint.value;
            this.mapaPosicaoInicialQuantidadeSolucoes.merge(primeiraPosicao, 1, Integer::sum);

            //System.out.println("==========================");
            //System.out.println("Posição Inicial: " + primeiraPosicao +
            //        ", Quantidade de Soluções Encontradas (até o momento): " + this.mapaPosicaoInicialQuantidadeSolucoes.get(primeiraPosicao));

            return true;
        }

        final List<Posicao> proximosMovimentos = grafoMovimentos.get(currentNode.value);

        // Paralelizar apenas no início da árvore
        final boolean usarParalelo = movimentos < (dimensao * dimensao) / 4;

        final Stream<Posicao> stream = usarParalelo ?
                proximosMovimentos.parallelStream() :
                proximosMovimentos.stream();

        stream.forEach(proximaPosicao -> {
            if (!isPosicaoVisitada(bitMaskVisitados, proximaPosicao)) {

                final TreeNode<Posicao> childNode = currentNode.addChild(proximaPosicao);
                long bitMaskVisitadosNovo = marcarPosicaoVisitada(bitMaskVisitados, proximaPosicao);

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

    //
    //===================================================================================
    //===================================================================================

    /**
     *
     */
    public int encontrarPasseioDoCavalo7(Posicao posicaoInicial) {
        int movimentosRealizados = 1;
        final TreeNode<Posicao> treeNodeInicial = new TreeNode<Posicao>(posicaoInicial, movimentosRealizados, null);

        if (dimensao * dimensao > 63) {
            throw new IllegalArgumentException(
                    "This implementations supports a path with maximum of 63 visited positions, " +
                            "but the given board requires " + (dimensao * dimensao) + ".");
        }

        final TreeNode<Posicao> treeNodeInicialRegistrado = this.mapaPosicaoInicialTreeNode.get(posicaoInicial);
        if(treeNodeInicialRegistrado != null) {
            this.mapaPosicaoInicialTreeNode.put(posicaoInicial, treeNodeInicial);
        }

        //Se já calculou a posição espelho original, usa-o
        final Posicao posicaoInicialEspelhoOriginal = posicaoInicial.posicaoEspelhoOriginal(dimensao);
        final Integer quantidadeSolucoesEspelhoOriginal = this.mapaPosicaoInicialQuantidadeSolucoes.get(posicaoInicialEspelhoOriginal);
        if (quantidadeSolucoesEspelhoOriginal != null) {
            this.mapaPosicaoInicialQuantidadeSolucoes.put(posicaoInicial, quantidadeSolucoesEspelhoOriginal);

            return quantidadeSolucoesEspelhoOriginal;
        }
        // Começa a quantidade com zero e calcula o total de soluções
        else {
            this.mapaPosicaoInicialQuantidadeSolucoes.put(posicaoInicial, 0);

            // Inicializa com a posição inicial
            long bitMaskVisitados = marcarPosicaoVisitada(0L, posicaoInicial);

            encontrarPasseioRecursivo7(treeNodeInicial, bitMaskVisitados, movimentosRealizados);
        }
        return this.mapaPosicaoInicialQuantidadeSolucoes.get(posicaoInicial);
    }

    private void encontrarPasseioRecursivo7(TreeNode<Posicao> currentNode, long bitMaskVisitados, int movimentosRealizados) {
        // Se já visitamos todas as casas, encontramos uma solução
        if (movimentosRealizados == dimensao * dimensao) {

            TreeNode<Posicao> currToPrint = currentNode;
            while ((currToPrint != null) && (currToPrint.parentNode != null)) {
                currToPrint = currToPrint.parentNode;
            }

            final Posicao primeiraPosicao = currToPrint.value;
            this.mapaPosicaoInicialQuantidadeSolucoes.merge(primeiraPosicao, 1, Integer::sum);

            //System.out.println("==========================");
            //System.out.println("Posição Inicial: " + primeiraPosicao +
            //        ", Quantidade de Soluções Encontradas (até o momento): " + this.mapaPosicaoInicialQuantidadeSolucoes.get(primeiraPosicao));

            return;
        }

        final List<Posicao> proximosMovimentos = grafoMovimentos.get(currentNode.value);

        // Paralelizar apenas no início da árvore
        final boolean usarParalelo = movimentosRealizados < (dimensao * dimensao) / 4;

        final Stream<Posicao> stream = usarParalelo ?
                proximosMovimentos.parallelStream() :
                proximosMovimentos.stream();

        stream.forEach(proximaPosicao -> {
            if (!isPosicaoVisitada(bitMaskVisitados, proximaPosicao)) {

                final TreeNode<Posicao> childNode = currentNode.addChild(proximaPosicao);
                long bitMaskVisitadosNovo = marcarPosicaoVisitada(bitMaskVisitados, proximaPosicao);

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

}