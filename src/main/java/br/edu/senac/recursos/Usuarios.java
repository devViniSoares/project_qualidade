package br.edu.senac.recursos;

import static br.edu.senac.recursos.Modelos.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class Usuarios implements UserDetailsService {
    private final JdbcTemplate db;
    private final PasswordEncoder encoder;
    public Usuarios(JdbcTemplate db, PasswordEncoder encoder) { this.db = db; this.encoder = encoder; }
    static String normalizar(String email) { return email.trim().toLowerCase(Locale.ROOT); }
    public synchronized Usuario criar(String nome, String email, String senha, Perfil perfil) {
        if (senha.getBytes(StandardCharsets.UTF_8).length > 72) throw Falha.invalido("Senha deve ter até 72 bytes UTF-8.");
        String chave = normalizar(email);
        if (db.queryForObject("SELECT COUNT(*) FROM usuarios WHERE email = ?", Long.class, chave) > 0)
            throw Falha.conflito("E-mail já cadastrado.");
        db.update("INSERT INTO usuarios(nome,email,senha,perfil) VALUES (?,?,?,?)",
            nome.trim(), chave, encoder.encode(senha), perfil.name());
        return buscar(chave);
    }
    public Usuario buscar(String email) {
        return db.query("SELECT id,nome,email,perfil FROM usuarios WHERE email = ?", (rs, n) ->
            new Usuario(rs.getLong("id"), rs.getString("nome"), rs.getString("email"),
                Perfil.valueOf(rs.getString("perfil"))), normalizar(email))
            .stream().findFirst().orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado."));
    }
    public List<Usuario> listar() {
        return db.query("SELECT id,nome,email,perfil FROM usuarios ORDER BY nome", (rs,n) ->
            new Usuario(rs.getLong("id"),rs.getString("nome"),rs.getString("email"),Perfil.valueOf(rs.getString("perfil"))));
    }
    @Override public UserDetails loadUserByUsername(String email) {
        return db.query("SELECT email,senha,perfil FROM usuarios WHERE email = ?", (rs,n) ->
            User.withUsername(rs.getString("email")).password(rs.getString("senha"))
                .roles(rs.getString("perfil")).build(), normalizar(email))
            .stream().findFirst().orElseThrow(() -> new UsernameNotFoundException("Credenciais inválidas."));
    }
}
