// services/PedidoServiceIMPL.java
package services;

import entities.Cliente;
import entities.Pedido;
import entities.TipoPedido;
import repositories.PedidoRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class PedidoServiceIMPL implements PedidoService {
    private final PedidoRepository repository;

    public PedidoServiceIMPL(PedidoRepository repository) {
        this.repository = repository;
    }

    @Override
    public Pedido registrar(Cliente cliente, TipoPedido tipo, LocalDate data, LocalTime horario,
                            double quantidade, String observacao) {
        Pedido pedido = new Pedido(cliente, tipo, data, horario, quantidade, observacao);
        repository.salvar(pedido);
        return pedido;
    }

    @Override
    public List<Pedido> listarPorTipo(TipoPedido tipo) {
        List<Pedido> resultado = new ArrayList<>();
        for (Pedido p : repository.listarTodos()) {
            if (p.getTipo() == tipo) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    @Override
    public List<Pedido> listarTodos() {
        return repository.listarTodos();
    }

    @Override
    public List<Pedido> buscarPorNome(String nome) {
        List<Pedido> resultado = new ArrayList<>();
        for (Pedido p : repository.listarTodos()) {
            if (p.getCliente().getName().equalsIgnoreCase(nome.trim())) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    @Override
    public double totalPorTipo(TipoPedido tipo, LocalDate data) {
        double total = 0;
        for (Pedido p : repository.listarTodos()) {
            if (p.getTipo() == tipo && !p.isRetirado() && (data == null || p.getData().equals(data))) {
                total += p.getQuantidade();
            }
        }
        return total;
    }

    @Override
    public void marcarRetirado(int id, boolean retirado) {
        repository.marcarRetirado(id, retirado);
    }
}
