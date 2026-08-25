package tps.tp4;

public abstract class Pessoa implements Nomeavel, Emailavel, Identificavel {

    // Atributos comuns a todas as pessoas do ISEL
    private String nome;
    private String email;

    // Construtor da classe abstrata
    public Pessoa(String nome, String email) {
        this.nome = nome;
        this.email = email;
    }

    @Override
    public String getNome() {
        return nome;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public final String getIdentificador() {
        // Implementa a interface Identificavel, a identidade concreta (numero aluno/id docente) fica a cargo das subclasses.
        return getIdPessoa();
    }

    // Cada subclasse deve definir o seu identificador único (numero ou id)
    // Metodo abstrato interno (para nao confundir com o contrato da interface Identificavel)
    protected abstract String getIdPessoa();

    @Override
    public String toString() {
        return "Nome = " + nome + ", Email = " + email;
    }
}
