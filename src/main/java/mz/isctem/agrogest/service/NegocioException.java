package mz.isctem.agrogest.service;

/** Erro de validacao ou de regra de negocio, com mensagem para mostrar ao utilizador. */
public class NegocioException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public NegocioException(String mensagem) {
        super(mensagem);
    }
}
