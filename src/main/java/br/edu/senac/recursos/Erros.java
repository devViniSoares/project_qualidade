package br.edu.senac.recursos;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class Erros {
    @ExceptionHandler(Falha.class) ResponseEntity<ProblemDetail> dominio(Falha e) { return erro(e.status(),e.getMessage()); }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ProblemDetail> validacao(MethodArgumentNotValidException e) {
        String detalhe=e.getBindingResult().getFieldErrors().stream()
            .map(f -> f.getField()+": "+f.getDefaultMessage()).sorted().collect(java.util.stream.Collectors.joining("; "));
        return erro(HttpStatus.BAD_REQUEST,detalhe);
    }
    @ExceptionHandler({HttpMessageNotReadableException.class,MethodArgumentTypeMismatchException.class})
    ResponseEntity<ProblemDetail> formato(Exception e) { return erro(HttpStatus.BAD_REQUEST,"Dados inválidos. Confira campos, tipos e datas com fuso horário."); }
    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<ProblemDetail> integridade(DataIntegrityViolationException e) { return erro(HttpStatus.CONFLICT,"Operação viola a integridade dos dados."); }
    private ResponseEntity<ProblemDetail> erro(HttpStatus status,String detalhe) {
        return ResponseEntity.status(status).body(ProblemDetail.forStatusAndDetail(status,detalhe));
    }
}
