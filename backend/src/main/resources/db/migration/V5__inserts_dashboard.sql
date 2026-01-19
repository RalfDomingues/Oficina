INSERT INTO cliente (nome, telefone, email, cpf)
VALUES ('João Silva', '47999990001', 'joao@email.com', '111.111.111-11'),
       ('Maria Souza', '47999990002', 'maria@email.com', '222.222.222-22'),
       ('Carlos Pereira', '47999990003', 'carlos@email.com', '333.333.333-33'),
       ('Ana Costa', '47999990004', 'ana@email.com', '444.444.444-44');

INSERT INTO veiculo (cliente_id, placa, modelo, marca, ano, tipo)
VALUES (1, 'ABC1A12', 'Gol', 'Volkswagen', 2015, 'CARRO'),
       (2, 'DEF2B23', 'Onix', 'Chevrolet', 2019, 'CARRO'),
       (3, 'GHI3C34', 'Civic', 'Honda', 2018, 'CARRO'),
       (4, 'JKL4D44', 'CG 160', 'Honda', 2022, 'MOTO');

INSERT INTO servico (nome, preco)
VALUES ('Troca de óleo', 150.00),
       ('Alinhamento', 120.00),
       ('Balanceamento', 100.00),
       ('Troca de velas', 180.00),
       ('Revisão geral', 450.00);

INSERT INTO ordens_servico
(cliente_id, veiculo_id, descricao, data_abertura, data_conclusao, status, valor_estimado, valor_final)
VALUES
-- Janeiro
(1, 1, 'Troca de óleo e revisão', '2025-01-10 10:00', '2025-01-10 15:00', 'CONCLUIDA', 600, 600),

-- Fevereiro
(2, 2, 'Alinhamento e balanceamento', '2025-02-05 09:00', '2025-02-05 12:00', 'CONCLUIDA', 220, 220),

-- Março
(3, 3, 'Troca de velas', '2025-03-18 14:00', '2025-03-18 16:00', 'CONCLUIDA', 180, 180),

-- Abril
(4, 4, 'Revisão preventiva', '2025-04-02 08:30', '2025-04-02 17:00', 'CONCLUIDA', 450, 450),

-- Maio (ABERTA)
(1, 1, 'Nova revisão', '2025-05-20 11:00', NULL, 'ABERTA', 500, NULL),

-- Junho (CANCELADA)
(2, 2, 'Serviço cancelado pelo cliente', '2025-06-10 09:00', NULL, 'CANCELADA', 300, NULL),

-- Julho
(3, 3, 'Troca de óleo', '2025-07-08 10:00', '2025-07-08 11:00', 'CONCLUIDA', 150, 150),

-- Agosto
(4, 4, 'Revisão geral', '2025-08-12 13:00', '2025-08-12 18:00', 'CONCLUIDA', 450, 450);

INSERT INTO itens_servico (ordem_id, servico_id, quantidade, valor_unitario)
VALUES
-- OS 1
(1, 1, 1, 150),
(1, 5, 1, 450),

-- OS 2
(2, 2, 1, 120),
(2, 3, 1, 100),

-- OS 3
(3, 4, 1, 180),

-- OS 4
(4, 5, 1, 450),

-- OS 7
(7, 1, 1, 150),

-- OS 8
(8, 5, 1, 450),
(8, 1, 1, 150);


