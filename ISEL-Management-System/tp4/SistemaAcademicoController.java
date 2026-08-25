package tps.tp4;

import java.nio.file.Path;

// Controlador da interface Swing. Centraliza o acesso ao modelo e a persistencia.
public class SistemaAcademicoController {

    private SistemaAcademico sistema;
    private final Path xmlPath;
    private final Path dtdPath;
    private String startupWarning;
    private String currentUsername;

    public SistemaAcademicoController() {
        this.xmlPath = ProjectPaths.resolveXmlPath();
        this.dtdPath = ProjectPaths.resolveDtdPath();
        this.startupWarning = null;
        this.currentUsername = null;
        this.sistema = carregarInicial();
    }

    public SistemaAcademico getSistema() {
        return sistema;
    }

    public Path getXmlPath() {
        return xmlPath;
    }

    public Path getDtdPath() {
        return dtdPath;
    }

    public String getXmlInfo() {
        return xmlPath.toAbsolutePath().toString();
    }

    public String getStartupWarning() {
        return startupWarning;
    }

    public String getCurrentUsername() {
        return currentUsername;
    }

    public void registerUser(String username, String password) throws AcademicoException {
        sistema.registarUtilizadorOuFalhar(username, password);
        save();
    }

    public boolean login(String username, String password) throws AcademicoException {
        boolean autenticado = sistema.autenticarOuFalhar(username, password);
        if (autenticado) {
            currentUsername = username == null ? "" : username.trim().toLowerCase();
        }
        return autenticado;
    }

    public void save() throws AcademicoException {
        SistemaAcademicoXml.guardar(sistema, xmlPath, dtdPath);
    }

    public void reload() throws AcademicoException {
        sistema = SistemaAcademicoXml.carregar(xmlPath);
    }

    public String getSummary() {
        return "Utilizadores: " + sistema.listarUtilizadores().size()
                + "\nCursos: " + sistema.listarCursos().size()
                + "\nUCs: " + sistema.listarUcs().size()
                + "\nTurmas: " + sistema.listarTurmas().size()
                + "\nAlunos: " + sistema.listarAlunos().size()
                + "\nDocentes: " + sistema.listarPessoas().stream().filter(p -> p instanceof Docente).count()
                + "\nInscricoes: " + sistema.getInscricoesInterno().size()
                + "\nAvaliacoes: " + sistema.getAvaliacoesInterno().size();
    }

    public void criarCurso(String codigo, String nome, int duracaoAnos) throws AcademicoException {
        sistema.criarCursoOuFalhar(codigo, nome, duracaoAnos);
        save();
    }

    public void criarUc(String codigo, String nome, int ects, int semestreNumero, int anoCurso, String codigoCurso, int capacidade) throws AcademicoException {
        sistema.criarUcOuFalhar(codigo, nome, ects, semestreNumero, anoCurso, codigoCurso, capacidade);
        save();
    }

    public void criarTurma(String idTurma, String codigoUc, int anoInicioAnoLetivo, int semestreNumero, int capacidadeMaxima) throws AcademicoException {
        sistema.criarTurmaOuFalhar(idTurma, codigoUc, anoInicioAnoLetivo, semestreNumero, capacidadeMaxima);
        save();
    }

    public void atribuirDocenteTurma(String idTurma, int idDocente) throws AcademicoException {
        sistema.atribuirDocenteTurmaOuFalhar(idTurma, idDocente);
        save();
    }

    public void registarAluno(int numero, String nome, String email, String curso, int ano) throws AcademicoException {
        sistema.registarAlunoOuFalhar(numero, nome, email, curso, ano);
        save();
    }

    public void removerAluno(int numero) throws AcademicoException {
        sistema.removerAlunoOuFalhar(numero);
        save();
    }

    public void registarDocente(int id, String nome, String email, String departamento) throws AcademicoException {
        sistema.registarDocenteOuFalhar(id, nome, email, departamento);
        save();
    }

    public void removerDocente(int id) throws AcademicoException {
        sistema.removerDocenteOuFalhar(id);
        save();
    }

    public void inscreverAluno(int numeroAluno, String codigoUc, String idTurma) throws AcademicoException {
        sistema.inscreverAlunoOuFalhar(numeroAluno, codigoUc, idTurma);
        save();
    }

    public void anularInscricao(int numeroAluno, String idTurma) throws AcademicoException {
        sistema.anularInscricaoOuFalhar(numeroAluno, idTurma);
        save();
    }

    public void lancarAvaliacao(int numeroAluno, String codigoUc, String elementoAvaliacao, double nota, double peso) throws AcademicoException {
        sistema.lancarAvaliacaoOuFalhar(numeroAluno, codigoUc, elementoAvaliacao, nota, peso);
        save();
    }

    public Double calcularNotaFinal(int numeroAluno, String codigoUc) {
        return sistema.calcularNotaFinal(numeroAluno, codigoUc);
    }

    public RelatorioUc gerarRelatorioUc(String codigoUc) {
        return sistema.gerarRelatorioUc(codigoUc);
    }

    public java.util.List<String> consultarAlunosPorTurma(String idTurma) throws AcademicoException {
        return XPathConsultas.listarAlunosPorTurma(xmlPath, idTurma);
    }

    public Double consultarMediaUc(String codigoUc) throws AcademicoException {
        return XPathConsultas.mediaNotasUc(xmlPath, codigoUc);
    }

    private SistemaAcademico carregarInicial() {
        if (!xmlPath.toFile().exists()) {
            return new SistemaAcademico();
        }

        try {
            return SistemaAcademicoXml.carregar(xmlPath);
        } catch (AcademicoException e) {
            startupWarning = e.getMessage();
            return new SistemaAcademico();
        }
    }
}
