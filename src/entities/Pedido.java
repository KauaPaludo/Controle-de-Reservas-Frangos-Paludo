// entities/Pedido.java
package entities;

import util.Entrada;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Locale;

public class Pedido {
    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");

    private final Integer id;
    private final Cliente cliente;
    private final TipoPedido tipo;
    private final LocalDate data;
    private final LocalTime horario;
    /** FRANGO: quantidade em unidades. CARNE: peso em kg. */
    private final double quantidade;
    private final String observacao;
    private final boolean retirado;

    /** Pedido novo (ainda sem id, não retirado). */
    public Pedido(Cliente cliente, TipoPedido tipo, LocalDate data, LocalTime horario,
                  double quantidade, String observacao) {
        this(null, cliente, tipo, data, horario, quantidade, observacao, false);
    }

    /** Pedido vindo do banco. */
    public Pedido(Integer id, Cliente cliente, TipoPedido tipo, LocalDate data, LocalTime horario,
                  double quantidade, String observacao, boolean retirado) {
        this.id = id;
        this.cliente = cliente;
        this.tipo = tipo;
        this.data = data;
        this.horario = horario;
        this.quantidade = quantidade;
        this.observacao = observacao;
        this.retirado = retirado;
    }

    public Integer getId() { return id; }
    public Cliente getCliente() { return cliente; }
    public TipoPedido getTipo() { return tipo; }
    public LocalDate getData() { return data; }
    public LocalTime getHorario() { return horario; }
    public double getQuantidade() { return quantidade; }
    public String getObservacao() { return observacao; }
    public boolean isRetirado() { return retirado; }

    /** "3 un" para frango, "1,50 kg" para carne assada. */
    public String getQuantidadeFormatada() {
        return formatarQuantidade(tipo, quantidade);
    }

    public static String formatarQuantidade(TipoPedido tipo, double valor) {
        if (tipo == TipoPedido.CARNE) {
            return String.format(PT_BR, "%.2f kg", valor);
        }
        return Math.round(valor) + " un";
    }

    @Override
    public String toString() {
        String telefone = cliente.getNumber().isEmpty() ? "sem telefone" : cliente.getNumber();
        String rotulo = tipo == TipoPedido.CARNE ? "Peso" : "Quantidade";
        return tipo + " - " + cliente.getName() + " (" + telefone + ")"
                + " - Retirada em " + data.format(Entrada.DATA)
                + " às " + horario + " - " + rotulo + ": " + getQuantidadeFormatada()
                + " - Observações/Adicionais: " + observacao
                + " - " + (retirado ? "RETIRADO" : "PENDENTE");
    }
}
