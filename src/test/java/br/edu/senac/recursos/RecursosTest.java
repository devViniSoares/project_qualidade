package br.edu.senac.recursos;

import static br.edu.senac.recursos.Modelos.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Stream;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RecursosTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Autowired Agenda agenda;
    @Autowired Usuarios usuarios;
    @Autowired JdbcTemplate db;
    Usuario ana, bia, gestor, admin;
    Recurso sala;
    final OffsetDateTime inicio=OffsetDateTime.now().plusDays(3).withNano(0);
    @BeforeAll void pessoas() {
        ana=usuarios.criar("Ana Teste","ana@exemplo.test","SenhaSintetica123!",Perfil.USUARIO);
        bia=usuarios.criar("Bia Teste","bia@exemplo.test","SenhaSintetica123!",Perfil.USUARIO);
        gestor=usuarios.criar("Gestor Teste","gestor@exemplo.test","SenhaSintetica123!",Perfil.GESTOR);
        admin=usuarios.buscar("admin@exemplo.test");
    }
    @BeforeEach void preparar() {
        db.update("DELETE FROM reservas"); db.update("DELETE FROM recursos");
        sala=agenda.criar(dados("Sala de reunião",true));
    }
    DadosRecurso dados(String nome,boolean ativo) {return new DadosRecurso(nome,Tipo.SALA,"Bloco A",12,"Projetor e quadro",ativo);}
    NovaReserva reserva(long recursoId,OffsetDateTime de,OffsetDateTime ate) {return new NovaReserva(recursoId,de,ate,"Atividade acadêmica");}
    MockHttpServletRequestBuilder como(MockHttpServletRequestBuilder req,Usuario u) {return req.with(user(u.email()).roles(u.perfil().name()));}
    String body(Object value) throws Exception {return json.writeValueAsString(value);}

    // RF01, RNF01: autenticação e isolamento por perfil.
    @Test void acessoPublicoESeguranca() throws Exception {
        mvc.perform(get("/api/saude")).andExpect(status().isOk()).andExpect(jsonPath("$.status").value("UP"));
        mvc.perform(get("/api/csrf")).andExpect(status().isOk()).andExpect(jsonPath("$.token").isNotEmpty());
        mvc.perform(get("/")).andExpect(status().isOk()).andExpect(header().exists("Content-Security-Policy"));
        mvc.perform(get("/api/recursos")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/me").with(httpBasic("admin@exemplo.test","SenhaSintetica123!")))
            .andExpect(status().isOk()).andExpect(jsonPath("$.perfil").value("ADMIN")).andExpect(jsonPath("$.senha").doesNotExist());
        mvc.perform(get("/api/me").with(httpBasic("admin@exemplo.test","incorreta"))).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/me").with(httpBasic("inexistente@exemplo.test","incorreta"))).andExpect(status().isUnauthorized());
    }
    @Test void csrfObrigatorio() throws Exception {
        mvc.perform(como(post("/api/recursos"),gestor).contentType(MediaType.APPLICATION_JSON).content(body(dados("Outra",true))))
            .andExpect(status().isForbidden());
    }
    // RF02: cadastro público fixa o perfil USUARIO e normaliza e-mail.
    @Test void cadastroNaoPermiteEscaladaNemDuplicata() throws Exception {
        String email="Novo@exemplo.test";
        mvc.perform(post("/api/cadastro").with(csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(body(new Cadastro("Novo Usuário",email,"SenhaSintetica123!"))))
            .andExpect(status().isCreated()).andExpect(jsonPath("$.email").value("novo@exemplo.test"))
            .andExpect(jsonPath("$.perfil").value("USUARIO")).andExpect(jsonPath("$.senha").doesNotExist());
        mvc.perform(post("/api/cadastro").with(csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(body(new Cadastro("Duplicado",email,"SenhaSintetica123!"))))
            .andExpect(status().isConflict());
        mvc.perform(post("/api/cadastro").with(csrf()).contentType(MediaType.APPLICATION_JSON)
            .content("{\"nome\":\"Intruso\",\"email\":\"x@exemplo.test\",\"senha\":\"SenhaSintetica123!\",\"perfil\":\"ADMIN\"}"))
            .andExpect(status().isBadRequest());
    }
    @Test void validacaoCadastro() throws Exception {
        mvc.perform(post("/api/cadastro").with(csrf()).contentType(MediaType.APPLICATION_JSON)
            .content("{\"nome\":\"\",\"email\":\"invalido\",\"senha\":\"curta\"}"))
            .andExpect(status().isBadRequest()).andExpect(jsonPath("$.detail").exists());
        assertThatThrownBy(()->usuarios.criar("Nome","bytes@exemplo.test","á".repeat(40),Perfil.USUARIO))
            .isInstanceOf(Falha.class).hasMessageContaining("72 bytes");
    }
    @Test void somenteAdminGerenciaUsuarios() throws Exception {
        var d=new NovoUsuario("Gestor Novo","gestor-novo@exemplo.test","SenhaSintetica123!",Perfil.GESTOR);
        mvc.perform(como(post("/api/usuarios"),admin).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(body(d)))
            .andExpect(status().isCreated()).andExpect(jsonPath("$.perfil").value("GESTOR"));
        mvc.perform(como(get("/api/usuarios"),admin)).andExpect(status().isOk()).andExpect(jsonPath("$[0].senha").doesNotExist());
        mvc.perform(como(get("/api/usuarios"),gestor)).andExpect(status().isForbidden());
        mvc.perform(como(post("/api/usuarios"),ana).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(body(d)))
            .andExpect(status().isForbidden());
    }
    // RF03-RF04: CRUD de recursos, busca e filtros.
    @Test void gestorGerenciaRecursos() throws Exception {
        var result=mvc.perform(como(post("/api/recursos"),gestor).with(csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(body(new DadosRecurso("Notebook",Tipo.EQUIPAMENTO,"Laboratório",1,"Portátil",true))))
            .andExpect(status().isCreated()).andReturn();
        long id=json.readTree(result.getResponse().getContentAsString()).get("id").asLong();
        mvc.perform(como(get("/api/recursos").param("tipo","EQUIPAMENTO").param("busca","note"),ana))
            .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
        mvc.perform(como(get("/api/recursos").param("busca","NÃO EXISTE"),ana))
            .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(0));
        mvc.perform(como(put("/api/recursos/"+id),gestor).with(csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(body(dados("Sala B",false)))).andExpect(status().isOk()).andExpect(jsonPath("$.ativo").value(false));
        mvc.perform(como(get("/api/recursos/"+id),ana)).andExpect(status().isOk()).andExpect(jsonPath("$.nome").value("Sala B"));
        mvc.perform(como(delete("/api/recursos/"+id),gestor).with(csrf())).andExpect(status().isNoContent());
        mvc.perform(como(get("/api/recursos/"+id),ana)).andExpect(status().isNotFound());
    }
    @ParameterizedTest @ValueSource(strings={"POST","PUT","DELETE"})
    void usuarioNaoAlteraRecursos(String method) throws Exception {
        var req=switch(method){case "POST"->post("/api/recursos");case "PUT"->put("/api/recursos/"+sala.id());default->delete("/api/recursos/"+sala.id());};
        mvc.perform(como(req,ana).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(body(dados("Inválida",true))))
            .andExpect(status().isForbidden());
    }
    @Test void validaRecursoEParametros() throws Exception {
        mvc.perform(como(post("/api/recursos"),gestor).with(csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(body(new DadosRecurso("",null,"",0,null,true)))).andExpect(status().isBadRequest());
        mvc.perform(como(get("/api/recursos").param("tipo","INVALIDO"),ana)).andExpect(status().isBadRequest());
        mvc.perform(como(post("/api/reservas"),ana).with(csrf()).contentType(MediaType.APPLICATION_JSON).content("{"))
            .andExpect(status().isBadRequest());
        mvc.perform(como(delete("/api/recursos/999999"),gestor).with(csrf())).andExpect(status().isNotFound());
        mvc.perform(como(put("/api/recursos/999999"),gestor).with(csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(body(dados("Não existe",true)))).andExpect(status().isNotFound());
    }
    // RF05-RF06, RN01: intervalos semiabertos e reserva atômica.
    @Test void disponibilidadeEReservaComCancelamento() throws Exception {
        mvc.perform(como(get("/api/recursos/"+sala.id()+"/disponibilidade").param("inicio",inicio.toString())
            .param("fim",inicio.plusHours(1).toString()),ana)).andExpect(status().isOk()).andExpect(jsonPath("$.disponivel").value(true));
        var result=mvc.perform(como(post("/api/reservas"),ana).with(csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(body(reserva(sala.id(),inicio,inicio.plusHours(1))))).andExpect(status().isCreated()).andReturn();
        long id=json.readTree(result.getResponse().getContentAsString()).get("id").asLong();
        assertThat(agenda.disponivel(sala.id(),inicio,inicio.plusMinutes(30))).isFalse();
        assertThat(agenda.disponivel(sala.id(),inicio.plusHours(1),inicio.plusHours(2))).isTrue();
        mvc.perform(como(post("/api/reservas/"+id+"/cancelamento"),ana).with(csrf())).andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("CANCELADA"));
        mvc.perform(como(post("/api/reservas/"+id+"/cancelamento"),ana).with(csrf())).andExpect(status().isOk());
        assertThat(agenda.disponivel(sala.id(),inicio,inicio.plusHours(1))).isTrue();
    }
    @Test void bloqueiaSobreposicaoEConservaHistorico() throws Exception {
        agenda.reservar(ana,reserva(sala.id(),inicio,inicio.plusHours(2)));
        mvc.perform(como(post("/api/reservas"),bia).with(csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(body(reserva(sala.id(),inicio.plusHours(1),inicio.plusHours(3)))))
            .andExpect(status().isConflict()).andExpect(jsonPath("$.detail").value("O recurso já está reservado neste horário."));
        mvc.perform(como(delete("/api/recursos/"+sala.id()),gestor).with(csrf())).andExpect(status().isConflict());
        assertThat(agenda.reservas(ana,false)).hasSize(1);
    }
    @Test void naoReservaRecursoInativoOuInexistente() throws Exception {
        agenda.atualizar(sala.id(),dados("Indisponível",false));
        assertThat(agenda.disponivel(sala.id(),inicio,inicio.plusHours(1))).isFalse();
        mvc.perform(como(post("/api/reservas"),ana).with(csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(body(reserva(sala.id(),inicio,inicio.plusHours(1))))).andExpect(status().isConflict());
        mvc.perform(como(post("/api/reservas"),ana).with(csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(body(reserva(999999,inicio,inicio.plusHours(1))))).andExpect(status().isNotFound());
    }
    @ParameterizedTest @ValueSource(ints={0,-1,13})
    void validaDuracaoDaReserva(int horas) throws Exception {
        mvc.perform(como(post("/api/reservas"),ana).with(csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(body(reserva(sala.id(),inicio,inicio.plusHours(horas))))).andExpect(status().isBadRequest());
    }
    @Test void naoReservaNoPassadoNemSemDatas() throws Exception {
        mvc.perform(como(post("/api/reservas"),ana).with(csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(body(reserva(sala.id(),inicio.minusDays(10),inicio.minusDays(10).plusHours(1)))))
            .andExpect(status().isBadRequest());
        mvc.perform(como(post("/api/reservas"),ana).with(csrf()).contentType(MediaType.APPLICATION_JSON)
            .content("{\"recursoId\":0,\"finalidade\":\"\"}"))
            .andExpect(status().isBadRequest());
        assertThatThrownBy(()->Agenda.validarIntervalo(null,inicio)).isInstanceOf(Falha.class);
        assertThatThrownBy(()->Agenda.validarIntervalo(inicio,null)).isInstanceOf(Falha.class);
    }
    // RN02: o próprio usuário não pode ocupar dois recursos ao mesmo tempo.
    @Test void bloqueiaConflitoDoUsuarioEPermiteIntervaloAdjacente() {
        Recurso outro=agenda.criar(dados("Outra sala",true));
        agenda.reservar(ana,reserva(sala.id(),inicio,inicio.plusHours(1)));
        assertThatThrownBy(()->agenda.reservar(ana,reserva(outro.id(),inicio,inicio.plusHours(1))))
            .isInstanceOf(Falha.class).hasMessageContaining("Você já possui");
        assertThat(agenda.reservar(ana,reserva(outro.id(),inicio.plusHours(1),inicio.plusHours(2))).status()).isEqualTo("ATIVA");
    }
    // RF07-RF08: isolamento e intervenção de gestão.
    @Test void privacidadeEPermissoesDeCancelamento() throws Exception {
        Reserva r=agenda.reservar(ana,reserva(sala.id(),inicio,inicio.plusHours(1)));
        mvc.perform(como(get("/api/reservas"),bia)).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(0));
        mvc.perform(como(get("/api/reservas").param("todas","true"),bia)).andExpect(status().isForbidden());
        mvc.perform(como(get("/api/reservas").param("todas","true"),gestor)).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
        mvc.perform(como(post("/api/reservas/"+r.id()+"/cancelamento"),bia).with(csrf())).andExpect(status().isForbidden());
        mvc.perform(como(post("/api/reservas/"+r.id()+"/cancelamento"),gestor).with(csrf())).andExpect(status().isOk());
        mvc.perform(como(post("/api/reservas/999999/cancelamento"),admin).with(csrf())).andExpect(status().isNotFound());
    }
    // RNF02: todas as chamadas começam juntas e somente uma pode vencer.
    @Test void concorrenciaNaoDuplicaReserva() throws Exception {
        int n=12;
        CountDownLatch pronto=new CountDownLatch(n), largada=new CountDownLatch(1);
        try(var pool=Executors.newFixedThreadPool(n)) {
            List<Future<Boolean>> futures=new ArrayList<>();
            for(int i=0;i<n;i++) futures.add(pool.submit(()->{
                pronto.countDown(); if(!largada.await(5,TimeUnit.SECONDS))throw new IllegalStateException("Sem largada");
                try {agenda.reservar(ana,reserva(sala.id(),inicio,inicio.plusHours(1)));return true;}
                catch(Falha e){assertThat(e.status().value()).isEqualTo(409);return false;}
            }));
            assertThat(pronto.await(5,TimeUnit.SECONDS)).isTrue();largada.countDown();
            int sucessos=0;for(var f:futures)if(f.get(10,TimeUnit.SECONDS))sucessos++;
            assertThat(sucessos).isEqualTo(1);
        }
        assertThat(db.queryForObject("SELECT COUNT(*) FROM reservas WHERE status='ATIVA'",Long.class)).isEqualTo(1);
    }
    @Test void senhasPersistidasComHash() {
        String hash=db.queryForObject("SELECT senha FROM usuarios WHERE id=?",String.class,ana.id());
        assertThat(hash).startsWith("$2a$").doesNotContain("SenhaSintetica");
    }
}
