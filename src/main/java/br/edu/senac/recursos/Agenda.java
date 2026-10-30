package br.edu.senac.recursos;

import static br.edu.senac.recursos.Modelos.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Clock;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class Agenda {
    private final JdbcTemplate db;
    private final TransactionTemplate tx;
    private final Clock clock;
    private static final String RESERVAS = """
        SELECT r.*, c.nome AS recurso_nome, u.nome AS usuario_nome
        FROM reservas r JOIN recursos c ON c.id=r.recurso_id JOIN usuarios u ON u.id=r.usuario_id
        """;
    private static final RowMapper<Recurso> RECURSO = (rs,n) -> new Recurso(rs.getLong("id"),
        rs.getString("nome"), Tipo.valueOf(rs.getString("tipo")), rs.getString("localizacao"),
        rs.getInt("capacidade"),rs.getString("descricao"),rs.getBoolean("ativo"));
    public Agenda(JdbcTemplate db, TransactionTemplate tx, Clock clock) { this.db=db; this.tx=tx; this.clock=clock; }
    public List<Recurso> recursos(String busca, Tipo tipo) {
        String termo = busca == null ? "" : busca.trim().toLowerCase(java.util.Locale.ROOT);
        return db.query("SELECT * FROM recursos ORDER BY nome", RECURSO).stream()
            .filter(r -> tipo == null || r.tipo() == tipo)
            .filter(r -> (r.nome()+" "+r.localizacao()).toLowerCase(java.util.Locale.ROOT).contains(termo)).toList();
    }
    public Recurso recurso(long id) {
        return db.query("SELECT * FROM recursos WHERE id=?", RECURSO, id).stream().findFirst()
            .orElseThrow(() -> Falha.inexistente("Recurso não encontrado."));
    }
    public synchronized Recurso criar(DadosRecurso d) {
        var key = new GeneratedKeyHolder();
        db.update(con -> {
            var st=con.prepareStatement("INSERT INTO recursos(nome,tipo,localizacao,capacidade,descricao,ativo) VALUES (?,?,?,?,?,?)", new String[]{"id"});
            st.setString(1,d.nome().trim()); st.setString(2,d.tipo().name()); st.setString(3,d.localizacao().trim());
            st.setInt(4,d.capacidade()); st.setString(5,d.descricao()); st.setBoolean(6,d.ativo()); return st;
        },key);
        return recurso(key.getKey().longValue());
    }
    public synchronized Recurso atualizar(long id, DadosRecurso d) {
        recurso(id);
        db.update("UPDATE recursos SET nome=?,tipo=?,localizacao=?,capacidade=?,descricao=?,ativo=? WHERE id=?",
            d.nome().trim(),d.tipo().name(),d.localizacao().trim(),d.capacidade(),d.descricao(),d.ativo(),id);
        return recurso(id);
    }
    public synchronized void excluir(long id) {
        recurso(id);
        if (db.queryForObject("SELECT COUNT(*) FROM reservas WHERE recurso_id=?",Long.class,id)>0)
            throw Falha.conflito("Recurso possui histórico de reservas. Desative-o para preservar o histórico.");
        db.update("DELETE FROM recursos WHERE id=?",id);
    }
    static void validarIntervalo(OffsetDateTime inicio, OffsetDateTime fim) {
        if (inicio == null || fim == null || !fim.isAfter(inicio))
            throw Falha.invalido("O fim deve ser posterior ao início.");
        if (Duration.between(inicio, fim).compareTo(Duration.ofHours(12)) > 0)
            throw Falha.invalido("A reserva pode durar no máximo 12 horas.");
    }
    public boolean disponivel(long id, OffsetDateTime inicio, OffsetDateTime fim) {
        validarIntervalo(inicio,fim);
        return recurso(id).ativo() && conflitos(id,inicio,fim)==0;
    }
    private long conflitos(long recursoId, OffsetDateTime inicio, OffsetDateTime fim) {
        return db.queryForObject("SELECT COUNT(*) FROM reservas WHERE recurso_id=? AND status='ATIVA' AND inicio < ? AND fim > ?",
            Long.class,recursoId,fim,inicio);
    }
    // O monitor cobre a transação inteira, incluindo o commit, em uma única instância.
    public synchronized Reserva reservar(Usuario usuario, NovaReserva d) {
        validarIntervalo(d.inicio(),d.fim());
        if (!d.inicio().isAfter(OffsetDateTime.now(clock))) throw Falha.invalido("Escolha um início no futuro.");
        return tx.execute(status -> {
            if (!recurso(d.recursoId()).ativo()) throw Falha.conflito("Recurso indisponível para reservas.");
            if (conflitos(d.recursoId(),d.inicio(),d.fim())>0) throw Falha.conflito("O recurso já está reservado neste horário.");
            if (db.queryForObject("SELECT COUNT(*) FROM reservas WHERE usuario_id=? AND status='ATIVA' AND inicio < ? AND fim > ?",
                Long.class,usuario.id(),d.fim(),d.inicio())>0) throw Falha.conflito("Você já possui uma reserva neste horário.");
            var key = new GeneratedKeyHolder();
            db.update(con -> {
                var st=con.prepareStatement("INSERT INTO reservas(recurso_id,usuario_id,inicio,fim,finalidade,status) VALUES (?,?,?,?,?,'ATIVA')",new String[]{"id"});
                st.setLong(1,d.recursoId()); st.setLong(2,usuario.id()); st.setObject(3,d.inicio());
                st.setObject(4,d.fim()); st.setString(5,d.finalidade().trim()); return st;
            },key);
            return reserva(key.getKey().longValue());
        });
    }
    private static Reserva mapear(ResultSet rs, int n) throws SQLException {
        return new Reserva(rs.getLong("id"),rs.getLong("recurso_id"),rs.getString("recurso_nome"),
            rs.getLong("usuario_id"),rs.getString("usuario_nome"),rs.getObject("inicio",OffsetDateTime.class),
            rs.getObject("fim",OffsetDateTime.class),rs.getString("finalidade"),rs.getString("status"));
    }
    public Reserva reserva(long id) {
        return db.query(RESERVAS+" WHERE r.id=?",Agenda::mapear,id).stream().findFirst()
            .orElseThrow(() -> Falha.inexistente("Reserva não encontrada."));
    }
    public List<Reserva> reservas(Usuario usuario, boolean todas) {
        if (todas && usuario.perfil()==Perfil.USUARIO) throw new Falha(HttpStatus.FORBIDDEN,"Acesso restrito à gestão.");
        return todas ? db.query(RESERVAS+" ORDER BY r.inicio DESC",Agenda::mapear)
            : db.query(RESERVAS+" WHERE r.usuario_id=? ORDER BY r.inicio DESC",Agenda::mapear,usuario.id());
    }
    public synchronized Reserva cancelar(long id, Usuario usuario) {
        Reserva r=reserva(id);
        if (r.usuarioId()!=usuario.id() && usuario.perfil()==Perfil.USUARIO)
            throw new Falha(HttpStatus.FORBIDDEN,"Você só pode cancelar suas próprias reservas.");
        db.update("UPDATE reservas SET status='CANCELADA' WHERE id=?",id);
        return reserva(id);
    }
}
