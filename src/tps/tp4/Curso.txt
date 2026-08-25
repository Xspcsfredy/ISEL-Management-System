package tps.tp4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Curso implements Nomeavel, Identificavel {

    // Atributos
    private final String codigo;
    private final String nome;
    private final int duracaoAnos;
    private final List<String> codigosUcs; // Para nao criar dependencias circulares com UC

    // Construtor da classe
    public Curso(String codigo, String nome, int duracaoAnos) throws ValidacaoException {
        // 1. codigo == null: Verifica se a variável sequer existe na memoria
        // 2. codigo.trim().isEmpty(): So corre se o primeiro der falso ('codigo' existe). Limpa os espaços e vê se o texto é uma String vazia
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new ValidacaoException("Codigo de curso nao pode ser vazio.");
        }
        if (nome == null || nome.trim().isEmpty()) {
            throw new ValidacaoException("Nome de curso nao pode ser vazio.");
        }
        if (duracaoAnos <= 0 || duracaoAnos > 10) {
            throw new ValidacaoException("Duracao do curso invalida.");
        }

        this.codigo = codigo.trim().toUpperCase();
        this.nome = nome.trim();
        this.duracaoAnos = duracaoAnos;
        this.codigosUcs = new ArrayList<>();
    }

    public String getCodigo() {
        return codigo;
    }

    @Override
    public String getNome() {
        return nome;
    }

    public int getDuracaoAnos() {
        return duracaoAnos;
    }

    public List<String> getCodigosUcs() {
        return Collections.unmodifiableList(codigosUcs); // Faz uma copia read-only da lista de UCs
    }
    // Assim, mais ninguem pode adicionar ou apagar algo a esta lista, ou seja, é obrigado a usar o metodo associarUc
    // e passando pelas verificações de segurança

    // ADICIONAR UMA NOVA CADEIRA A ESTE CURSO
    public void associarUc(String codigoUc) throws ValidacaoException {

        // 1. barreira de segurança: não aceita codigos que nao existem ou em branco
        if (codigoUc == null || codigoUc.trim().isEmpty()) {
            throw new ValidacaoException("Codigo de UC invalido.");
        }

        // 2. Padronização: limpa espaços e mete em maiúsculas
        String chave = codigoUc.trim().toUpperCase();

        // 3. Verifica se a lista de codigos de Ucs NÃO contem esta chave
        if (!codigosUcs.contains(chave)) {
            codigosUcs.add(chave); // Se a chave não estiver na lista, adiciona a UC
        }
    }

    @Override
    public String getIdentificador() {
        return codigo;
    }

    @Override
    public String toString() {
        return "Curso " + codigo + ": Nome = " + nome + ", Duracao = " + duracaoAnos + " anos, UCs = " + codigosUcs.size();
    }
}

