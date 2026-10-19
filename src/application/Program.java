package application;

import database.Conexao;
import entities.Cliente;
import entities.Pedido;
import entities.TipoPedido;
import repositories.PedidoRepository;
import repositories.PedidoRepositoryJDBC;
import services.PedidoService;
import services.PedidoServiceIMPL;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
                    System.out.println("Frangos: " + service.totalPorTipo(TipoPedido.FRANGO));
                    break;
                case "3":
                    imprimir(service.listarPorTipo(TipoPedido.CARNE));
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
        DateTimeFormatter fmt =DateTimeFormatter.ofPattern("HH:mm");

        System.out.print("Nome: ");
        String nome = sc.nextLine();
        String telefone;
        while (true) {
            System.out.print("Telefone (XX) XXXXX-XXXX: ");
            String entrada = sc.nextLine();
            telefone = entrada.replaceAll("\\D", ""); // mantém só os dígitos

            if (telefone.length() == 11) {
                break;
            }
            System.out.println("Número de telefone inválido");
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

        LocalTime horario = null;

        while (horario == null) {
            System.out.print("Horário de retirada (HH:mm): ");
            try {
                horario = LocalTime.parse(sc.nextLine().trim(), fmt);
            } catch (DateTimeParseException e) {
                System.out.println("Horário inválido. Use o formato HH:mm, por exemplo 18:30.");
            }
        }

        int quant = 0;
        while (quant <= 0) {
            System.out.print("Quantidade: ");
            try {
                quant = Integer.parseInt(sc.nextLine().trim());
                if (quant <= 0) {
                    System.out.println("A quantidade deve ser maior que zero.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Digite um número válido.");
            }
        }

        String Aob = "Sem observações";

        System.out.print("Deseja algum adicional ou observação? (S/N): ");
        String adicional = sc.nextLine().trim().toUpperCase();
        if (adicional.equals("S")) {
            System.out.print("Escreva: ");
            Aob = sc.nextLine().trim();
        }
        
        Pedido pedido = service.registrar(cliente, tipo, horario,quant,Aob);
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

}