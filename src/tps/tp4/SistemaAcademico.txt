package tps.tp4;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SistemaAcademico {

    // Listas principais do sistema (estado da aplicacao)
    private final List<Aluno> alunos;
    private final List<Docente> docentes;
    private final List<Curso> cursos;
    private final List<UnidadeCurricular> ucs;
    private final List<Turma> turmas;
    private final List<Inscricao> inscricoes;
    private final List<Avaliacao> avaliacoes;
    private final List<Utilizador> utilizadores;

    public SistemaAcademico() {
        alunos = new ArrayList<>();
        docentes = new ArrayList<>();
        cursos = new ArrayList<>();
        ucs = new ArrayList<>();
        turmas = new ArrayList<>();
        inscricoes = new ArrayList<>();
        avaliacoes = new ArrayList<>();
        utilizadores = new ArrayList<>();
    }

    // 0 - REGISTO E LOGIN (UTILIZADORES)
    public void registarUtilizadorOuFalhar(String username, String password) throws AcademicoException {
        if (procurarUtilizador(username) != null) {
            throw new DuplicadoException("Utilizador ja existe.");
        }
        utilizadores.add(Utilizador.criar(username, password));
    }

    public boolean autenticarOuFalhar(String username, String password) throws AcademicoException {
        Utilizador u = procurarUtilizador(username);
        if (u == null) {
            throw new NaoEncontradoException("Utilizador nao encontrado.");
        }
        if (!u.validarPassword(password)) {
            throw new ValidacaoException("Password incorreta.");
        }
        return true;
    }

    public List<Utilizador> listarUtilizadores() {
        return new ArrayList<>(utilizadores);
    }

    // 1 - REGISTAR ALUNO
    public boolean registarAluno(int numero, String nome, String email, String curso, int ano) {
        try {
            registarAlunoOuFalhar(numero, nome, email, curso, ano);
            return true;
        } catch (AcademicoException e) {
            return false;
        }
    }

    // 1A - REGISTAR ALUNO COM EXCECOES
    public void registarAlunoOuFalhar(int numero, String nome, String email, String curso, int ano) throws AcademicoException {
        if (procurarAluno(numero) != null) {
            throw new DuplicadoException("Aluno ja existe.");
        }
        if (!emailValido(email) || ano < 1 || ano > 10) {
            throw new ValidacaoException("Dados invalidos para registo de aluno.");
        }
        if (curso == null || curso.trim().isEmpty()) {
            throw new ValidacaoException("Curso do aluno e obrigatorio.");
        }

        String codigoCurso = curso.trim().toUpperCase();
        if (!cursos.isEmpty() && procurarCurso(codigoCurso) == null) {
            throw new NaoEncontradoException("Curso nao existe. Crie o curso primeiro.");
        }

        alunos.add(new Aluno(numero, nome, email, codigoCurso, ano));
    }

    // 2 - REMOVER ALUNO
    public boolean removerAluno(int numero) {
        try {
            removerAlunoOuFalhar(numero);
            return true;
        } catch (AcademicoException e) {
            return false;
        }
    }

    // 2A - REMOVER ALUNO COM EXCECOES
    public void removerAlunoOuFalhar(int numero) throws AcademicoException {
        for (int i = 0; i < alunos.size(); i++) { // Percorre a lista de alunos
            Aluno a = alunos.get(i); // Vai buscar o do indice

            if (a.getNumero() == numero) { // Compara o numero inserido
                alunos.remove(i); // Remove da lista
                removerDependenciasAluno(numero);
                return;
            }
        }
        throw new NaoEncontradoException("Aluno nao encontrado.");
    }

    // 3 - LISTAR ALUNOS
    public List<Aluno> listarAlunos() {
        return new ArrayList<>(alunos);
    }

    // 3A - LISTAR TODAS AS PESSOAS (POLIMORFISMO)
    public List<Pessoa> listarPessoas() {
        List<Pessoa> pessoas = new ArrayList<>();
        pessoas.addAll(alunos);
        pessoas.addAll(docentes);
        return pessoas;
    }

    // 4 - LANCAR AVALIACAO
    public boolean lancarAvaliacao(int numeroAluno, String codigoUc, String elementoAvaliacao, double nota, double peso) {
        try {
            lancarAvaliacaoOuFalhar(numeroAluno, codigoUc, elementoAvaliacao, nota, peso);
            return true;
        } catch (AcademicoException e) {
            return false;
        }
    }

    // 4A - LANCAR AVALIACAO COM EXCECOES
    public void lancarAvaliacaoOuFalhar(int numeroAluno, String codigoUc, String elementoAvaliacao, double nota, double peso) throws AcademicoException {
        Aluno aluno = procurarAluno(numeroAluno);
        UnidadeCurricular uc = procurarUc(codigoUc);

        if (aluno == null || uc == null) { // Se nao houver aluno ou uc
            throw new NaoEncontradoException("Aluno ou UC nao encontrados.");
        }
        if (!jaInscritoNaUc(numeroAluno, uc.getCodigo())) { // Se o aluno nao esta inscrito
            throw new ValidacaoException("Aluno nao inscrito na UC.");
        }
        if (nota < 0.0 || nota > 20.0 || peso <= 0.0 || peso > 100.0) { // Se notas/pesos invalidos
            throw new ValidacaoException("Nota ou peso invalidos.");
        }
        if (elementoAvaliacao == null || elementoAvaliacao.trim().isEmpty()) {
            throw new ValidacaoException("Elemento de avaliacao invalido.");
        }

        double pesoAtual = 0.0;

        for (Avaliacao a : avaliacoes) {
            if (a.getNumeroAluno() == numeroAluno && a.getCodigoUc().equals(uc.getCodigo())) {
                pesoAtual += a.getPeso();
            }
        }
        if (pesoAtual + peso > 100.0) {
            throw new ValidacaoException("Peso total excede 100%.");
        }

        Avaliacao nova = new Avaliacao(numeroAluno, uc.getCodigo(), elementoAvaliacao.trim(), nota, peso);
        avaliacoes.add(nova);
    }

    // 5 - CALCULAR NOTA FINAL
    public Double calcularNotaFinal(int numeroAluno, String codigoUc) {
        String chaveUc = codigoUc.toUpperCase();
        double somaPesos = 0.0;
        double somaPonderada = 0.0;

        for (Avaliacao a : avaliacoes) {
            if (a.getNumeroAluno() == numeroAluno && a.getCodigoUc().equals(chaveUc)) {
                somaPesos += a.getPeso();
                somaPonderada += a.getNota() * a.getPeso();
            }
        }

        if (somaPesos == 0.0) {
            return null;
        }

        return somaPonderada / somaPesos;
    }

    // 6 - REGISTAR DOCENTE
    public boolean registarDocente(int id, String nome, String email, String departamento) {
        try {
            registarDocenteOuFalhar(id, nome, email, departamento);
            return true;
        } catch (AcademicoException e) {
            return false;
        }
    }

    // 6A - REGISTAR DOCENTE COM EXCECOES
    public void registarDocenteOuFalhar(int id, String nome, String email, String departamento) throws AcademicoException {
        if (procurarDocente(id) != null) {
            throw new DuplicadoException("Docente ja existe.");
        }
        if (!emailValido(email) || departamento == null || departamento.trim().isEmpty()) {
            throw new ValidacaoException("Dados invalidos para registo de docente.");
        }

        docentes.add(new Docente(id, nome, email, departamento.trim()));
    }

    // 7 - REMOVER DOCENTE
    public boolean removerDocente(int id) {
        try {
            removerDocenteOuFalhar(id);
            return true;
        } catch (AcademicoException e) {
            return false;
        }
    }

    // 7A - REMOVER DOCENTE COM EXCECOES
    public void removerDocenteOuFalhar(int id) throws AcademicoException {
        for (int i = 0; i < docentes.size(); i++) {
            Docente d = docentes.get(i);
            if (d.getId() == id) {
                docentes.remove(i);
                desatribuirDocenteDeTurmas(id);
                return;
            }
        }
        throw new NaoEncontradoException("Docente nao encontrado.");
    }

    // 8 - CRIAR CURSO
    public void criarCursoOuFalhar(String codigo, String nome, int duracaoAnos) throws AcademicoException {
        String chave = (codigo == null ? "" : codigo.trim().toUpperCase());
        if (procurarCurso(chave) != null) {
            throw new DuplicadoException("Curso ja existe.");
        }
        cursos.add(new Curso(chave, nome, duracaoAnos));
    }

    public List<Curso> listarCursos() {
        return new ArrayList<>(cursos);
    }

    // 9 - CRIAR UC
    public void criarUcOuFalhar(String codigo, String nome, int ects, int semestreNumero, int anoCurso, String codigoCurso, int capacidade) throws AcademicoException {
        if (procurarUc(codigo) != null) {
            throw new DuplicadoException("UC ja existe.");
        }

        Curso curso = procurarCurso(codigoCurso == null ? "" : codigoCurso.trim().toUpperCase());
        if (curso == null) {
            throw new NaoEncontradoException("Curso nao encontrado. Crie o curso primeiro.");
        }

        Semestre semestre = Semestre.fromNumero(semestreNumero);
        UnidadeCurricular uc = new UnidadeCurricular(codigo, nome, ects, semestre, anoCurso, curso.getCodigo(), capacidade);
        ucs.add(uc);
        curso.associarUc(uc.getCodigo());
    }

    // 10 - CRIAR TURMA
    public void criarTurmaOuFalhar(String idTurma, String codigoUc, int anoInicioAnoLetivo, int semestreNumero, int capacidadeMaxima) throws AcademicoException {
        if (procurarTurma(idTurma) != null) {
            throw new DuplicadoException("Turma ja existe.");
        }

        UnidadeCurricular uc = procurarUc(codigoUc);
        if (uc == null) {
            throw new NaoEncontradoException("UC nao encontrada.");
        }

        AnoLetivo anoLetivo = new AnoLetivo(anoInicioAnoLetivo);
        Semestre semestre = Semestre.fromNumero(semestreNumero);

        Turma turma = new Turma(idTurma, uc.getCodigo(), anoLetivo, semestre, capacidadeMaxima);
        turmas.add(turma);
    }

    public void atribuirDocenteTurmaOuFalhar(String idTurma, int idDocente) throws AcademicoException {
        Turma turma = procurarTurma(idTurma);
        if (turma == null) {
            throw new NaoEncontradoException("Turma nao encontrada.");
        }

        Docente docente = procurarDocente(idDocente);
        if (docente == null) {
            throw new NaoEncontradoException("Docente nao encontrado.");
        }

        turma.atribuirDocenteResponsavel(idDocente);
    }

    public List<Turma> listarTurmas() {
        return new ArrayList<>(turmas);
    }

    public List<UnidadeCurricular> listarUcs() {
        return new ArrayList<>(ucs);
    }

    // 11 - INSCREVER ALUNO EM UC + TURMA
    public void inscreverAlunoOuFalhar(int numeroAluno, String codigoUc, String idTurma) throws AcademicoException {
        Aluno aluno = procurarAluno(numeroAluno);
        UnidadeCurricular uc = procurarUc(codigoUc);
        Turma turma = procurarTurma(idTurma);

        if (aluno == null) {
            throw new NaoEncontradoException("Aluno nao encontrado.");
        }
        if (uc == null) {
            throw new NaoEncontradoException("UC nao encontrada.");
        }
        if (turma == null) {
            throw new NaoEncontradoException("Turma nao encontrada.");
        }
        if (!turma.getCodigoUc().equals(uc.getCodigo())) {
            throw new ValidacaoException("A turma nao pertence a esta UC.");
        }
        if (jaInscritoNaTurma(numeroAluno, turma.getId())) {
            throw new ValidacaoException("Aluno ja inscrito nesta turma.");
        }
        if (contarInscritosTurma(turma.getId()) >= turma.getCapacidadeMaxima()) {
            throw new CapacidadeExcedidaException("Capacidade maxima da turma atingida.");
        }

        inscricoes.add(new Inscricao(numeroAluno, uc.getCodigo(), turma.getId(), turma.getAnoLetivo(), turma.getSemestre(), LocalDate.now()));
    }

    // 12 - ANULAR INSCRICAO
    public void anularInscricaoOuFalhar(int numeroAluno, String idTurma) throws AcademicoException {
        String chaveTurma = (idTurma == null ? "" : idTurma.trim().toUpperCase());
        for (int i = 0; i < inscricoes.size(); i++) {
            Inscricao insc = inscricoes.get(i);

            if (insc.getNumeroAluno() == numeroAluno && insc.getIdTurma().equals(chaveTurma)) {
                inscricoes.remove(i);
                return;
            }
        }
        throw new NaoEncontradoException("Inscricao nao encontrada.");
    }

    // 13 - GERAR RELATORIO DA UC
    public RelatorioUc gerarRelatorioUc(String codigoUc) {

        UnidadeCurricular uc = procurarUc(codigoUc);

        if (uc == null) {
            return null;
        }

        int inscritos = 0;
        int avaliados = 0;
        int aprovados = 0;
        int reprovados = 0;

        double somaNotas = 0;

        for (Inscricao i : inscricoes) {
            if (i.getCodigoUc().equals(uc.getCodigo())) {
                inscritos++;

                Double nota = calcularNotaFinal(i.getNumeroAluno(), uc.getCodigo());

                if (nota != null) {
                    avaliados++;
                    somaNotas = somaNotas + nota;

                    if (nota >= 9.5) {
                        aprovados++;
                    } else {
                        reprovados++;
                    }
                }
            }
        }

        double media = 0;
        if (avaliados > 0) {
            media = somaNotas / avaliados;
        }

        return new RelatorioUc(uc.getCodigo(), inscritos, avaliados, media, aprovados, reprovados);
    }

    // FUNCOES AUXILIARES (PESQUISA)
    private Aluno procurarAluno(int numero) {
        for (Aluno a : alunos) {
            if (a.getNumero() == numero) {
                return a;
            }
        }
        return null;
    }

    private Docente procurarDocente(int id) {
        for (Docente d : docentes) {
            if (d.getId() == id) {
                return d;
            }
        }
        return null;
    }

    private Curso procurarCurso(String codigoCurso) {
        String chave = (codigoCurso == null ? "" : codigoCurso.trim().toUpperCase());
        for (Curso c : cursos) {
            if (c.getCodigo().equals(chave)) {
                return c;
            }
        }
        return null;
    }

    private UnidadeCurricular procurarUc(String codigo) {
        String chave = (codigo == null ? "" : codigo.trim().toUpperCase());
        for (UnidadeCurricular uc : ucs) {
            if (uc.getCodigo().equals(chave)) {
                return uc;
            }
        }
        return null;
    }

    private Turma procurarTurma(String idTurma) {
        String chave = (idTurma == null ? "" : idTurma.trim().toUpperCase());
        for (Turma t : turmas) {
            if (t.getId().equals(chave)) {
                return t;
            }
        }
        return null;
    }

    private Utilizador procurarUtilizador(String username) {
        String chave = (username == null ? "" : username.trim().toLowerCase());
        for (Utilizador u : utilizadores) {
            if (u.getUsername().equals(chave)) {
                return u;
            }
        }
        return null;
    }

    private boolean jaInscritoNaTurma(int numeroAluno, String idTurma) {
        for (Inscricao i : inscricoes) {
            if (i.getNumeroAluno() == numeroAluno && i.getIdTurma().equals(idTurma.trim().toUpperCase())) {
                return true;
            }
        }
        return false;
    }

    private boolean jaInscritoNaUc(int numeroAluno, String codigoUc) {
        for (Inscricao i : inscricoes) {
            if (i.getNumeroAluno() == numeroAluno && i.getCodigoUc().equals(codigoUc.trim().toUpperCase())) {
                return true;
            }
        }
        return false;
    }

    private int contarInscritosTurma(String idTurma) {
        int contador = 0;
        for (Inscricao i : inscricoes) {
            if (i.getIdTurma().equals(idTurma.trim().toUpperCase())) {
                contador++;
            }
        }
        return contador;
    }

    private void desatribuirDocenteDeTurmas(int idDocente) {
        for (Turma t : turmas) {
            Integer atual = t.getIdDocenteResponsavel();
            if (atual != null && atual == idDocente) {
                t.removerDocenteResponsavel();
            }
        }
    }

    private void removerDependenciasAluno(int numeroAluno) {
        // Remove inscricoes do aluno
        for (int i = inscricoes.size() - 1; i >= 0; i--) {
            if (inscricoes.get(i).getNumeroAluno() == numeroAluno) {
                inscricoes.remove(i);
            }
        }
        // Remove avaliacoes do aluno
        for (int i = avaliacoes.size() - 1; i >= 0; i--) {
            if (avaliacoes.get(i).getNumeroAluno() == numeroAluno) {
                avaliacoes.remove(i);
            }
        }
    }

    private boolean emailValido(String email) {
        return (email != null && email.contains("@") && email.contains("."));
    }

    public Avaliacao getUltimaAvaliacao() {
        if (avaliacoes.isEmpty()) {
            return null;
        }

        return avaliacoes.get(avaliacoes.size() - 1);
    }

    public Inscricao getUltimaInscricao() {
        if (inscricoes.isEmpty()) {
            return null;
        }

        return inscricoes.get(inscricoes.size() - 1);
    }

    // Acesso ao estado (usado para persistencia em XML)
    List<Aluno> getAlunosInterno() {
        return alunos;
    }

    List<Docente> getDocentesInterno() {
        return docentes;
    }

    List<Curso> getCursosInterno() {
        return cursos;
    }

    List<UnidadeCurricular> getUcsInterno() {
        return ucs;
    }

    List<Turma> getTurmasInterno() {
        return turmas;
    }

    List<Inscricao> getInscricoesInterno() {
        return inscricoes;
    }

    List<Avaliacao> getAvaliacoesInterno() {
        return avaliacoes;
    }

    List<Utilizador> getUtilizadoresInterno() {
        return utilizadores;
    }
}
