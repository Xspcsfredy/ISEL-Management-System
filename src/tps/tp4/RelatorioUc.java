package tps.tp4;

public class RelatorioUc {

    private final String codigoUc;
    private final int inscritos;
    private final int avaliados;
    private final double media;
    private final int aprovados;
    private final int reprovados;

    public RelatorioUc(String codigoUc, int inscritos, int avaliados, double media, int aprovados, int reprovados) {
        this.codigoUc = codigoUc;
        this.inscritos = inscritos;
        this.avaliados = avaliados;
        this.media = media;
        this.aprovados = aprovados;
        this.reprovados = reprovados;
    }

    public String getCodigoUc() {
        return codigoUc;
    }

    public int getInscritos() {
        return inscritos;
    }

    public int getAvaliados() {
        return avaliados;
    }

    public double getMedia() {
        return media;
    }

    public int getAprovados() {
        return aprovados;
    }

    public int getReprovados() {
        return reprovados;
    }

    @Override
    public String toString() {
        return "Relatorio UC " + codigoUc + ": inscritos = " + inscritos + ", avaliados = " + avaliados + ", media = " + media + ", aprovados = " + aprovados + ", reprovados = " + reprovados;
    }
}
