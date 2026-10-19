// services/PedidoServiceImpl.java
package services;

import entities.Cliente;
import entities.Pedido;
import entities.TipoPedido;
import repositories.PedidoRepository;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class PedidoServiceIMPL implements PedidoService {
    private final PedidoRepository repository;

    public PedidoServiceIMPL(PedidoRepository repository) {
        this.repository = repository;
    }

    @Override
    public Pedido registrar(Cliente cliente, TipoPedido tipo, LocalTime horario, int quant, String aob) {
        Pedido pedido = new Pedido(cliente, tipo, horario, quant,aob);
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
    public int totalPorTipo(TipoPedido tipo) {
        int total = 0;
        for (Pedido p : repository.listarTodos()) {
            if (p.getTipo() == tipo && !p.isRetirado()) {
                total += p.getQuant();
            }
        }
        return total;
    }
    @Override
    public void marcarRetirado(int id, boolean retirado) {
        repository.marcarRetirado(id, retirado);
    }

}
