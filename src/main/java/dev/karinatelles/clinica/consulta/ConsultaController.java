package dev.karinatelles.clinica.consulta;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/consultas")
public class ConsultaController {

    private final ConsultaService service;

    public ConsultaController(ConsultaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<DadosConsulta> agendar(@RequestBody @Valid DadosAgendamento dados, UriComponentsBuilder uri) {
        var consulta = service.agendar(dados);
        var location = uri.path("/consultas/{id}").buildAndExpand(consulta.id()).toUri();
        return ResponseEntity.created(location).body(consulta);
    }

    @GetMapping
    public Page<DadosConsulta> listar(@RequestParam(required = false) Long medicoId,
                                      @PageableDefault(size = 10, sort = "dataHora", direction = Sort.Direction.ASC) Pageable paginacao) {
        return service.listarAtivas(medicoId, paginacao);
    }

    @GetMapping("/{id}")
    public DadosConsulta detalhar(@PathVariable Long id) {
        return service.detalhar(id);
    }

    @PostMapping("/{id}/cancelamento")
    public DadosConsulta cancelar(@PathVariable Long id, @RequestBody @Valid DadosCancelamento dados) {
        return service.cancelar(id, dados);
    }
}
