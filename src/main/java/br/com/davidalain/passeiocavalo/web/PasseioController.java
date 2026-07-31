package br.com.davidalain.passeiocavalo.web;

import br.com.davidalain.passeiocavalo.model.Tabuleiro;
import br.com.davidalain.passeiocavalo.solucao.Solucao7;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

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

        if (request.dimensao() < 1 || request.dimensao() > 8) {
            return ResponseEntity.badRequest().body(
                    Map.of("erro", "Dimensão " + request.dimensao() + " inválida. Utilize valores no intervalo: 1 <= dimensao <= 8.")
            );
        }

        final Solucao7 solucao = new Solucao7(
                new Tabuleiro(request.dimensao())
        );

        //Tempo inicial
        long timeIni = System.currentTimeMillis();

        //Soma os resultados
        int somaTotal = solucao.calcular();

        //Tempo final
        long timeEnd = System.currentTimeMillis();

        return ResponseEntity.ok(Map.of(
                "dimensaoTabuleiro", request.dimensao() + "x" + request.dimensao(),
                "totalSolucoes", somaTotal,
                "tempoExecucaoMs", (timeEnd - timeIni),
                "status", "Sucesso"
        ));
    }
}