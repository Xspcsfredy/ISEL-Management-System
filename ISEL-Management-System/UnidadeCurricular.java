package tps.tp4;

public class UnidadeCurricular implements Nomeavel, Identificavel {

    // Atributos
    private final String codigo;
    private final String nome;
    private final int ects;
    private final Semestre semestre;
    private final int anoCurso;
    private final String codigoCurso;
    private final int capacidade;

    // Construtor de classe
    public UnidadeCurricular(String codigo, String nome, int ects, Semestre semestre, int anoCurso, String codigoCurso, int capacidade) throws ValidacaoException {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new ValidacaoException("Codigo de UC invalido.");
        }
        if (nome == null || nome.trim().isEmpty()) {
            throw new ValidacaoException("Nome de UC invalido.");
        }
        if (ects <= 0 || ects > 30) {
            throw new ValidacaoException("ECTS invalidos.");
        }
        if (semestre == null) {
            throw new ValidacaoException("Semestre e obrigatorio.");
        }
        if (anoCurso < 1 || anoCurso > 10) {
            throw new ValidacaoException("Ano de curso invalido.");
        }
        if (codigoCurso == null || codigoCurso.trim().isEmpty()) {
            throw new ValidacaoException("Codigo do curso e obrigatorio.");
        }
        if (capacidade <= 0 || capacidade > 500) {
            throw new ValidacaoException("Capacidade invalida.");
        }

        this.codigo = codigo.trim().toUpperCase();
        this.nome = nome.trim();
        this.ects = ects;
        this.semestre = semestre;
        this.anoCurso = anoCurso;
        this.codigoCurso = codigoCurso.trim().toUpperCase();
        this.capacidade = capacidade;
    }

    public String getCodigo() {
        return codigo;
    }

    @Override
    public String getNome() {
        return nome;
    }

    public int getEcts() {
        return ects;
    }

    public Semestre getSemestre() {
        return semestre;
    }

    public int getAnoCurso() {
        return anoCurso;
    }

    public String getCodigoCurso() {
        return codigoCurso;
    }

    public int getCapacidade() {
        return capacidade;
    }

    @Override
    public String getIdentificador() {
        return codigo;
    }

    @Override
    public String toString() {
        return "UC codigo " + codigo + ": Nome = " + nome + ", ECTS = " + ects + ", Ano = " + anoCurso + ", Curso = " + codigoCurso + ", Semestre = " + semestre.getNumero() + ", Capacidade = " + capacidade;
    }
}
