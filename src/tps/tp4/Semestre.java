package tps.tp4;

// Classe enumerado para representar APENAS os dois semestres do ano letivo
public enum Semestre {
    S1(1),
    S2(2);

    // Criação do atributo para guardar o número do semestre
    private final int numero;

    // Construtor interno da enum. Corre automaticamente para associar o número a cada constante
    Semestre(int numero) {
        this.numero = numero;
    }

    // Metodo que permite outras classes consultarem o numero deste semestre
    public int getNumero() {
        return numero;
    }

    // Funcao auxiliar para converter um int (1-2) na constante correspondente
    public static Semestre fromNumero(int numero) throws ValidacaoException {
        if (numero == 1) {
            return S1;
        }
        if (numero == 2) {
            return S2;
        }
        throw new ValidacaoException("Semestre invalido. Use 1 ou 2."); // Se não for nem 1 nem 2
    }
}