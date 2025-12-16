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

@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository repository,
                          PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

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

    public Page<UsuarioResponseDTO> listar(Pageable pageable) {
        return repository.findAllByAtivoTrue(pageable)
                .map(UsuarioResponseDTO::new);
    }


    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorId(Long id) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado."));
        return new UsuarioResponseDTO(usuario);
    }

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

    @Transactional
    public void desativar(Long id) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado."));

        usuario.setAtivo(false);
        repository.save(usuario);
    }
}
