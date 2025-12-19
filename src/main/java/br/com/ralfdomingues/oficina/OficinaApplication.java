package br.com.ralfdomingues.oficina;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Classe principal da aplicação Oficina.
 *
 * <p>
 * Responsável por iniciar a aplicação Spring Boot e habilitar o agendamento de tarefas.
 * </p>
 */
@EnableScheduling // Habilita suporte a tarefas agendadas
@SpringBootApplication // Marca como aplicação Spring Boot
public class OficinaApplication {

    /**
     * Ponto de entrada da aplicação.
     *
     * @param args argumentos de linha de comando
     */
    public static void main(String[] args) {
        SpringApplication.run(OficinaApplication.class, args);
    }

}
