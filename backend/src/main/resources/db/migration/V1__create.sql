CREATE TABLE cliente
(
    id       BIGSERIAL PRIMARY KEY,
    nome     VARCHAR(150) NOT NULL,
    telefone VARCHAR(30)  NOT NULL,
    email    VARCHAR(150),
    cpf      VARCHAR(14)  NOT NULL UNIQUE,
    ativo    BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE veiculo
(
    id         BIGSERIAL PRIMARY KEY,
    cliente_id BIGINT       NOT NULL,
    placa      VARCHAR(10)  NOT NULL UNIQUE,
    modelo     VARCHAR(100) NOT NULL,
    marca      VARCHAR(100) NOT NULL,
    ano        INT          NOT NULL,
    tipo       VARCHAR(20)  NOT NULL,
    ativo      BOOLEAN      NOT NULL DEFAULT TRUE,

    CONSTRAINT fk_veiculo_cliente
        FOREIGN KEY (cliente_id) REFERENCES cliente (id)
);

CREATE TABLE servico
(
    id    BIGSERIAL PRIMARY KEY,
    nome  VARCHAR(120)   NOT NULL,
    preco NUMERIC(10, 2) NOT NULL CHECK (preco >= 0.01)
);

CREATE TABLE ordens_servico
(
    id             BIGSERIAL PRIMARY KEY,
    cliente_id     BIGINT       NOT NULL,
    veiculo_id     BIGINT       NOT NULL,
    descricao      VARCHAR(300) NOT NULL,
    data_abertura  TIMESTAMP    NOT NULL,
    data_conclusao TIMESTAMP,
    status         VARCHAR(30)  NOT NULL,
    valor_estimado NUMERIC(10, 2),
    valor_final    NUMERIC(10, 2),

    CONSTRAINT fk_os_cliente FOREIGN KEY (cliente_id) REFERENCES cliente (id),
    CONSTRAINT fk_os_veiculo FOREIGN KEY (veiculo_id) REFERENCES veiculo (id)
);

CREATE TABLE itens_servico
(
    id             BIGSERIAL PRIMARY KEY,
    ordem_id       BIGINT         NOT NULL,
    servico_id     BIGINT         NOT NULL,
    quantidade     INT            NOT NULL CHECK (quantidade >= 1),
    valor_unitario NUMERIC(10, 2) NOT NULL CHECK (valor_unitario >= 0.01),

    CONSTRAINT fk_item_os FOREIGN KEY (ordem_id) REFERENCES ordens_servico (id),
    CONSTRAINT fk_item_servico FOREIGN KEY (servico_id) REFERENCES servico (id)
);
