package br.edu.senac.recursos;

import br.edu.senac.recursos.Modelos.Perfil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class Bootstrap implements ApplicationRunner {
    private final Usuarios usuarios;
    private final String email;
    private final String senha;
    public Bootstrap(Usuarios usuarios, @Value("${app.admin.email}") String email,
                     @Value("${app.admin.password}") String senha) {
        this.usuarios = usuarios; this.email = email; this.senha = senha;
    }
    @Override public void run(ApplicationArguments args) {
        if (usuarios.listar().stream().anyMatch(u -> u.perfil() == Perfil.ADMIN)) return;
        if (senha.length() < 12 || senha.length() > 72)
            throw new IllegalStateException("Defina ADMIN_PASSWORD com 12 a 72 caracteres para criar o administrador inicial.");
        usuarios.criar("Administrador", email, senha, Perfil.ADMIN);
    }
}
