-- CLIENTES
INSERT INTO cliente (nome, telefone, email, cpf, ativo)
VALUES
    ('João da Silva', '48991000001', 'joao@gmail.com', '12345678901', TRUE),
    ('Maria Oliveira', '48991000002', 'maria@gmail.com', '98765432100', TRUE),
    ('Carlos Pereira', '48991000003', 'carlos@gmail.com', '55566677788', TRUE);


-- VEICULOS
INSERT INTO veiculo (cliente_id, placa, modelo, marca, ano, tipo, ativo)
VALUES
    (1, 'ABC1A11', 'Civic', 'Honda', 2018, 'CARRO', TRUE),
    (1, 'DEF2B22', 'Uno',   'Fiat',  2012, 'CARRO', TRUE),
    (2, 'GHI3C33', 'CG 160', 'Honda', 2020, 'MOTO', TRUE);


-- SERVICOS
INSERT INTO servico (nome, preco)
VALUES
    ('Troca de óleo', 120.00),
    ('Alinhamento',    90.00),
    ('Balanceamento',  60.00),
    ('Revisão geral', 300.00),
    ('Troca de velas', 80.00);


-- ORDENS DE SERVICO
INSERT INTO ordens_servico
(cliente_id, veiculo_id, descricao, data_abertura, status, valor_estimado, valor_final)
VALUES
    (1, 1, 'Barulho na suspensão ao passar em lombadas', NOW(), 'ABERTA', 200.00, NULL),
    (2, 3, 'Revisão completa antes de viagem',          NOW(), 'ABERTA', 500.00, NULL);


-- ITENS DE SERVICO (OS 1)
INSERT INTO itens_servico (ordem_id, servico_id, quantidade, valor_unitario)
VALUES
    (1, 1, 1, 120.00),  -- troca de óleo
    (1, 2, 1, 90.00);   -- alinhamento

-- Atualiza valor_final da OS 1
UPDATE ordens_servico
SET valor_final = (
    SELECT SUM(quantidade * valor_unitario)
    FROM itens_servico WHERE ordem_id = 1
)
WHERE id = 1;


-- ITENS DE SERVICO (OS 2)
INSERT INTO itens_servico (ordem_id, servico_id, quantidade, valor_unitario)
VALUES
    (2, 4, 1, 300.00),  -- revisão geral
    (2, 5, 4, 80.00);   -- troca de velas (4 unidades)

-- Atualiza valor_final da OS 2
UPDATE ordens_servico
SET valor_final = (
    SELECT SUM(quantidade * valor_unitario)
    FROM itens_servico WHERE ordem_id = 2
)
WHERE id = 2;
