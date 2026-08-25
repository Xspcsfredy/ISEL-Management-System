package tps.tp4;

public class Docente extends Pessoa {

    // Atributos
    private final int id;
    private String departamento;

    // Construtor da classe
    public Docente(int id, String nome, String email, String departamento) {
        super(nome, email); // Pede a Pessoa os dados comuns
        this.id = id;
        this.departamento = departamento;
    }

    public int getId() {
        return id;
    }

    public String getDepartamento() {
        return departamento;
    }

    @Override
    protected String getIdPessoa() {
        return String.valueOf(id); // O id do docente e o seu id interno
    }

    @Override
    public String toString() {
        return "Docente: ID = " + id + ", " + super.toString() + ", Departamento = " + departamento;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Docente))
            return false;

        Docente d = (Docente) o;
        return id == d.id;
    }
}

