// entities/Pedido.java
package entities;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class Pedido{
    private Cliente cliente;
    private TipoPedido tipo;
    protected LocalTime horario;
    private Integer quant;
    private String Aob;
    private double peso;

    public Pedido(Cliente cliente, TipoPedido tipo, LocalTime horario, Integer quant,String Aob) {
        this.cliente = cliente;
        this.tipo = tipo;
        this.horario = horario;
        this.quant = quant;
        this.Aob = Aob;
    }
    public Pedido(Cliente cliente, TipoPedido tipo, LocalTime horario, double peso,String Aob) {
        this.cliente = cliente;
        this.tipo = tipo;
        this.horario = horario;
        this.peso=peso;
        this.Aob = Aob;
    }

    private Integer id;
    private boolean retirado;

    public Pedido(Integer id, Cliente cliente, TipoPedido tipo, LocalTime horario,
                  Integer quant, String Aob, boolean retirado) {
        this(cliente, tipo, horario, quant, Aob);
        this.id = id;
        this.retirado = retirado;
    }

    public Integer getId() { return id; }
    public boolean isRetirado() { return retirado; }

    public Cliente getCliente() { return cliente; }
    public TipoPedido getTipo() { return tipo; }
    public LocalTime getHorario() { return horario; }
    public Integer getQuant(){ return quant; }
    public String getAob() {
        return Aob;
    }

    @Override
    public String toString() {
        return tipo + " - " + cliente.getName() + " (" + cliente.getNumber() + ")"
                + " - Retirada às " + horario + " - Quantidade: " + quant
                + " - Observações/Adicionais: " + Aob
                + " - " + (retirado ? "RETIRADO" : "PENDENTE");
    }

}