package tps.tp4;

public class Turma implements Identificavel, Nomeavel {

    // Atributos
    private final String id;
    private final String codigoUc;
    private final AnoLetivo anoLetivo;
    private final Semestre semestre;
    private final int capacidadeMaxima;

    // Podemos criar uma turma, sem que haja professores
    // Logo precisamos que a variavel do id do professor possa ser 'null' (ainda nao atribuida)
    // Para isso usamos 'Integer' e não 'int', porque Integer aceita o valor null
    private Integer idDocenteResponsavel; // Pode ser null (ainda nao atribuido)

    // Construtor da classe
    // Tem arquitetura de segurança = quando se faz 'new Turma(...)', passa por uma serie de verificações a ver se sao validos
    public Turma(String id, String codigoUc, AnoLetivo anoLetivo, Semestre semestre, int capacidadeMaxima) throws ValidacaoException {
        if (id == null || id.trim().isEmpty()) {
            throw new ValidacaoException("Id da turma nao pode ser vazio.");
        }
        if (codigoUc == null || codigoUc.trim().isEmpty()) {
            throw new ValidacaoException("Codigo de UC invalido.");
        }
        if (anoLetivo == null || semestre == null) {
            throw new ValidacaoException("Ano letivo e semestre sao obrigatorios.");
        }
        if (capacidadeMaxima <= 0 || capacidadeMaxima > 30) {
            throw new ValidacaoException("Capacidade maxima invalida (1-30).");
        }

        // Criação dos atributos e atribuição dos valores
        this.id = id.trim().toUpperCase(); // Limpa espaços e coloca em maiusculas
        this.codigoUc = codigoUc.trim().toUpperCase(); // Limpa espaços e coloca em maiusculas
        this.anoLetivo = anoLetivo;
        this.semestre = semestre;
        this.capacidadeMaxima = capacidadeMaxima;
        this.idDocenteResponsavel = null; // Todas as turmas nascem sem nenhum professor atribuido
    }

    public String getId() {
        return id;
    }

    public String getCodigoUc() {
        return codigoUc;
    }

    public AnoLetivo getAnoLetivo() {
        return anoLetivo;
    }

    public Semestre getSemestre() {
        return semestre;
    }

    public int getCapacidadeMaxima() {
        return capacidadeMaxima;
    }

    public Integer getIdDocenteResponsavel() {
        return idDocenteResponsavel;
    }

    // Aqui é onde atribuimos um professor a turma
    public void atribuirDocenteResponsavel(int idDocente) {
        this.idDocenteResponsavel = idDocente;
    }

    // Aqui é onde removemos um professor da turma
    public void removerDocenteResponsavel() {
        this.idDocenteResponsavel = null;
    }

    @Override
    public String getIdentificador() {
        return id;
    }

    @Override
    public String getNome() {
        return "Turma " + id + " (" + codigoUc + " - " + anoLetivo + " - " + semestre.getNumero() + "º semestre)";
    }

    @Override
    public String toString() {
        return getNome() + ", Capacidade = " + capacidadeMaxima + ", Docente = " + (idDocenteResponsavel == null ? "N/A" : idDocenteResponsavel);
    }
}
