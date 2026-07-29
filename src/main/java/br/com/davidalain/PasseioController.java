package br.com.davidalain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/api/cavalo")
public class PasseioController {

    private final String tokenServidor;

    public PasseioController(@Value("${API_TOKEN}") String tokenServidor) {
        this.tokenServidor = tokenServidor;
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("UP");
    }

    @PostMapping("/calcular")
    public ResponseEntity<Map<String, Object>> calcularPasseio(@RequestBody CavaloRequest request) {
        
        // Validação de segurança: verifica se o request chegou e se o token é igual ao definido no Kubernetes
        if (request == null || request.getToken() == null || !request.getToken().equals(tokenServidor)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("erro", "Acesso negado. Token inválido ou ausente."));
        }

        int dimensao = request.getDimensao() > 0 ? request.getDimensao() : 6;
        
        if (dimensao > 8) { 
            return ResponseEntity.badRequest().body(Map.of("erro", "Dimensão muito alta, máximo permitido é 8"));
        }

        long timeIni = System.currentTimeMillis();
        final Tabuleiro tabuleiro = new Tabuleiro(dimensao);

        IntStream.range(0, dimensao * dimensao)
                .mapToObj(i -> tabuleiro.getPosicaoOrdenada(i).posicaoEspelhoOriginal(dimensao))
                .collect(Collectors.toSet())
                .forEach(tabuleiro::encontrarPasseioDoCavalo7);

        IntStream.range(0, dimensao * dimensao)
                .sorted()
                .parallel()
                .mapToObj(tabuleiro::getPosicaoOrdenada)
                .forEach(tabuleiro::encontrarPasseioDoCavalo7);

        long timeEnd = System.currentTimeMillis();

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