package br.edu.senac.recursos;

import org.springframework.http.HttpStatus;

public class Falha extends RuntimeException {
    private final HttpStatus status;
    public Falha(HttpStatus status, String mensagem) { super(mensagem); this.status = status; }
    public HttpStatus status() { return status; }
    public static Falha invalido(String mensagem) { return new Falha(HttpStatus.BAD_REQUEST, mensagem); }
    public static Falha conflito(String mensagem) { return new Falha(HttpStatus.CONFLICT, mensagem); }
    public static Falha inexistente(String mensagem) { return new Falha(HttpStatus.NOT_FOUND, mensagem); }
}
