package tps.tp4;

import java.time.LocalDate;

// Esta classe é um Value Object - serve para embrulhar e proteger dados, garantindo que seguem regras rígidas
public class AnoLetivo implements Identificavel {

    private final int anoInicio;

    // Construtor da classe
    public AnoLetivo(int anoInicio) throws ValidacaoException { // Avisa que este metodo pode falhar e lançar um erro
        if (anoInicio < 2000 || anoInicio > 2100) {
            throw new ValidacaoException("Ano de inicio do ano letivo invalido.");// Cria erro com a mensagem e "atira-o" para tras
        }
        this.anoInicio = anoInicio; // Se o ano passou no teste acima, o sistema guarda o valor no atributo
    }

    public int getAnoInicio() {
        return anoInicio;
    }

    public int getAnoFim() {
        return anoInicio + 1;
    }

    @Override
    public String getIdentificador() {
        return toString();
    }

    @Override
    public String toString() {
        return anoInicio + "/" + (anoInicio + 1);
    }

    // Regra de funcionamento:
    // O ano letivo começa sempre em setembro (mes 9)
    public static AnoLetivo atual() throws ValidacaoException {
        LocalDate hoje = LocalDate.now(); // Pergunta ao relógio do pc a data de hoje
        int ano = hoje.getYear(); // Extrai o ano civil
        int mes = hoje.getMonthValue(); // Extrai o numero do mes

        if (mes >= 9) {
            return new AnoLetivo(ano); // Arrancou novo ano letivo
        }
        return new AnoLetivo(ano - 1);
    }
}

