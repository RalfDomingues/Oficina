package br.com.ralfdomingues.oficina.infra.logging;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Component
public class LogCleanupService {

    private static final Path LOG_DIR = Paths.get("logs");
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @PostConstruct
    public void cleanupOnStartup() {
        cleanupLogs();
    }

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
            // loga no console apenas, para não causar loop
            System.err.println("Erro ao limpar logs antigos: " + e.getMessage());
        }
    }
}
