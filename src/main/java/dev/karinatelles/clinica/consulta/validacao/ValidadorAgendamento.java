package dev.karinatelles.clinica.consulta.validacao;

/**
 * Uma regra de agendamento. Cada implementação lança exceção quando o pedido
 * não pode ser aceito; o serviço roda todas antes de gravar.
 */
public interface ValidadorAgendamento {

    void validar(PedidoAgendamento pedido);
}
