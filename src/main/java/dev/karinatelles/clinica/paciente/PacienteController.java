package dev.karinatelles.clinica.paciente;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/pacientes")
public class PacienteController {

    private final PacienteService service;

    public PacienteController(PacienteService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<DadosPaciente> cadastrar(@RequestBody @Valid DadosCadastroPaciente dados, UriComponentsBuilder uri) {
        var paciente = service.cadastrar(dados);
        var location = uri.path("/pacientes/{id}").buildAndExpand(paciente.getId()).toUri();
        return ResponseEntity.created(location).body(new DadosPaciente(paciente));
    }

    @GetMapping
    public Page<DadosListagemPaciente> listar(@PageableDefault(size = 10, sort = "nome") Pageable paginacao) {
        return service.listarAtivos(paginacao).map(DadosListagemPaciente::new);
    }

    @GetMapping("/{id}")
    public DadosPaciente detalhar(@PathVariable Long id) {
        return new DadosPaciente(service.buscar(id));
    }

    @PutMapping("/{id}")
    public DadosPaciente atualizar(@PathVariable Long id, @RequestBody @Valid DadosAtualizacaoPaciente dados) {
        return new DadosPaciente(service.atualizar(id, dados));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> inativar(@PathVariable Long id) {
        service.inativar(id);
        return ResponseEntity.noContent().build();
    }
}
