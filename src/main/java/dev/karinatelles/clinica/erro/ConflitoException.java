package dev.karinatelles.clinica.erro;

/** Regra violada porque o recurso já está ocupado (horário, CRM, CPF). Vira 409. */
public class ConflitoException extends RuntimeException {
    public ConflitoException(String mensagem) {
        super(mensagem);
    }
}
