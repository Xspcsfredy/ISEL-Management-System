package tps.tp4;

public class Aluno extends Pessoa {

    // Atributos exclusivos do aluno
    private final int numero;
    private String curso;
    private int ano;

    // Construtor da classe
    public Aluno(int numero, String nome, String email, String curso, int ano) {
        super(nome, email); // Pede a Pessoa os dados comuns
        this.numero = numero;
        this.curso = curso;
        this.ano = ano;
    }

    public int getNumero() {
        return numero;
    }

    public String getCurso() {
        return curso;
    }

    public int getAno() {
        return ano;
    }

    @Override
    protected String getIdPessoa() {
        return String.valueOf(numero); // O id do aluno e o seu numero
    }

    @Override
    public String toString() {
        return "Aluno Numero = " + numero + ", " + super.toString() + ", Curso = " + curso + ", Ano = " + ano;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Aluno))
            return false;

        Aluno a = (Aluno) o;
        return numero == a.numero;
    }
}

