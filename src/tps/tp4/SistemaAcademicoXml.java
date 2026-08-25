package tps.tp4;

// Imports do java para manipulação da arvore DOM do XML
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

// Imports para construir leitores e escritores de Xml
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.xml.sax.InputSource;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

// Classe responsavel por guardar/carregar o estado do SistemaAcademico em XML (com DTD externo)
public class SistemaAcademicoXml {

    // String gigante que guarda as regras gramaticais do xml
    private static final String DTD_CONTEUDO =
            "<!ELEMENT isel (utilizadores, cursos, ucs, turmas, alunos, docentes, inscricoes, avaliacoes)>\n" +
            "\n" +
            "<!ELEMENT utilizadores (utilizador*)>\n" +
            "<!ELEMENT utilizador EMPTY>\n" +
            "<!ATTLIST utilizador username CDATA #REQUIRED>\n" +
            "<!ATTLIST utilizador password CDATA #REQUIRED>\n" +
            "\n" +
            "<!ELEMENT cursos (curso*)>\n" +
            "<!ELEMENT curso (ucRef*)>\n" +
            "<!ATTLIST curso codigo CDATA #REQUIRED>\n" +
            "<!ATTLIST curso nome CDATA #REQUIRED>\n" +
            "<!ATTLIST curso duracaoAnos CDATA #REQUIRED>\n" +
            "<!ELEMENT ucRef EMPTY>\n" +
            "<!ATTLIST ucRef codigo CDATA #REQUIRED>\n" +
            "\n" +
            "<!ELEMENT ucs (uc*)>\n" +
            "<!ELEMENT uc EMPTY>\n" +
            "<!ATTLIST uc codigo CDATA #REQUIRED>\n" +
            "<!ATTLIST uc nome CDATA #REQUIRED>\n" +
            "<!ATTLIST uc ects CDATA #REQUIRED>\n" +
            "<!ATTLIST uc semestre CDATA #REQUIRED>\n" +
            "<!ATTLIST uc anoCurso CDATA #REQUIRED>\n" +
            "<!ATTLIST uc curso CDATA #REQUIRED>\n" +
            "<!ATTLIST uc capacidade CDATA #REQUIRED>\n" +
            "\n" +
            "<!ELEMENT turmas (turma*)>\n" +
            "<!ELEMENT turma EMPTY>\n" +
            "<!ATTLIST turma id CDATA #REQUIRED>\n" +
            "<!ATTLIST turma uc CDATA #REQUIRED>\n" +
            "<!ATTLIST turma anoInicio CDATA #REQUIRED>\n" +
            "<!ATTLIST turma semestre CDATA #REQUIRED>\n" +
            "<!ATTLIST turma capacidade CDATA #REQUIRED>\n" +
            "<!ATTLIST turma docenteId CDATA #IMPLIED>\n" +
            "\n" +
            "<!ELEMENT alunos (aluno*)>\n" +
            "<!ELEMENT aluno EMPTY>\n" +
            "<!ATTLIST aluno numero CDATA #REQUIRED>\n" +
            "<!ATTLIST aluno nome CDATA #REQUIRED>\n" +
            "<!ATTLIST aluno email CDATA #REQUIRED>\n" +
            "<!ATTLIST aluno curso CDATA #REQUIRED>\n" +
            "<!ATTLIST aluno ano CDATA #REQUIRED>\n" +
            "\n" +
            "<!ELEMENT docentes (docente*)>\n" +
            "<!ELEMENT docente EMPTY>\n" +
            "<!ATTLIST docente id CDATA #REQUIRED>\n" +
            "<!ATTLIST docente nome CDATA #REQUIRED>\n" +
            "<!ATTLIST docente email CDATA #REQUIRED>\n" +
            "<!ATTLIST docente departamento CDATA #REQUIRED>\n" +
            "\n" +
            "<!ELEMENT inscricoes (inscricao*)>\n" +
            "<!ELEMENT inscricao EMPTY>\n" +
            "<!ATTLIST inscricao alunoNumero CDATA #REQUIRED>\n" +
            "<!ATTLIST inscricao uc CDATA #REQUIRED>\n" +
            "<!ATTLIST inscricao turma CDATA #REQUIRED>\n" +
            "<!ATTLIST inscricao anoInicio CDATA #REQUIRED>\n" +
            "<!ATTLIST inscricao semestre CDATA #REQUIRED>\n" +
            "<!ATTLIST inscricao data CDATA #REQUIRED>\n" +
            "\n" +
            "<!ELEMENT avaliacoes (avaliacao*)>\n" +
            "<!ELEMENT avaliacao EMPTY>\n" +
            "<!ATTLIST avaliacao alunoNumero CDATA #REQUIRED>\n" +
            "<!ATTLIST avaliacao uc CDATA #REQUIRED>\n" +
            "<!ATTLIST avaliacao elemento CDATA #REQUIRED>\n" +
            "<!ATTLIST avaliacao nota CDATA #REQUIRED>\n" +
            "<!ATTLIST avaliacao peso CDATA #REQUIRED>\n";

