package br.edu.senac.recursos;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.dao.DataIntegrityViolationException;
import br.edu.senac.recursos.Modelos.*;

class BootstrapTest {
    @Test void logsNaoExpoemSenhasDosPayloads() {
        String senha="SenhaSintetica123!";
        assertThat(new Cadastro("Pessoa","pessoa@exemplo.test",senha).toString()).doesNotContain(senha);
        assertThat(new NovoUsuario("Pessoa","pessoa@exemplo.test",senha,Perfil.GESTOR).toString()).doesNotContain(senha);
    }
    @Test void exigeSenhaInicialEReutilizaAdministradorExistente() {
        Usuarios users=mock(Usuarios.class);
        when(users.listar()).thenReturn(List.of());
        assertThatThrownBy(()->new Bootstrap(users,"admin@exemplo.test","").run(new DefaultApplicationArguments()))
            .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(()->new Bootstrap(users,"admin@exemplo.test","x".repeat(73)).run(new DefaultApplicationArguments()))
            .isInstanceOf(IllegalStateException.class);
        new Bootstrap(users,"admin@exemplo.test","SenhaSintetica123!").run(new DefaultApplicationArguments());
        verify(users).criar("Administrador","admin@exemplo.test","SenhaSintetica123!",Perfil.ADMIN);
        when(users.listar()).thenReturn(List.of(new Usuario(1,"Admin","admin@exemplo.test",Perfil.ADMIN)));
        new Bootstrap(users,"admin@exemplo.test","").run(new DefaultApplicationArguments());
        verify(users,times(1)).criar(anyString(),anyString(),anyString(),any());
    }
    @Test void errosDeIntegridadeNaoExpoemSql() {
        var response=new Erros().integridade(new DataIntegrityViolationException("SQL segredo"));
        assertThat(response.getStatusCode().value()).isEqualTo(409);
        assertThat(response.getBody().getDetail()).doesNotContain("SQL","segredo");
    }
}
