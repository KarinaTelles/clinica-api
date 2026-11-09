package dev.karinatelles.clinica.erro;

/** Pedido bem formado que a clínica não aceita (fora do horário, sem antecedência...). Vira 422. */
public class RegraNegocioException extends RuntimeException {
    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
