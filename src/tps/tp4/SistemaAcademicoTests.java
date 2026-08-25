package tps.tp4;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class SistemaAcademicoTests {

    // Guarda a pasta raiz do projeto para que os testes saibam onde ler/gravar os ficheiros XML
    private static final Path BASE_DIR = resolverDiretoriaProjeto();

    // Metodo principal que executa todos os testes em fila
    public static void main(String[] args) throws Exception {
        System.out.println("Diretoria base dos testes: " + BASE_DIR.toAbsolutePath());

        testRegistoELogin();
        testPolimorfismoEInterfaces();
        testCapacidadeTurma();
        testPersistenciaXmlEDtd();
        testXPath();

        // Se o programa chegar a esta linha sem crashar, significa que NENHUM assert falhou
        System.out.println("Todos os testes passaram.");
    }

    // Função auxiliar (igual à da AppISEL) que localiza a pasta onde os ficheiros XML devem ficar
    private static Path resolverDiretoriaProjeto() {
        Path atual = Paths.get(System.getProperty("user.dir")).toAbsolutePath();

        for (int i = 0; i < 8; i++) {
            Path xml = atual.resolve(Paths.get("xml", "isel.xml"));
            if (Files.exists(xml)) {
                return atual;
            }
            Path parent = atual.getParent();
            if (parent == null) {
                break;
            }
            atual = parent;
        }

        return Paths.get(System.getProperty("user.dir")).toAbsolutePath();
    }

    // TESTE 1: Garante que o sistema de login e a conversão para minúculas funciona
    private static void testRegistoELogin() throws AcademicoException {
        SistemaAcademico sistema = new SistemaAcademico();

        // Regista um utilizador com "A" maiúsculo
        sistema.registarUtilizadorOuFalhar("Admin", "1234");

        // TESTE DE SUCESSO: Tenta fazer login com "ADMIN" (tudo maiúsculas). Deve dar true
        boolean ok = sistema.autenticarOuFalhar("ADMIN", "1234");
        assert ok : "Login devia ser bem sucedido."; // Se 'ok' for false, o teste falha aqui

        // TESTE DE FALHA CONTROLADA: Tenta fazer login com password errada
        boolean falhou = false;
        try {
            sistema.autenticarOuFalhar("admin", "errada");
        } catch (ValidacaoException e) {
            falhou = true; // Se o motor disparou o erro corretamente, pomos a variável a true
        }
        // Acontece se, mesmo apanhando o erro, fica false
        assert falhou : "Deveria falhar com password errada.";
    }

    // TESTE 2: Garante que as interfaces e as listas genéricas (polimorfismo) estão a funcionar
    private static void testPolimorfismoEInterfaces() {
        SistemaAcademico sistema = new SistemaAcademico();

        try {
            // Cria dados iniciais validos
            sistema.criarCursoOuFalhar("LEIM", "Licenciatura em Engenharia Informatica e Multimedia", 3);
            sistema.registarAlunoOuFalhar(1, "Ana", "ana@mail.com", "LEIM", 1);
            sistema.registarDocenteOuFalhar(10, "Rui", "rui@mail.com", "DEI");
        } catch (AcademicoException e) {
            // Se disparar um erro aqui, algo está muito errado com as regras básicas
            assert false : "Nao devia falhar no registo inicial.";
        }

        // Vai buscar a lista de Pessoas (que mistura Alunos e Docentes)
        List<Pessoa> pessoas = sistema.listarPessoas();

        // Se o tamanho NÃO estiver correto, afirma que têm de lá estar guardados exatamente 2 registos
        assert pessoas.size() == 2 : "Devem existir 2 pessoas.";

        // Obtém e guarda em variáveis a primeira (p0) e a segunda (p1) Pessoa de uma lista chamada pessoas
        Pessoa p0 = pessoas.get(0);
        Pessoa p1 = pessoas.get(1);

        // Garante que o Java reconhece ambos os objetos como instâncias válidas de Pessoa (Polimorfismo)
        assert p0 instanceof Pessoa && p1 instanceof Pessoa : "Polimorfismo esperado.";

        // Testa o polimorfismo com interfaces: trata a Pessoa p0 como um objeto 'Identificavel'
        Identificavel id0 = p0;
        assert id0.getIdentificador() != null : "Identificador nao pode ser nulo.";

        try {
            sistema.criarUcOuFalhar("MP", "Modelacao e Programacao", 6, 2, 1, "LEIM", 200);
        } catch (AcademicoException e) {
            assert false : "Nao devia falhar na criacao de UC.";
        }

        // Vai buscar a UC e "mascara-a" usando as interfaces que ela é obrigada a assinar
        UnidadeCurricular uc = sistema.listarUcs().get(0);
        Nomeavel nomeavel = uc;
        Identificavel identificavel = uc;

        // Garante que os contratos das interfaces devolvem os dados certos da UC
        assert nomeavel.getNome().equals("Modelacao e Programacao") : "Nome da UC incorreto.";
        assert identificavel.getIdentificador().equals("MP") : "Codigo da UC incorreto.";
    }

    // TESTE 3: Garante que o sistema barra inscrições quando a turma está cheia
    private static void testCapacidadeTurma() {
        SistemaAcademico sistema = new SistemaAcademico();

        try {
            sistema.criarCursoOuFalhar("LEIM", "Licenciatura em Engenharia Informatica e Multimedia", 3);
            sistema.registarAlunoOuFalhar(1, "Ana", "ana@mail.com", "LEIM", 1);
            sistema.registarAlunoOuFalhar(2, "Luis", "luis@mail.com", "LEIM", 1);
            sistema.criarUcOuFalhar("MP", "Modelacao e Programacao", 6, 2, 1, "LEIM", 200);

            // Cria uma turma com capacidade máxima de 1 Aluno
            sistema.criarTurmaOuFalhar("MP1D", "MP", 2025, 2, 1);

            // Inscreve a Ana. A turma fica imediatamente cheia
            sistema.inscreverAlunoOuFalhar(1, "MP", "MP1D");
        } catch (AcademicoException e) {
            assert false : "Nao devia falhar na preparacao do teste.";
        }

        boolean lancou = false;
        try {
            // Tenta inscrever o Luis na mesma turma (ja cheia)
            sistema.inscreverAlunoOuFalhar(2, "MP", "MP1D");
        } catch (CapacidadeExcedidaException e) {
            lancou = true; // O motor travou o Luis com a exceção certa
        } catch (AcademicoException e) {
            assert false : "Excecao errada na capacidade.";
        }

        // Garante que o erro de falta de vagas foi mesmo disparado
        assert lancou : "Deveria lancar CapacidadeExcedidaException.";
    }

    // TESTE 4
    private static void testPersistenciaXmlEDtd() throws Exception {
        SistemaAcademico sistema = new SistemaAcademico();

        sistema.registarUtilizadorOuFalhar("admin", "1234");
        sistema.criarCursoOuFalhar("LEIM", "Licenciatura em Engenharia Informatica e Multimedia", 3);
        sistema.criarUcOuFalhar("MP", "Modelacao e Programacao", 6, 2, 1, "LEIM", 200);
        sistema.criarTurmaOuFalhar("MP1D", "MP", 2025, 2, 30);
        sistema.registarAlunoOuFalhar(1, "Ana", "ana@mail.com", "LEIM", 1);
        sistema.inscreverAlunoOuFalhar(1, "MP", "MP1D");
        sistema.lancarAvaliacaoOuFalhar(1, "MP", "Projeto", 18, 50);
        sistema.lancarAvaliacaoOuFalhar(1, "MP", "Teste", 16, 50);

        // Definimos os caminhos de ficheiro temporários para teste
        Path pasta = BASE_DIR.resolve("xml");
        Files.createDirectories(pasta);
        Path xml = pasta.resolve("test_isel.xml");
        Path dtd = pasta.resolve("isel.dtd");

        // GRAVA tudo no ficheiro "test_isel.xml"
        SistemaAcademicoXml.guardar(sistema, xml, dtd);

        // Garante que os ficheiros físicos foram criados com sucesso no disco rígido
        assert Files.exists(xml) : "XML nao foi criado.";
        assert Files.exists(dtd) : "DTD nao foi criado.";

        // Cria um segundo sistema isolado e carrega os dados a partir do ficheiro gravado
        SistemaAcademico sistema2 = SistemaAcademicoXml.carregar(xml);
        assert sistema2.listarUtilizadores().size() == 1 : "Utilizador devia existir apos load.";
        assert sistema2.listarCursos().size() == 1 : "Curso devia existir apos load.";
        assert sistema2.listarUcs().size() == 1 : "UC devia existir apos load.";
        assert sistema2.listarTurmas().size() == 1 : "Turma devia existir apos load.";
        assert sistema2.listarAlunos().size() == 1 : "Aluno devia existir apos load.";
        assert sistema2.getUltimaInscricao() != null : "Inscricao devia existir apos load.";
        assert sistema2.getUltimaAvaliacao() != null : "Avaliacao devia existir apos load.";
    }

    // TESTE 5
    private static void testXPath() throws Exception {
        // Aponta para o ficheiro criado no teste anterior
        Path xml = BASE_DIR.resolve(Paths.get("xml", "test_isel.xml"));

        // Busca direta no XML: "Quantos alunos há na turma MP1D?"
        List<String> alunos = XPathConsultas.listarAlunosPorTurma(xml, "MP1D");
        assert alunos.size() == 1 : "Deveria encontrar 1 aluno na turma via XPath.";

        // Pede ao XPath para ler o XML e calcular a média ponderada das notas da UC "MP"
        Double media = XPathConsultas.mediaNotasUc(xml, "MP");
        assert media != null : "Media nao devia ser nula.";

        assert Math.abs(media - 17.0) < 0.0001 : "Media ponderada esperada 17.0.";
    }
}
