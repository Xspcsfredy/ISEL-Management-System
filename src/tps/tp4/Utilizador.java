package tps.tp4;

public class Utilizador implements Identificavel, Nomeavel {

    // Atributos
    private final String username;
    private final String password;

    // Construtor usado internamente e na persistencia
    Utilizador(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Factory para criar utilizador a partir de password
    public static Utilizador criar(String username, String password) throws ValidacaoException {
        if (username == null || username.trim().isEmpty()) {
            throw new ValidacaoException("Nome de utilizador nao pode ser vazio.");
        }
        if (password == null || password.isEmpty()) {
            throw new ValidacaoException("Password nao pode ser vazia.");
        }

        String user = normalizarUsername(username);
        return new Utilizador(user, password);
    }

    // Factory usada no carregamento do XML - lê o que estava no XML e reconstroi para o user, mas é mais exigente
    // porque não pode vacilar com formatações de texto invisiveis que possam corromper as passes guardadas
    public static Utilizador criarComPassword(String username, String password) throws ValidacaoException {
        if (username == null || username.trim().isEmpty()) {
            throw new ValidacaoException("Nome de utilizador nao pode ser vazio.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new ValidacaoException("Password invalida.");
        }
        return new Utilizador(normalizarUsername(username), password);
    }

    public String getUsername() {
        return username;
    }

    public boolean validarPassword(String password) throws ValidacaoException {
        if (password == null) {
            throw new ValidacaoException("Password nao pode ser nula.");
        }
        return this.password.equals(password);
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String getIdentificador() {
        return username;
    }

    @Override
    public String getNome() {
        return username;
    }

    private static String normalizarUsername(String username) {
        // Guardamos sempre o username numa forma consistente para evitar problemas de maiusculas/minusculas
        return username.trim().toLowerCase();
    }

    @Override
    public String toString() {
        return "Utilizador: username = " + username;
    }
}
