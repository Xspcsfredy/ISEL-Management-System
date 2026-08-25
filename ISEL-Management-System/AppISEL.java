package tps.tp4;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class AppISEL {

    // Path é uma classe que serve para guardar o caminho de um ficheiro no disco
    private static Path XML_PATH; // Vai guardar o caminho exato para o ficheiro "isel.xml"
    private static Path DTD_PATH; // Vai guardar o caminho para o ficheiro "isel.dtd"

    private static SistemaAcademico sistema;
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        // 1. Descobre a pasta raiz do projeto usando a função auxiliar lá de baixo
        Path base = resolverDiretoriaProjeto();

        // 2. O metodo .resolve() junta caminhos
        // Ex.: Se a base for "C:/Projeto", o XML_PATH passa a ser "C:/Projeto/xml/isel.xml"
        XML_PATH = base.resolve(Paths.get("xml", "isel.xml"));
        DTD_PATH = base.resolve(Paths.get("xml", "isel.dtd"));

        System.out.println("XML usado: " + XML_PATH.toAbsolutePath());

        // 3. Tentamos carregar o estado a partir do XML.
        // Se houver algum problema a ler o XML, mostramos a causa e iniciamos vazio.
        sistema = carregarSistemaInicial();

        System.out.println("Utilizadores carregados: " + sistema.listarUtilizadores().size());

        menuLogin();

        // Ao sair do menuLogin, tentamos guardar sempre o estado final
        try {
            SistemaAcademicoXml.guardar(sistema, XML_PATH, DTD_PATH);
        } catch (AcademicoException e) {
            System.out.println("Nao foi possivel guardar automaticamente o XML.");
            System.out.println(e.getMessage());
        }

        scanner.close();
    }

    // Função para resolver um problema típico:
    // Ela sobe as pastas (até 8 níveis) e tenta encontrar onde está a pasta "xml"
    private static Path resolverDiretoriaProjeto() {
        // Descobre onde o Java abriu o programa neste momento
        Path atual = Paths.get(System.getProperty("user.dir")).toAbsolutePath();

        // Ciclo que sobe na estrutura de pastas
        for (int i = 0; i < 8; i++) { // limitamos a procura para nao subir infinitamente
            Path xml = atual.resolve(Paths.get("xml", "isel.xml"));
            if (Files.exists(xml)) {
                return atual;
            }
            // Se não encontrou, apanha a pasta "mãe" (sobe um nível na árvore de pastas) e tenta outra vez
            Path parent = atual.getParent();
            if (parent == null) {
                break; // Se chegou ao topo do disco rígido, desiste
            }
            atual = parent;
        }

        // Se nao encontrar, usa a diretoria atual do programa (pode criar xml/ aqui quando guardar)
        return Paths.get(System.getProperty("user.dir")).toAbsolutePath();
    }

    // MENU LOGIN
    private static void menuLogin() {
        int opcao = -1;

        while (opcao != 0) {
            System.out.println("\n=== INICIAR SESSÃO NO SISTEMA ACADÉMICO DO ISEL ===");
            System.out.println("1. Registar utilizador");
            System.out.println("2. Login");
            System.out.println("0. Sair");

            opcao = lerInteiro("Escolha uma opcao: ");

            switch (opcao) {
                case 1:
                    registarUtilizador();
                    break;
                case 2:
                    if (login()) {
                        menuPrincipal();
                    }
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opcao invalida.");
                    break;
            }
        }
    }

    private static SistemaAcademico carregarSistemaInicial() {
        if (!Files.exists(XML_PATH)) {
            return new SistemaAcademico();
        }

        try {
            return SistemaAcademicoXml.carregar(XML_PATH);
        } catch (AcademicoException e) {
            System.out.println("Aviso: nao foi possivel carregar o XML. O sistema vai iniciar vazio.");
            System.out.println(e.getMessage());
            if (e.getCause() != null) {
                System.out.println("Causa: " + e.getCause().getClass().getSimpleName() + " - " + e.getCause().getMessage());
            }
            return new SistemaAcademico();
        }
    }

    // MENU PRINCIPAL (GESTAO ACADEMICA)
    private static void menuPrincipal() {
        int opcao = -1;

        while (opcao != 0) {
            System.out.println("\n                                                                               ▄▄  ▄                                                  \n" +
                    "                                                                              ▀  ▀▀                                                   \n" +
                    "██▄  ▄██ ██████ ███  ██ ██  ██   ████▄  ██████    ▄████  ██████ ▄█████ ██████ ▄████▄ ▄████▄   ████▄  ▄████▄   ██ ▄█████ ██████ ██     \n" +
                    "██ ▀▀ ██ ██▄▄   ██ ▀▄██ ██  ██   ██  ██ ██▄▄     ██  ▄▄▄ ██▄▄   ▀▀▀▄▄▄   ██   ██▄▄██ ██  ██   ██  ██ ██  ██   ██ ▀▀▀▄▄▄ ██▄▄   ██     \n" +
                    "██    ██ ██▄▄▄▄ ██   ██ ▀████▀   ████▀  ██▄▄▄▄    ▀███▀  ██▄▄▄▄ █████▀   ██   ██  ██ ▀████▀   ████▀  ▀████▀   ██ █████▀ ██▄▄▄▄ ██████ \n" +
                    "                                                                                                                                      ");
            System.out.println("\nPERSISTENCIA:");
            System.out.println("1. Guardar XML");

            System.out.println("\nCURSOS / UCs / TURMAS:");
            System.out.println("2. Criar curso");
            System.out.println("3. Criar UC");
            System.out.println("4. Criar turma");
            System.out.println("5. Atribuir docente a turma");
            System.out.println("6. Listar cursos / UCs / turmas");

            System.out.println("\nPESSOAS:");
            System.out.println("7. Registar aluno");
            System.out.println("8. Remover aluno");
            System.out.println("9. Listar alunos");
            System.out.println("10. Registar docente");
            System.out.println("11. Remover docente");

            System.out.println("\nINSCRICOES / AVALIACOES:");
            System.out.println("12. Inscrever aluno em turma");
            System.out.println("13. Anular inscricao (por turma)");
            System.out.println("14. Lancar avaliacao");
            System.out.println("15. Calcular nota final");
            System.out.println("16. Relatorio de UC");

            System.out.println("\nXPATH:");
            System.out.println("17. Listar alunos por turma (XPath)");
            System.out.println("18. Media de notas de uma UC (XPath)");

            System.out.println("\n0. Terminar sessao (logout)");

            opcao = lerInteiro("Escolha uma opcao: ");

            switch (opcao) {
                case 1:
                    guardarXml();
                    break;
                case 2:
                    criarCurso();
                    break;
                case 3:
                    criarUc();
                    break;
                case 4:
                    criarTurma();
                    break;
                case 5:
                    atribuirDocenteTurma();
                    break;
                case 6:
                    listarEstruturas();
                    break;
                case 7:
                    registarAluno();
                    break;
                case 8:
                    removerAluno();
                    break;
                case 9:
                    listarAlunos();
                    break;
                case 10:
                    registarDocente();
                    break;
                case 11:
                    removerDocente();
                    break;
                case 12:
                    inscreverAluno();
                    break;
                case 13:
                    anularInscricao();
                    break;
                case 14:
                    lancarAvaliacao();
                    break;
                case 15:
                    calcularNotaFinal();
                    break;
                case 16:
                    relatorioUc();
                    break;
                case 17:
                    listarAlunosPorTurmaXPath();
                    break;
                case 18:
                    mediaNotasUcXPath();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Opcao invalida.");
                    break;
            }
        }
    }

    // ===================================================== MENU LOGIN =====================================================
    // 1 - REGISTAR UTILIZADOR
    private static void registarUtilizador() {
        String username = lerTexto("Username: ");
        String password = lerTexto("Password: ");

        try {
            sistema.registarUtilizadorOuFalhar(username, password);
            System.out.println("Utilizador registado.");
            // Guardamos logo aqui para nao perder o registo caso a aplicacao seja interrompida a meio
            try {
                SistemaAcademicoXml.guardar(sistema, XML_PATH, DTD_PATH);
            } catch (AcademicoException e) {
                System.out.println("Aviso: o utilizador foi criado, mas nao foi possivel guardar no XML.");
                System.out.println(e.getMessage());
            }
        } catch (AcademicoException e) {
            System.out.println("Nao foi possivel registar utilizador.");
            System.out.println(e.getMessage());
        }
    }

    // 2 - LOGIN
    private static boolean login() {
        String username = lerTexto("Username: ");
        String password = lerTexto("Password: ");

        try {
            sistema.autenticarOuFalhar(username, password);
            System.out.println("Login efetuado com sucesso.");
            return true;
        } catch (AcademicoException e) {
            System.out.println("Falha no login.");
            System.out.println(e.getMessage());
            return false;
        }
    }

    // ================================================ MENU PRINCIPAL ================================================
    // 3 - CRIAR CURSO
    private static void criarCurso() {
        String codigo = lerTexto("Codigo do curso: ");
        String nome = lerTexto("Nome do curso: ");
        int duracao = lerInteiro("Duracao (anos): ");

        try {
            sistema.criarCursoOuFalhar(codigo, nome, duracao);
            System.out.println("Curso criado.");
        } catch (AcademicoException e) {
            System.out.println("Nao foi possivel criar o curso.");
            System.out.println(e.getMessage());
        }
    }

    // 4 - CRIAR UC
    private static void criarUc() {
        String codigoCurso = lerTexto("Codigo do curso: ");
        String codigo = lerTexto("Codigo da UC (ex: MP): ");
        String nome = lerTexto("Nome da UC: ");
        int ects = lerInteiro("ECTS: ");
        int anoCurso = lerInteiro("Ano do curso: ");
        int semestre = lerInteiro("Semestre (1-2): ");
        int capacidade = lerInteiro("Capacidade maxima (geral): ");

        try {
            sistema.criarUcOuFalhar(codigo, nome, ects, semestre, anoCurso, codigoCurso, capacidade);
            System.out.println("UC criada com sucesso.");
        } catch (AcademicoException e) {
            System.out.println("Nao foi possivel criar a UC.");
            System.out.println(e.getMessage());
        }
    }

    // 5 - CRIAR TURMA
    private static void criarTurma() {
        String idTurma = lerTexto("Id da turma (ex: MP1D): ");
        String codigoUc = lerTexto("Codigo da UC: ");
        int anoInicio = lerInteiro("Ano de inicio do ano letivo (ex: 2025 para 2025/2026): ");
        int semestre = lerInteiro("Semestre (1-2): ");
        int capacidade = lerInteiro("Capacidade maxima da turma (1-30): ");

        try {
            sistema.criarTurmaOuFalhar(idTurma, codigoUc, anoInicio, semestre, capacidade);
            System.out.println("Turma criada.");
        } catch (AcademicoException e) {
            System.out.println("Nao foi possivel criar turma.");
            System.out.println(e.getMessage());
        }
    }

    // 6 - ATRIBUIR DOCENTE A TURMA
    private static void atribuirDocenteTurma() {
        String idTurma = lerTexto("Id da turma: ");
        int idDocente = lerInteiro("Id do docente: ");

        try {
            sistema.atribuirDocenteTurmaOuFalhar(idTurma, idDocente);
            System.out.println("Docente atribuido a turma.");
        } catch (AcademicoException e) {
            System.out.println("Nao foi possivel atribuir docente.");
            System.out.println(e.getMessage());
        }
    }

    // 7 - LISTAR CURSOS / UCS / TURMAS
    private static void listarEstruturas() {
        List<Curso> cursos = sistema.listarCursos();
        List<UnidadeCurricular> ucs = sistema.listarUcs();
        List<Turma> turmas = sistema.listarTurmas();

        System.out.println("\n--- CURSOS ---");
        if (cursos.isEmpty()) {
            System.out.println("Sem cursos registados.");
        } else {
            for (Curso c : cursos) {
                System.out.println(c);
            }
        }

        System.out.println("\n--- UCs ---");
        if (ucs.isEmpty()) {
            System.out.println("Sem UCs registadas.");
        } else {
            for (UnidadeCurricular uc : ucs) {
                System.out.println(uc);
            }
        }

        System.out.println("\n--- TURMAS ---");
        if (turmas.isEmpty()) {
            System.out.println("Sem turmas registadas.");
        } else {
            for (Turma t : turmas) {
                System.out.println(t);
            }
        }
    }

    // 8 - REGISTAR ALUNO
    private static void registarAluno() {
        int numero = lerInteiro("Numero do aluno: ");
        String nome = lerTexto("Nome: ");
        String email = lerTexto("Email: ");
        String curso = lerTexto("Curso: ");
        int ano = lerInteiro("Ano do curso: ");

        try {
            sistema.registarAlunoOuFalhar(numero, nome, email, curso, ano);
            System.out.println("Aluno registado.");
        } catch (AcademicoException e) {
            System.out.println("Nao foi possivel registar o aluno.");
            System.out.println(e.getMessage());
        }
    }

    // 9 - REMOVER ALUNO
    private static void removerAluno() {
        int numero = lerInteiro("Numero do aluno: ");

        try {
            sistema.removerAlunoOuFalhar(numero);
            System.out.println("Aluno removido.");
        } catch (AcademicoException e) {
            System.out.println("Nao foi possivel remover o aluno.");
            System.out.println(e.getMessage());
        }
    }

    // 10 - LISTAR ALUNOS
    private static void listarAlunos() {
        List<Aluno> alunos = sistema.listarAlunos();

        if (alunos.isEmpty()) {
            System.out.println("Nao existem alunos registados.");
            return;
        }

        for (Aluno a : alunos) {
            System.out.println(a);
        }
    }

    // 11 - REGISTAR DOCENTE
    private static void registarDocente() {
        int id = lerInteiro("ID do docente: ");
        String nome = lerTexto("Nome: ");
        String email = lerTexto("Email: ");
        String departamento = lerTexto("Departamento: ");

        try {
            sistema.registarDocenteOuFalhar(id, nome, email, departamento);
            System.out.println("Docente registado.");
        } catch (AcademicoException e) {
            System.out.println("Nao foi possivel registar o docente.");
            System.out.println(e.getMessage());
        }
    }

    // 12 - REMOVER DOCENTE
    private static void removerDocente() {
        int id = lerInteiro("ID do docente: ");

        try {
            sistema.removerDocenteOuFalhar(id);
            System.out.println("Docente removido.");
        } catch (AcademicoException e) {
            System.out.println("Docente nao encontrado.");
            System.out.println(e.getMessage());
        }
    }

    // 13 - INSCREVER ALUNO EM TURMA
    private static void inscreverAluno() {
        int numeroAluno = lerInteiro("Numero do aluno: ");
        String codigoUc = lerTexto("Codigo da UC: ");
        String idTurma = lerTexto("Id da turma: ");

        try {
            sistema.inscreverAlunoOuFalhar(numeroAluno, codigoUc, idTurma);
            Inscricao i = sistema.getUltimaInscricao();
            System.out.println(i);
        } catch (AcademicoException e) {
            System.out.println("Nao foi possivel inscrever.");
            System.out.println(e.getMessage());
        }
    }

    // 14 - ANULAR INSCRICAO (POR TURMA)
    private static void anularInscricao() {
        int numeroAluno = lerInteiro("Numero do aluno: ");
        String idTurma = lerTexto("Id da turma: ");

        try {
            sistema.anularInscricaoOuFalhar(numeroAluno, idTurma);
            System.out.println("Inscricao anulada.");
        } catch (AcademicoException e) {
            System.out.println("Inscricao nao encontrada.");
            System.out.println(e.getMessage());
        }
    }

    // 15 - LANCAR AVALIACAO
    private static void lancarAvaliacao() {
        int numeroAluno = lerInteiro("Numero do aluno: ");
        String codigoUc = lerTexto("Codigo da UC: ");
        String elemento = lerTexto("Elemento de avaliacao (ex: Projeto): ");
        double nota = lerDouble("Nota (0-20): ");
        double peso = lerDouble("Peso (0-100): ");

        try {
            sistema.lancarAvaliacaoOuFalhar(numeroAluno, codigoUc, elemento, nota, peso);
            Avaliacao a = sistema.getUltimaAvaliacao();
            System.out.println(a);
        } catch (AcademicoException e) {
            System.out.println("Nao foi possivel lancar avaliacao.");
            System.out.println(e.getMessage());
        }
    }

    // 16 - CALCULAR NOTA FINAL
    private static void calcularNotaFinal() {
        int numeroAluno = lerInteiro("Numero do aluno: ");
        String codigoUc = lerTexto("Codigo da UC: ");

        Double notaFinal = sistema.calcularNotaFinal(numeroAluno, codigoUc);
        if (notaFinal == null) {
            System.out.println("Nao existem avaliacoes para calcular nota final.");
            return;
        }

        System.out.println("Nota final: " + notaFinal);
    }

    // 17 - RELATORIO UC
    private static void relatorioUc() {
        String codigo = lerTexto("Codigo da UC: ");
        RelatorioUc r = sistema.gerarRelatorioUc(codigo);

        if (r == null) {
            System.out.println("UC nao encontrada.");
            return;
        }

        System.out.println("\n=== RELATORIO DA UC ===");
        System.out.println(r);
    }

    // 18 - LISTAR ALUNOS POR TURMA (XPATH)
    private static void listarAlunosPorTurmaXPath() {
        String idTurma = lerTexto("Id da turma: ");

        try {
            List<String> alunos = XPathConsultas.listarAlunosPorTurma(XML_PATH, idTurma);
            if (alunos.isEmpty()) {
                System.out.println("Nao existem alunos nessa turma (ou nao existe no XML).");
                return;
            }
            for (String a : alunos) {
                System.out.println(a);
            }
        } catch (AcademicoException e) {
            System.out.println("Erro ao executar XPath.");
            System.out.println(e.getMessage());
        }
    }

    // 19 - MEDIA NOTAS UC (XPATH)
    private static void mediaNotasUcXPath() {
        String codigoUc = lerTexto("Codigo da UC: ");

        try {
            Double media = XPathConsultas.mediaNotasUc(XML_PATH, codigoUc);
            if (media == null) {
                System.out.println("Sem notas para essa UC (ou nao existe no XML).");
                return;
            }
            System.out.println("Media de notas (ponderada por peso): " + media);
        } catch (AcademicoException e) {
            System.out.println("Erro ao executar XPath.");
            System.out.println(e.getMessage());
        }
    }

    // GUARDAR XML (MENU)
    private static void guardarXml() {
        try {
            SistemaAcademicoXml.guardar(sistema, XML_PATH, DTD_PATH);
            System.out.println("XML guardado em: " + XML_PATH);
        } catch (AcademicoException e) {
            System.out.println("Nao foi possivel guardar XML.");
            System.out.println(e.getMessage());
        }
    }

    // FUNCOES AUXILIARES PARA EXCECOES
    private static int lerInteiro(String mensagem) {
        while (true) {
            try { // Bloco para testar / ver se encontra erros, se nao encontrar da return normalmente
                System.out.print(mensagem);
                return Integer.parseInt(scanner.nextLine().trim()); // Converte para inteiro
            } catch (NumberFormatException e) { // Se encontrar erros no formato do numero executa este bloco
                System.out.println("Valor invalido. Introduza um numero inteiro.");
            }
        }
    }

    private static double lerDouble(String mensagem) {
        while (true) {
            try { // Bloco para testar / ver se encontra erros, se nao encontrar da return normalmente
                System.out.print(mensagem);
                String valor = scanner.nextLine().trim().replace(',', '.');
                return Double.parseDouble(valor); // Converte para double
            } catch (NumberFormatException e) { // Se encontrar erros no formato do numero executa este bloco
                System.out.println("Valor invalido. Introduza um numero.");
            }
        }
    }

    private static String lerTexto(String mensagem) {
        while (true) {
            System.out.print(mensagem);
            String valor = scanner.nextLine().trim();
            if (!valor.isEmpty()) {
                return valor;
            }
            System.out.println("O valor nao pode ser vazio.");
        }
    }
}
