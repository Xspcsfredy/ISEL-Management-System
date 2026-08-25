package tps.tp4;

public class Avaliacao {

    // Atributos
    private final int numeroAluno;
    private final String codigoUc;
    private final String elementoAvaliacao;
    private final double nota;
    private final double peso;

    // Construtor da classe
    public Avaliacao(int numeroAluno, String codigoUc, String elementoAvaliacao, double nota, double peso) {
        this.numeroAluno = numeroAluno;
        this.codigoUc = codigoUc;
        this.elementoAvaliacao = elementoAvaliacao;
        this.nota = nota;
        this.peso = peso;
    }

    public int getNumeroAluno() {
        return numeroAluno;
    }

    public String getCodigoUc() {
        return codigoUc;
    }

    public String getElementoAvaliacao() {
        return elementoAvaliacao;
    }

    public double getNota() {
        return nota;
    }

    public double getPeso() {
        return peso;
    }

    @Override
    public String toString() {
        return "Avaliação introduzida: Aluno = " + numeroAluno + ", UC = " + codigoUc + ", Elemento de avaliação = " + elementoAvaliacao + ", Nota = " + nota + ", Peso = " + peso;
    }
}
