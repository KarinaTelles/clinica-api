package dev.karinatelles.clinica.consulta;

import java.time.LocalDateTime;

import dev.karinatelles.clinica.medico.Medico;
import dev.karinatelles.clinica.paciente.Paciente;
import jakarta.persistence.*;

@Entity
@Table(name = "consultas")
public class Consulta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Medico medico;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Paciente paciente;

    private LocalDateTime dataHora;

    @Enumerated(EnumType.STRING)
    private MotivoCancelamento motivoCancelamento;

    private LocalDateTime canceladaEm;

    @Column(insertable = false, updatable = false)
    private LocalDateTime criadaEm;

    protected Consulta() {
    }

    public Consulta(Medico medico, Paciente paciente, LocalDateTime dataHora) {
        this.medico = medico;
        this.paciente = paciente;
        this.dataHora = dataHora;
    }

    public void cancelar(MotivoCancelamento motivo, LocalDateTime agora) {
        this.motivoCancelamento = motivo;
        this.canceladaEm = agora;
    }

    public boolean isCancelada() {
        return motivoCancelamento != null;
    }

    public Long getId() { return id; }
    public Medico getMedico() { return medico; }
    public Paciente getPaciente() { return paciente; }
    public LocalDateTime getDataHora() { return dataHora; }
    public MotivoCancelamento getMotivoCancelamento() { return motivoCancelamento; }
    public LocalDateTime getCanceladaEm() { return canceladaEm; }
}
