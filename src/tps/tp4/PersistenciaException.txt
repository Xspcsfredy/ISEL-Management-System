package tps.tp4;

public class PersistenciaException extends AcademicoException {

    public PersistenciaException(String mensagem) {
        super(mensagem);
    }

    public PersistenciaException(String mensagem, Exception causa) {
        super(mensagem);
        initCause(causa);
    }
}

