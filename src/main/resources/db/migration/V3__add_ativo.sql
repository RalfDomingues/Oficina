-- adicionar coluna ativo em servico
ALTER TABLE servico
    ADD COLUMN ativo BOOLEAN NOT NULL DEFAULT TRUE;

-- adicionar coluna ativo em itens_servico
ALTER TABLE itens_servico
    ADD COLUMN ativo BOOLEAN NOT NULL DEFAULT TRUE;

UPDATE servico SET ativo = TRUE WHERE ativo IS NULL;
UPDATE itens_servico SET ativo = TRUE WHERE ativo IS NULL;

