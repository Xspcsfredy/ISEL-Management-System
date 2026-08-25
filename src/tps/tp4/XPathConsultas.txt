package tps.tp4;

// Imports oficiais do Java para processar o DOM do XML
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

// Imports para configurar e executar o motor de busca XPath
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.xml.sax.InputSource;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

// Classe com consultas sobre o XML usando XPath
public class XPathConsultas {

    // CONSULTA 1: Lista de todos os alunos (numero + nome) inscritos numa determinada turma
    public static List<String> listarAlunosPorTurma(Path xmlPath, String idTurma) throws AcademicoException {
        // Carrega o ficheiro XML para a memória RAM num formato de árvore (DOM)
        Document doc = carregarDocumento(xmlPath);

        // Limpa e normaliza o ID da turma (ex.: "mp1d " --> "MP1D")
        String turma = (idTurma == null ? "" : idTurma.trim().toUpperCase());
        if (turma.isEmpty()) {
            throw new ValidacaoException("Id de turma invalido.");
        }

        try {
            // Cria uma instância do motor XPath
            XPath xpath = XPathFactory.newInstance().newXPath();

            // Vai buscar todos os numeros de aluno inscritos nessa turma
            NodeList numeros = (NodeList) xpath.evaluate("//inscricao[@turma='" + turma + "']/@alunoNumero", doc, XPathConstants.NODESET);
            List<String> resultado = new ArrayList<>();

            // Ciclo para percorrer cada número de aluno encontrado na turma
            for (int i = 0; i < numeros.getLength(); i++) {
                // Vai buscar o valor do atributo
                String numero = numeros.item(i).getNodeValue();

                // NOVA EXPRESSAO XPATH
                // Vai procurar a tag <aluno> que tenha o [@numero] igual ao que acabámos de extrair, e lê o seu [/@nome]
                // Metodo string antes da expressao = se nao encontrar, devolve texto vazio em vez de crashar
                String nome = xpath.evaluate("string(//aluno[@numero='" + numero + "']/@nome)", doc);
                resultado.add("Aluno " + numero + " - " + nome); // Formata o resultado final e add à lista
            }

            return resultado;
        } catch (Exception e) {
            // Se o XPath falhar por má sintaxe ou erro de leitura
            throw new PersistenciaException("Erro ao executar consulta XPath.", e);
        }
    }

    // CONSULTA 2: Calcula a media ponderada das avaliacoes de uma UC (nota*peso / somaPesos)
    public static Double mediaNotasUc(Path xmlPath, String codigoUc) throws AcademicoException {
        // Carrega o ficheiro XML
        Document doc = carregarDocumento(xmlPath);

        // Normaliza o codigo da UC
        String uc = (codigoUc == null ? "" : codigoUc.trim().toUpperCase());
        if (uc.isEmpty()) {
            throw new ValidacaoException("Codigo de UC invalido.");
        }

        try {
            XPath xpath = XPathFactory.newInstance().newXPath();

            // Procura todas as tags <avaliacao> daquela UC e extrai as notas (/@nota)
            NodeList notas = (NodeList) xpath.evaluate("//avaliacao[@uc='" + uc + "']/@nota", doc, XPathConstants.NODESET);
            // Procura as mesmas tags <avaliacao> daquela UC e extrai os pesos respetivos (/@peso)
            NodeList pesos = (NodeList) xpath.evaluate("//avaliacao[@uc='" + uc + "']/@peso", doc, XPathConstants.NODESET);

            // Se n houverem avaliações lançadas para esta UC
            if (notas.getLength() == 0) {
                return null;
            }

            double somaPesos = 0.0;
            double somaPonderada = 0.0;

            // Percorre as listas paralelas (a nota na posição i corresponde ao peso na posição i)
            for (int i = 0; i < notas.getLength(); i++) {
                // Converte o texto do XML (String) em números decimais (double)
                double nota = Double.parseDouble(notas.item(i).getNodeValue());
                double peso = Double.parseDouble(pesos.item(i).getNodeValue());

                somaPesos += peso; // Acumula os pesos
                somaPonderada += nota * peso; // Multiplica a nota pelo seu peso
            }

            // Evita o erro matemático de divisão por zero
            if (somaPesos == 0.0) {
                return null;
            }

            return somaPonderada / somaPesos; // Devolve o cálculo final
        } catch (Exception e) {
            throw new PersistenciaException("Erro ao executar consulta XPath.", e);
        }
    }

    // METODO AUXILIAR: Trata de abrir, validar e converter o ficheiro físico em memória
    private static Document carregarDocumento(Path xmlPath) throws AcademicoException {
        // Validações de segurança do ficheiro
        if (xmlPath == null) {
            throw new PersistenciaException("Caminho do XML invalido.");
        }
        if (!Files.exists(xmlPath)) {
            throw new NaoEncontradoException("XML nao encontrado: " + xmlPath);
        }

        try {
            // Cria a fabrica que sabe como construir leitores de XML
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            // Indica que NAO queremos validar o XML contra o DTD nesta fase
            factory.setValidating(false);

            // Truque de segurança para desligar o carregamento de DTDs externos
            // A mim estava a falhar porque tenho paths com caracteres especiais (ex: º)
            try {
                factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            } catch (Exception ignored) {
                // Se o parser nao suportar esta feature, prossegue na mesma
            }

            // Cria o leitor propriamente dito
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Abre um canal de leitura (InputStream) seguro para ler o ficheiro
            try (InputStream in = Files.newInputStream(xmlPath)) {
                InputSource source = new InputSource(in);
                // Força o caminho do ficheiro a usar ASCII para evitar problemas com caracteres especiais no caminho (ex.: "º")
                source.setSystemId(xmlPath.toUri().toASCIIString());

                // Faz o parse do ficheiro e devolve a árvore DOM pronta a ser consultada
                return builder.parse(source);
            }
        } catch (Exception e) {
            throw new PersistenciaException("Erro ao carregar XML para consultas XPath.", e);
        }
    }
}
