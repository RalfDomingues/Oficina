package br.com.ralfdomingues.oficina.infra.logging;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Serviço responsável pela limpeza automática de arquivos de log antigos.
 *
 * <p>
 * Atua como mecanismo de manutenção da infraestrutura de logging,
 * evitando acúmulo excessivo de arquivos no sistema de arquivos.
 *
 * <p>
 * A limpeza é executada automaticamente na inicialização da aplicação
 * e remove logs mais antigos que o período configurado.
 */
@Component
public class LogCleanupService {

    private static final Path LOG_DIR = Paths.get("logs");

    /**
     * Formato esperado para datas no nome dos arquivos de log.
     *
     * <p>
     * Exemplo: log-01-09-2024.txt
     */
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd-MM-yyyy");

    /**
     * Executa a limpeza de logs durante a inicialização da aplicação.
     *
     * <p>
     * Utiliza {@link PostConstruct} para garantir que a limpeza
     * ocorra apenas após a inicialização do contexto Spring.
     */
    @PostConstruct
    public void cleanupOnStartup() {
        cleanupLogs();
    }

    /**
     * Remove arquivos de log antigos do diretório de logs.
     *
     * <p>
     * São considerados para remoção apenas arquivos que seguem
     * o padrão de nomenclatura esperado e que sejam anteriores
     * ao período de retenção definido.
     *
     * <p>
     * Arquivos fora do padrão ou o log atual são preservados.
     */
    public void cleanupLogs() {
        if (!Files.exists(LOG_DIR)) {
            return;
        }

        try (DirectoryStream<Path> stream =
                     Files.newDirectoryStream(LOG_DIR, "log-*.txt")) {

            for (Path file : stream) {
                String fileName = file.getFileName().toString();

                // Ignora o arquivo atual
                if (fileName.equals("log-atual.txt")) {
                    continue;
                }

                // Extrai a data do nome
                String datePart = fileName
                        .replace("log-", "")
                        .replace(".txt", "");

                LocalDate logDate;
                try {
                    logDate = LocalDate.parse(datePart, FORMATTER);
                } catch (DateTimeParseException e) {
                    // ignora arquivos fora do padrão
                    continue;
                }

                if (logDate.isBefore(LocalDate.now().minusDays(7))) {
                    Files.deleteIfExists(file);
                }
            }

        } catch (IOException e) {
            /**
             * Falhas na limpeza não devem interromper a aplicação.
             *
             * O erro é registrado apenas no console para evitar
             * loops de logging.
             */
            System.err.println("Erro ao limpar logs antigos: " + e.getMessage());
        }
    }
}