    // Guarda o estado atual do sistema em XML + DTD
    public static void guardar(SistemaAcademico sistema, Path xmlPath, Path dtdPath) throws AcademicoException {
        if (sistema == null) {
            throw new PersistenciaException("Sistema nao pode ser nulo.");
        }

        try {
            // Garante que as pastas onde os ficheiros vao ficar existem no disco (se nao existirem, cria-as)
            Files.createDirectories(xmlPath.getParent());
            Files.createDirectories(dtdPath.getParent());

            // Escreve fisicamente o ficheiro "isel.dtd" no disco rigido
            garantirDtd(dtdPath);

            // Inicia o construtor de documentos DOM vazios
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            // Cria a tag raiz do XML: <isel> </isel>
            Element root = doc.createElement("isel");
            doc.appendChild(root);

            // Executa as funções auxiliares que convertem cada lista de objetos em sub-tags XML
            escreverUtilizadores(doc, root, sistema);
            escreverCursos(doc, root, sistema);
            escreverUcs(doc, root, sistema);
            escreverTurmas(doc, root, sistema);
            escreverAlunos(doc, root, sistema);
            escreverDocentes(doc, root, sistema);
            escreverInscricoes(doc, root, sistema);
            escreverAvaliacoes(doc, root, sistema);

            // Prepara o "Transformador" (Transformer), que vai converter a árvore DOM em texto real
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();

            // Configurações do ficheiro de texto:
            transformer.setOutputProperty(OutputKeys.INDENT, "yes"); // Ativa a indentação (coloca espaços e quebras de linha para o XML ficar legível)
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8"); // Usa codificação padrão UTF-8 (suporta acentos)
            transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, dtdPath.getFileName().toString()); // Liga ao DTD externo

            // Abre o ficheiro e despeja lá dentro o texto XML gerado
            try (OutputStream out = Files.newOutputStream(xmlPath)) {
                transformer.transform(new DOMSource(doc), new StreamResult(out));
            }
        } catch (Exception e) {
            throw new PersistenciaException("Erro ao guardar XML.", e);
        }
    }

    // Carrega o XML do disco e reconstroi o sistemaAcademico
    public static SistemaAcademico carregar(Path xmlPath) throws AcademicoException {
        if (xmlPath == null) {
            throw new PersistenciaException("Caminho do XML invalido.");
        }
        if (!Files.exists(xmlPath)) {
            throw new NaoEncontradoException("XML nao encontrado: " + xmlPath);
        }

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(false);
            // Nao precisamos de validar com DTD no carregamento (o DTD existe para planeamento/gramatica).
            // Isto evita falhas se o parser tiver restricoes a entidades externas.
            factory.setValidating(false);

            try {
                factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            } catch (Exception ignored) {
                // Se nao suportar esta feature, prossegue na mesma
            }

            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc;
            try (InputStream in = Files.newInputStream(xmlPath)) {
                InputSource source = new InputSource(in);
                source.setSystemId(xmlPath.toUri().toString()); // Importante para resolver o DTD relativo ao ficheiro
                doc = builder.parse(source);
            }

            SistemaAcademico sistema = new SistemaAcademico();

            lerUtilizadores(doc, sistema);
            lerCursos(doc, sistema);
            lerUcs(doc, sistema);
            lerTurmas(doc, sistema);
            lerAlunos(doc, sistema);
            lerDocentes(doc, sistema);
            lerInscricoes(doc, sistema);
            lerAvaliacoes(doc, sistema);

            return sistema;
        } catch (Exception e) {
            throw new PersistenciaException("Erro ao carregar XML (" + e.getClass().getSimpleName() + "): " + e.getMessage(), e);
        }
    }

    // Variante util para o arranque da aplicacao
    public static SistemaAcademico carregarSeExistirOuVazio(Path xmlPath) {
        try {
            if (Files.exists(xmlPath)) {
                return carregar(xmlPath);
            }
        } catch (AcademicoException ignored) {
            // Se falhar o load, criamos vazio para o utilizador conseguir continuar
            // (o AppISEL mostra um aviso mais explicito quando necessario)
        }
        return new SistemaAcademico();
    }

    private static void garantirDtd(Path dtdPath) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(dtdPath, StandardCharsets.UTF_8)) {
            writer.write(DTD_CONTEUDO);
        }
    }

    // --- ESCRITA (OBJETOS -> XML) ---

    private static void escreverUtilizadores(Document doc, Element root, SistemaAcademico sistema) {
        Element e = doc.createElement("utilizadores");
        root.appendChild(e);

        for (Utilizador u : sistema.getUtilizadoresInterno()) {
            Element eu = doc.createElement("utilizador");
            eu.setAttribute("username", u.getUsername());
            eu.setAttribute("password", u.getPassword());
            e.appendChild(eu);
        }
    }

    private static void escreverCursos(Document doc, Element root, SistemaAcademico sistema) {
        Element e = doc.createElement("cursos");
        root.appendChild(e);

        for (Curso c : sistema.getCursosInterno()) {
            Element ec = doc.createElement("curso");
            ec.setAttribute("codigo", c.getCodigo());
            ec.setAttribute("nome", c.getNome());
            ec.setAttribute("duracaoAnos", String.valueOf(c.getDuracaoAnos()));

            for (String codigoUc : c.getCodigosUcs()) {
                Element ref = doc.createElement("ucRef");
                ref.setAttribute("codigo", codigoUc);
                ec.appendChild(ref);
            }

            e.appendChild(ec);
        }
    }

    private static void escreverUcs(Document doc, Element root, SistemaAcademico sistema) {
        Element e = doc.createElement("ucs");
        root.appendChild(e);

        for (UnidadeCurricular uc : sistema.getUcsInterno()) {
            Element eu = doc.createElement("uc");
            eu.setAttribute("codigo", uc.getCodigo());
            eu.setAttribute("nome", uc.getNome());
            eu.setAttribute("ects", String.valueOf(uc.getEcts()));
            eu.setAttribute("semestre", String.valueOf(uc.getSemestre().getNumero()));
            eu.setAttribute("anoCurso", String.valueOf(uc.getAnoCurso()));
            eu.setAttribute("curso", uc.getCodigoCurso());
            eu.setAttribute("capacidade", String.valueOf(uc.getCapacidade()));
            e.appendChild(eu);
        }
    }

    private static void escreverTurmas(Document doc, Element root, SistemaAcademico sistema) {
        Element e = doc.createElement("turmas");
        root.appendChild(e);

        for (Turma t : sistema.getTurmasInterno()) {
            Element et = doc.createElement("turma");
            et.setAttribute("id", t.getId());
            et.setAttribute("uc", t.getCodigoUc());
            et.setAttribute("anoInicio", String.valueOf(t.getAnoLetivo().getAnoInicio()));
            et.setAttribute("semestre", String.valueOf(t.getSemestre().getNumero()));
            et.setAttribute("capacidade", String.valueOf(t.getCapacidadeMaxima()));

            Integer docenteId = t.getIdDocenteResponsavel();
            if (docenteId != null) {
                et.setAttribute("docenteId", String.valueOf(docenteId));
            }

            e.appendChild(et);
        }
    }

    private static void escreverAlunos(Document doc, Element root, SistemaAcademico sistema) {
        Element e = doc.createElement("alunos");
        root.appendChild(e);

        for (Aluno a : sistema.getAlunosInterno()) {
            Element ea = doc.createElement("aluno");
            ea.setAttribute("numero", String.valueOf(a.getNumero()));
            ea.setAttribute("nome", a.getNome());
            ea.setAttribute("email", a.getEmail());
            ea.setAttribute("curso", a.getCurso());
            ea.setAttribute("ano", String.valueOf(a.getAno()));
            e.appendChild(ea);
        }
    }

    private static void escreverDocentes(Document doc, Element root, SistemaAcademico sistema) {
        Element e = doc.createElement("docentes");
        root.appendChild(e);

        for (Docente d : sistema.getDocentesInterno()) {
            Element ed = doc.createElement("docente");
            ed.setAttribute("id", String.valueOf(d.getId()));
            ed.setAttribute("nome", d.getNome());
            ed.setAttribute("email", d.getEmail());
            ed.setAttribute("departamento", d.getDepartamento());
            e.appendChild(ed);
        }
    }

    private static void escreverInscricoes(Document doc, Element root, SistemaAcademico sistema) {
        Element e = doc.createElement("inscricoes");
        root.appendChild(e);

        for (Inscricao i : sistema.getInscricoesInterno()) {
            Element ei = doc.createElement("inscricao");
            ei.setAttribute("alunoNumero", String.valueOf(i.getNumeroAluno()));
            ei.setAttribute("uc", i.getCodigoUc());
            ei.setAttribute("turma", i.getIdTurma());
            ei.setAttribute("anoInicio", String.valueOf(i.getAnoLetivo().getAnoInicio()));
            ei.setAttribute("semestre", String.valueOf(i.getSemestre().getNumero()));
            ei.setAttribute("data", i.getData().toString());
            e.appendChild(ei);
        }
    }

    private static void escreverAvaliacoes(Document doc, Element root, SistemaAcademico sistema) {
        Element e = doc.createElement("avaliacoes");
        root.appendChild(e);

        for (Avaliacao a : sistema.getAvaliacoesInterno()) {
            Element ea = doc.createElement("avaliacao");
            ea.setAttribute("alunoNumero", String.valueOf(a.getNumeroAluno()));
            ea.setAttribute("uc", a.getCodigoUc());
            ea.setAttribute("elemento", a.getElementoAvaliacao());
            ea.setAttribute("nota", String.valueOf(a.getNota()));
            ea.setAttribute("peso", String.valueOf(a.getPeso()));
            e.appendChild(ea);
        }
    }

    // --- LEITURA (XML -> OBJETOS) ---

    private static void lerUtilizadores(Document doc, SistemaAcademico sistema) {
        NodeList list = doc.getElementsByTagName("utilizador");
        for (int i = 0; i < list.getLength(); i++) {
            Element e = (Element) list.item(i);
            String username = e.getAttribute("username");
            String password = e.getAttribute("password");

            try {
                sistema.getUtilizadoresInterno().add(Utilizador.criarComPassword(username, password));
            } catch (ValidacaoException ex) {
                // Se houver dados estranhos no XML, ignoramos este utilizador
            }
        }
    }

    private static void lerCursos(Document doc, SistemaAcademico sistema) throws AcademicoException {
        NodeList list = doc.getElementsByTagName("curso");
        for (int i = 0; i < list.getLength(); i++) {
            Element e = (Element) list.item(i);
            String codigo = e.getAttribute("codigo");
            String nome = e.getAttribute("nome");
            int duracao = Integer.parseInt(e.getAttribute("duracaoAnos"));

            // Criamos diretamente e colocamos na lista interna (para nao depender de ordem/validacoes)
            sistema.getCursosInterno().add(new Curso(codigo, nome, duracao));
        }
    }

    private static void lerUcs(Document doc, SistemaAcademico sistema) throws AcademicoException {
        NodeList list = doc.getElementsByTagName("uc");
        for (int i = 0; i < list.getLength(); i++) {
            Element e = (Element) list.item(i);
            String codigo = e.getAttribute("codigo");
            String nome = e.getAttribute("nome");
            int ects = Integer.parseInt(e.getAttribute("ects"));
            int semestreNumero = Integer.parseInt(e.getAttribute("semestre"));
            int anoCurso = Integer.parseInt(e.getAttribute("anoCurso"));
            String curso = e.getAttribute("curso");
            int capacidade = Integer.parseInt(e.getAttribute("capacidade"));

            sistema.getUcsInterno().add(new UnidadeCurricular(codigo, nome, ects, Semestre.fromNumero(semestreNumero), anoCurso, curso, capacidade));
        }
    }

    private static void lerTurmas(Document doc, SistemaAcademico sistema) throws AcademicoException {
        NodeList list = doc.getElementsByTagName("turma");
        for (int i = 0; i < list.getLength(); i++) {
            Element e = (Element) list.item(i);
            String id = e.getAttribute("id");
            String uc = e.getAttribute("uc");
            int anoInicio = Integer.parseInt(e.getAttribute("anoInicio"));
            int semestreNumero = Integer.parseInt(e.getAttribute("semestre"));
            int capacidade = Integer.parseInt(e.getAttribute("capacidade"));

            Turma t = new Turma(id, uc, new AnoLetivo(anoInicio), Semestre.fromNumero(semestreNumero), capacidade);

            String docenteId = e.getAttribute("docenteId");
            if (docenteId != null && !docenteId.isEmpty()) {
                t.atribuirDocenteResponsavel(Integer.parseInt(docenteId));
            }

            sistema.getTurmasInterno().add(t);
        }
    }

    private static void lerAlunos(Document doc, SistemaAcademico sistema) {
        NodeList list = doc.getElementsByTagName("aluno");
        for (int i = 0; i < list.getLength(); i++) {
            Element e = (Element) list.item(i);
            int numero = Integer.parseInt(e.getAttribute("numero"));
            String nome = e.getAttribute("nome");
            String email = e.getAttribute("email");
            String curso = e.getAttribute("curso");
            int ano = Integer.parseInt(e.getAttribute("ano"));

            sistema.getAlunosInterno().add(new Aluno(numero, nome, email, curso, ano));
        }
    }

    private static void lerDocentes(Document doc, SistemaAcademico sistema) {
        NodeList list = doc.getElementsByTagName("docente");
        for (int i = 0; i < list.getLength(); i++) {
            Element e = (Element) list.item(i);
            int id = Integer.parseInt(e.getAttribute("id"));
            String nome = e.getAttribute("nome");
            String email = e.getAttribute("email");
            String dep = e.getAttribute("departamento");

            sistema.getDocentesInterno().add(new Docente(id, nome, email, dep));
        }
    }

    private static void lerInscricoes(Document doc, SistemaAcademico sistema) throws AcademicoException {
        NodeList list = doc.getElementsByTagName("inscricao");
        for (int i = 0; i < list.getLength(); i++) {
            Element e = (Element) list.item(i);
            int alunoNumero = Integer.parseInt(e.getAttribute("alunoNumero"));
            String uc = e.getAttribute("uc");
            String turma = e.getAttribute("turma");
            int anoInicio = Integer.parseInt(e.getAttribute("anoInicio"));
            int semestreNumero = Integer.parseInt(e.getAttribute("semestre"));
            String data = e.getAttribute("data");

            sistema.getInscricoesInterno().add(new Inscricao(alunoNumero, uc, turma, new AnoLetivo(anoInicio), Semestre.fromNumero(semestreNumero), java.time.LocalDate.parse(data)));
        }
    }

    private static void lerAvaliacoes(Document doc, SistemaAcademico sistema) {
        NodeList list = doc.getElementsByTagName("avaliacao");
        for (int i = 0; i < list.getLength(); i++) {
            Element e = (Element) list.item(i);
            int alunoNumero = Integer.parseInt(e.getAttribute("alunoNumero"));
            String uc = e.getAttribute("uc");
            String elemento = e.getAttribute("elemento");
            double nota = Double.parseDouble(e.getAttribute("nota"));
            double peso = Double.parseDouble(e.getAttribute("peso"));

            sistema.getAvaliacoesInterno().add(new Avaliacao(alunoNumero, uc, elemento, nota, peso));
        }
    }
}
