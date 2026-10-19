package application;

import database.Conexao;
import entities.Cliente;
import entities.Pedido;
import entities.TipoPedido;
import repositories.PedidoRepositoryJDBC;
import services.PedidoService;
import services.PedidoServiceIMPL;
import util.Entrada;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Scanner;

public class Program {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Conexao.criarTabelas();
        PedidoService service = new PedidoServiceIMPL(new PedidoRepositoryJDBC());

        boolean rodando = true;

        while (rodando) {
            System.out.println("\n===== MENU =====");
            System.out.println("1 - Novo pedido");
            System.out.println("2 - Ver pedidos de frango");
            System.out.println("3 - Ver pedidos de carne assada");
            System.out.println("4 - Ver todos os pedidos");
            System.out.println("5 - Buscar pedido por nome");
            System.out.println("0 - Sair");
            System.out.print("Escolha: ");

            String opcao = sc.nextLine().trim();

            switch (opcao) {
                case "1":
                    novoPedido(sc, service);
                    break;
                case "2":
                    imprimir(service.listarPorTipo(TipoPedido.FRANGO));
                    imprimirTotal(service, TipoPedido.FRANGO);
                    break;
                case "3":
                    imprimir(service.listarPorTipo(TipoPedido.CARNE));
                    imprimirTotal(service, TipoPedido.CARNE);
                    break;
                case "4":
                    imprimir(service.listarTodos());
                    break;
                case "5":
                    System.out.print("Nome: ");
                    String busca = sc.nextLine();
                    imprimir(service.buscarPorNome(busca));
                    break;
                case "0":
                    rodando = false;
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
        }

        System.out.println("Programa encerrado.");
        sc.close();
    }

    private static void novoPedido(Scanner sc, PedidoService service) {
        System.out.print("Nome: ");
        String nome = sc.nextLine().trim();

        String telefone = null;
        while (telefone == null) {
            System.out.print("Telefone (opcional, Enter para pular): ");
            try {
                telefone = Entrada.telefone(sc.nextLine());
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        Cliente cliente = new Cliente(nome, telefone);

        TipoPedido tipo = null;
        while (tipo == null) {
            System.out.print("F para frango / C para carne assada: ");
            String op = sc.nextLine().trim().toUpperCase();
            if (op.equals("F")) {
                tipo = TipoPedido.FRANGO;
            } else if (op.equals("C")) {
                tipo = TipoPedido.CARNE;
            } else {
                System.out.println("Opção inválida, tente de novo.");
            }
        }

        LocalDate data = null;
        while (data == null) {
            System.out.print("Data de retirada (dd/MM/aaaa, Enter para hoje): ");
            String texto = sc.nextLine().trim();
            if (texto.isEmpty()) {
                data = LocalDate.now();
                continue;
            }
            try {
                data = Entrada.data(texto);
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        LocalTime horario = null;
        while (horario == null) {
            System.out.print("Horário de retirada (HH:mm): ");
            try {
                horario = Entrada.hora(sc.nextLine());
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        double quantidade = 0;
        while (quantidade <= 0) {
            System.out.print(tipo == TipoPedido.CARNE ? "Peso em kg (ex: 1,5): " : "Quantidade: ");
            try {
                String texto = sc.nextLine();
                quantidade = tipo == TipoPedido.CARNE ? Entrada.peso(texto) : Entrada.unidades(texto);
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        String observacao = "Sem observações";

        System.out.print("Deseja algum adicional ou observação? (S/N): ");
        String adicional = sc.nextLine().trim().toUpperCase();
        if (adicional.equals("S")) {
            System.out.print("Escreva: ");
            observacao = sc.nextLine().trim();
        }

        Pedido pedido = service.registrar(cliente, tipo, data, horario, quantidade, observacao);
        System.out.println("Pedido registrado: " + pedido);
    }

    private static void imprimir(List<Pedido> pedidos) {
        if (pedidos.isEmpty()) {
            System.out.println("Nenhum pedido encontrado.");
            return;
        }
        System.out.println();
        for (Pedido p : pedidos) {
            System.out.println(p);
        }
    }

    private static void imprimirTotal(PedidoService service, TipoPedido tipo) {
        double total = service.totalPorTipo(tipo, LocalDate.now());
        System.out.println("Falta preparar hoje (" + tipo + "): " + Pedido.formatarQuantidade(tipo, total));
    }
}
