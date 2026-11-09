package dev.karinatelles.clinica.erro;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/** Todas as respostas de erro saem como ProblemDetail (RFC 9457). */
@RestControllerAdvice
public class TratadorDeErros extends ResponseEntityExceptionHandler {

    record CampoInvalido(String campo, String mensagem) {
    }

    @ExceptionHandler(NaoEncontradoException.class)
    ProblemDetail naoEncontrado(NaoEncontradoException e) {
        return problema(HttpStatus.NOT_FOUND, "Recurso não encontrado", e.getMessage());
    }

    @ExceptionHandler(ConflitoException.class)
    ProblemDetail conflito(ConflitoException e) {
        return problema(HttpStatus.CONFLICT, "Conflito de agenda ou cadastro", e.getMessage());
    }

    @ExceptionHandler(RegraNegocioException.class)
    ProblemDetail regra(RegraNegocioException e) {
        return problema(HttpStatus.UNPROCESSABLE_CONTENT, "Regra de agendamento violada", e.getMessage());
    }

    /** Última barreira: o índice único do banco pegou algo que passou pelas validações (ex.: requisições simultâneas). */
    @ExceptionHandler(DataIntegrityViolationException.class)
    ProblemDetail integridade(DataIntegrityViolationException e) {
        return problema(HttpStatus.CONFLICT, "Conflito de agenda ou cadastro",
                "O registro conflita com outro já existente (horário ou documento em uso)");
    }

    @ExceptionHandler(AuthenticationException.class)
    ProblemDetail autenticacao(AuthenticationException e) {
        return problema(HttpStatus.UNAUTHORIZED, "Não autenticado", e.getMessage());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e, HttpHeaders headers,
                                                                  HttpStatusCode status, WebRequest request) {
        var problema = problema(HttpStatus.BAD_REQUEST, "Dados inválidos", "Um ou mais campos não passaram na validação");
        List<CampoInvalido> campos = e.getBindingResult().getFieldErrors().stream()
                .map(f -> new CampoInvalido(f.getField(), f.getDefaultMessage()))
                .toList();
        problema.setProperty("campos", campos);
        return ResponseEntity.badRequest().body(problema);
    }

    private static ProblemDetail problema(HttpStatus status, String titulo, String detalhe) {
        var problema = ProblemDetail.forStatusAndDetail(status, detalhe);
        problema.setTitle(titulo);
        return problema;
    }
}
