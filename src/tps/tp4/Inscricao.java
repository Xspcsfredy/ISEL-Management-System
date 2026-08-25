package tps.tp4;

import java.time.LocalDate; // Serve para pegar a data real

public class Inscricao {

    // Atributos
    private final int numeroAluno;
    private final String codigoUc;
    private final String idTurma;
    private final AnoLetivo anoLetivo;
    private final Semestre semestre;
    private final LocalDate data;

    // Construtor da classe
    public Inscricao(int numeroAluno, String codigoUc, String idTurma, AnoLetivo anoLetivo, Semestre semestre, LocalDate data) throws ValidacaoException {
        if (codigoUc == null || codigoUc.trim().isEmpty()) {
            throw new ValidacaoException("Codigo de UC invalido.");
        }
        if (idTurma == null || idTurma.trim().isEmpty()) {
            throw new ValidacaoException("Id de turma invalido.");
        }
        if (anoLetivo == null || semestre == null) {
            throw new ValidacaoException("Ano letivo e semestre sao obrigatorios.");
        }
        if (data == null) {
            throw new ValidacaoException("Data da inscricao invalida.");
        }

        this.numeroAluno = numeroAluno;
        this.codigoUc = codigoUc.trim().toUpperCase();
        this.idTurma = idTurma.trim().toUpperCase();
        this.anoLetivo = anoLetivo;
        this.semestre = semestre;
        this.data = data;
    }

    public int getNumeroAluno() {
        return numeroAluno;
    }

    public String getCodigoUc() {
        return codigoUc;
    }

    public String getIdTurma() {
        return idTurma;
    }

    public AnoLetivo getAnoLetivo() {
        return anoLetivo;
    }

    public Semestre getSemestre() {
        return semestre;
    }

    public LocalDate getData() {
        return data;
    }

    @Override
    public String toString() {
        return "Inscricao efetuada com sucesso: " +
                "Numero do aluno = " + numeroAluno +
                ", codigo da uc = " + codigoUc +
                ", turma = " + idTurma +
                ", ano letivo = " + anoLetivo +
                ", semestre = " + semestre.getNumero() +
                ", data = " + data;
    }
}

