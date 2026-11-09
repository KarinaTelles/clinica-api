package dev.karinatelles.clinica.medico;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/medicos")
public class MedicoController {

    private final MedicoService service;

    public MedicoController(MedicoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<DadosMedico> cadastrar(@RequestBody @Valid DadosCadastroMedico dados, UriComponentsBuilder uri) {
        var medico = service.cadastrar(dados);
        var location = uri.path("/medicos/{id}").buildAndExpand(medico.getId()).toUri();
        return ResponseEntity.created(location).body(new DadosMedico(medico));
    }

    @GetMapping
    public Page<DadosListagemMedico> listar(@PageableDefault(size = 10, sort = "nome") Pageable paginacao) {
        return service.listarAtivos(paginacao).map(DadosListagemMedico::new);
    }

    @GetMapping("/{id}")
    public DadosMedico detalhar(@PathVariable Long id) {
        return new DadosMedico(service.buscar(id));
    }

    @PutMapping("/{id}")
    public DadosMedico atualizar(@PathVariable Long id, @RequestBody @Valid DadosAtualizacaoMedico dados) {
        return new DadosMedico(service.atualizar(id, dados));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> inativar(@PathVariable Long id) {
        service.inativar(id);
        return ResponseEntity.noContent().build();
    }
}
