package tps.tp4;

// EXCEÇÃO BASE DO PROJETO - Todas as outras classes herdam dela
// Posso escrever um só catch no codigo (AcademicoException e) e apanho qualquer erro do domínio

public class AcademicoException extends Exception { // Herda da classe nativa "Exception"

    public AcademicoException(String mensagem) {
        super(mensagem); // Explicação do erro
    }
}
