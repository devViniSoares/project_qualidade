package br.edu.senac.recursos;

import jakarta.validation.constraints.*;
import java.time.OffsetDateTime;

public final class Modelos {
    private Modelos() {}
    public enum Perfil { ADMIN, GESTOR, USUARIO }
    public enum Tipo { SALA, EQUIPAMENTO }
    public record Cadastro(@NotBlank @Size(max=100) String nome,
        @NotBlank @Email @Size(max=160) String email,
        @NotBlank @Size(min=12, max=72) String senha) {
        @Override public String toString() { return "Cadastro[credenciais protegidas]"; }
    }
    public record NovoUsuario(@NotBlank @Size(max=100) String nome,
        @NotBlank @Email @Size(max=160) String email,
        @NotBlank @Size(min=12, max=72) String senha, @NotNull Perfil perfil) {
        @Override public String toString() { return "NovoUsuario[credenciais protegidas]"; }
    }
    public record Usuario(long id, String nome, String email, Perfil perfil) {}
    public record DadosRecurso(@NotBlank @Size(max=100) String nome, @NotNull Tipo tipo,
        @NotBlank @Size(max=160) String localizacao, @Min(1) @Max(10000) int capacidade,
        @NotNull @Size(max=500) String descricao, boolean ativo) {}
    public record Recurso(long id, String nome, Tipo tipo, String localizacao,
        int capacidade, String descricao, boolean ativo) {}
    public record NovaReserva(@Positive long recursoId, @NotNull OffsetDateTime inicio,
        @NotNull OffsetDateTime fim, @NotBlank @Size(max=300) String finalidade) {}
    public record Reserva(long id, long recursoId, String recursoNome, long usuarioId,
        String usuarioNome, OffsetDateTime inicio, OffsetDateTime fim, String finalidade, String status) {}
}
