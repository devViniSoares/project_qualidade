package br.edu.senac.recursos;

import static br.edu.senac.recursos.Modelos.*;
import jakarta.validation.Valid;
import java.security.Principal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class Api {
    private final Agenda agenda;
    private final Usuarios usuarios;
    public Api(Agenda agenda, Usuarios usuarios) { this.agenda=agenda; this.usuarios=usuarios; }
    @GetMapping("/saude") public Map<String,String> saude() { return Map.of("status","UP"); }
    @GetMapping("/csrf") public Map<String,String> csrf(CsrfToken token) {
        return Map.of("headerName",token.getHeaderName(),"token",token.getToken());
    }
    @PostMapping("/cadastro") @ResponseStatus(HttpStatus.CREATED)
    public Usuario cadastro(@Valid @RequestBody Cadastro d) { return usuarios.criar(d.nome(),d.email(),d.senha(),Perfil.USUARIO); }
    @GetMapping("/me") public Usuario me(Principal p) { return usuarios.buscar(p.getName()); }
    @GetMapping("/usuarios") @PreAuthorize("hasRole('ADMIN')")
    public List<Usuario> usuarios() { return usuarios.listar(); }
    @PostMapping("/usuarios") @PreAuthorize("hasRole('ADMIN')") @ResponseStatus(HttpStatus.CREATED)
    public Usuario novoUsuario(@Valid @RequestBody NovoUsuario d) { return usuarios.criar(d.nome(),d.email(),d.senha(),d.perfil()); }
    @GetMapping("/recursos") public List<Recurso> recursos(@RequestParam(required=false) String busca,
        @RequestParam(required=false) Tipo tipo) { return agenda.recursos(busca,tipo); }
    @GetMapping("/recursos/{id}") public Recurso recurso(@PathVariable long id) { return agenda.recurso(id); }
    @PostMapping("/recursos") @PreAuthorize("hasAnyRole('ADMIN','GESTOR')") @ResponseStatus(HttpStatus.CREATED)
    public Recurso criar(@Valid @RequestBody DadosRecurso d) { return agenda.criar(d); }
    @PutMapping("/recursos/{id}") @PreAuthorize("hasAnyRole('ADMIN','GESTOR')")
    public Recurso atualizar(@PathVariable long id,@Valid @RequestBody DadosRecurso d) { return agenda.atualizar(id,d); }
    @DeleteMapping("/recursos/{id}") @PreAuthorize("hasAnyRole('ADMIN','GESTOR')") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable long id) { agenda.excluir(id); }
    @GetMapping("/recursos/{id}/disponibilidade")
    public Map<String,Boolean> disponivel(@PathVariable long id,@RequestParam OffsetDateTime inicio,@RequestParam OffsetDateTime fim) {
        return Map.of("disponivel",agenda.disponivel(id,inicio,fim));
    }
    @PostMapping("/reservas") @ResponseStatus(HttpStatus.CREATED)
    public Reserva reservar(Principal p,@Valid @RequestBody NovaReserva d) { return agenda.reservar(me(p),d); }
    @GetMapping("/reservas") public List<Reserva> reservas(Principal p,@RequestParam(defaultValue="false") boolean todas) {
        return agenda.reservas(me(p),todas);
    }
    @PostMapping("/reservas/{id}/cancelamento") public Reserva cancelar(Principal p,@PathVariable long id) {
        return agenda.cancelar(id,me(p));
    }
}
