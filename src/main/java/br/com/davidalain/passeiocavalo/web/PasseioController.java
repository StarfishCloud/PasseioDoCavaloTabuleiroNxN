package br.com.davidalain.passeiocavalo.web;

import br.com.davidalain.passeiocavalo.model.Tabuleiro;
import br.com.davidalain.passeiocavalo.solucao.Solucao7;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/api/passeiocavalo")
public class PasseioController {

    private final String tokenServidor;

    public PasseioController(@Value("${API_TOKEN}") String tokenServidor) {
        this.tokenServidor = tokenServidor;
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        return ResponseEntity.ok(Map.of("status", "UP"));
    }

    @PostMapping("/calcular")
    public ResponseEntity<Map<String, Object>> calcularPasseio(@RequestBody CavaloRequest request) {
        
        // Validação de segurança: verifica se o request chegou e se o token é igual ao definido no Kubernetes
        if (request == null || request.token() == null || !Objects.equals(request.token(), tokenServidor)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("erro", "Acesso negado. Token inválido ou ausente."));
        }

        int dimensao = request.dimensao() > 0 ? request.dimensao() : 6;
        
        if (dimensao > 8) { 
            return ResponseEntity.badRequest().body(Map.of("erro", "Dimensão muito alta, máximo permitido é 8"));
        }

        long timeIni = System.currentTimeMillis();
        final Tabuleiro tabuleiro = new Tabuleiro(dimensao);
        final Solucao7 solucao = new Solucao7(tabuleiro);

        //Primeiro calcula as posições "espelhos originais"
        IntStream.range(0, dimensao * dimensao)
                .mapToObj(i -> tabuleiro.getPosicaoOrdenada(i).posicaoEspelhoOriginal(dimensao))
                .distinct()
                .parallel()
                .forEach(solucao::encontrarPasseioDoCavalo7);

        //Depois calcula todas as posições, reaproveitando as posições espelho originais já calculadas
        IntStream.range(0, dimensao * dimensao)
                .sorted()
                .parallel()
                .mapToObj(tabuleiro::getPosicaoOrdenada)
                .forEach(solucao::encontrarPasseioDoCavalo7);

        long timeEnd = System.currentTimeMillis();

        //Soma os resultados
        int somaTotal = tabuleiro.getMapaPosicaoInicialQuantidadeSolucoes()
                .values()
                .stream()
                .mapToInt(Integer::intValue)
                .sum();

        return ResponseEntity.ok(Map.of(
                "dimensaoTabuleiro", dimensao + "x" + dimensao,
                "totalSolucoes", somaTotal,
                "tempoExecucaoMs", (timeEnd - timeIni),
                "status", "Sucesso"
        ));
    }
}