package br.com.ralfdomingues.oficina.domain.usuario.service;

import br.com.ralfdomingues.oficina.domain.usuario.dto.UsuarioCreateDTO;
import br.com.ralfdomingues.oficina.domain.usuario.dto.UsuarioResponseDTO;
import br.com.ralfdomingues.oficina.domain.usuario.dto.UsuarioUpdateDTO;
import br.com.ralfdomingues.oficina.domain.usuario.entity.Usuario;
import br.com.ralfdomingues.oficina.exception.BusinessException;
import br.com.ralfdomingues.oficina.exception.NotFoundException;
import br.com.ralfdomingues.oficina.repository.usuario.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Camada de serviço responsável pelas regras de negócio
 * relacionadas à entidade {@link Usuario}.
 *
 * <p>Centraliza operações de cadastro, atualização,
 * listagem e desativação lógica de usuários,
 * garantindo integridade e segurança dos dados.</p>
 */
@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository repository,
                          PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Cria um novo usuário no sistema.
     *
     * <p>Aplica validação de unicidade de email e
     * realiza a criptografia da senha antes da persistência.</p>
     *
     * @throws BusinessException caso o email já esteja cadastrado
     */
    @Transactional
    public UsuarioResponseDTO criar(UsuarioCreateDTO dto) {

        if (repository.existsByEmail(dto.email())) {
            throw new BusinessException("Email já cadastrado.");
        }

        Usuario usuario = Usuario.builder()
                .nome(dto.nome())
                .email(dto.email())
                .senha(passwordEncoder.encode(dto.senha()))
                .perfil(dto.perfil())
                .ativo(true)
                .build();

        repository.save(usuario);
        return new UsuarioResponseDTO(usuario);
    }

    /**
     * Lista apenas usuários ativos de forma paginada.
     */
    public Page<UsuarioResponseDTO> listar(Pageable pageable) {
        return repository.findAllByAtivoTrue(pageable)
                .map(UsuarioResponseDTO::new);
    }

    /**
     * Recupera um usuário pelo identificador.
     *
     * @throws NotFoundException caso o usuário não exista
     */
    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorId(Long id) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado."));
        return new UsuarioResponseDTO(usuario);
    }

    /**
     * Atualiza parcialmente um usuário existente.
     *
     * <p>Aplica validações de unicidade de email e
     * recriptografa a senha quando informada.</p>
     *
     * @throws NotFoundException   caso o usuário não exista
     * @throws BusinessException  caso o email já esteja em uso
     */
    @Transactional
    public UsuarioResponseDTO atualizar(Long id, UsuarioUpdateDTO dto) {

        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado."));

        if (dto.nome() != null) {
            usuario.setNome(dto.nome());
        }

        if (dto.email() != null && !dto.email().equals(usuario.getEmail())) {

            if (repository.existsByEmail(dto.email())) {
                throw new BusinessException("Email já cadastrado.");
            }

            usuario.setEmail(dto.email());
        }

        if (dto.senha() != null && !dto.senha().isBlank()) {
            usuario.setSenha(passwordEncoder.encode(dto.senha()));
        }

        if (dto.perfil() != null) {
            usuario.setPerfil(dto.perfil());
        }

        if (dto.ativo() != null) {
            usuario.setAtivo(dto.ativo());
        }

        repository.save(usuario);
        return new UsuarioResponseDTO(usuario);
    }

    /**
     * Realiza a desativação lógica de um usuário.
     *
     * <p>O registro permanece no banco de dados,
     * sendo apenas marcado como inativo.</p>
     *
     * @throws NotFoundException caso o usuário não exista
     */
    @Transactional
    public void desativar(Long id) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado."));

        usuario.setAtivo(false);
        repository.save(usuario);
    }
}
