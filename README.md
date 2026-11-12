# Controle de Reservas - Frangos Paludo

Sistema para registrar e acompanhar **pedidos de retirada** de **frango** (por unidade) e **carne assada** (por peso em kg). Ele mostra o que ainda falta preparar em cada dia e permite marcar os pedidos como retirados.

O projeto tem duas versões independentes:

- **Aplicativo Java** (console e janela Swing), com os dados guardados em um banco SQLite.
- **PWA** (pasta `docs/`), uma página web que funciona no navegador e pode ser instalada em um tablet, inclusive sem internet.
- **LINK** https://kauapaludo.github.io/Controle-de-Reservas-Frangos-Paludo/ 

> As duas versões **não compartilham dados**: o Java usa o arquivo `pedidos.db` e o PWA usa o armazenamento do navegador do aparelho.

## Funcionalidades

- Cadastro de pedido com nome, telefone (opcional), tipo, data, horário de retirada, quantidade ou peso e observação
- **Frango** em unidades, aceitando meio frango (por exemplo `1,5`); **carne assada** em kg (por exemplo `1,5`)
- Lista de pedidos com filtros por dia, status (pendentes, retirados ou todos), tipo e nome
- Total "falta preparar" por tipo, considerando só os pedidos ainda não retirados
- Marcar pedido como retirado e desfazer a retirada
- Validação do que é digitado (telefone com 10 ou 11 dígitos, data `dd/MM/aaaa`, horário `HH:mm`)

Recursos extras do PWA: excluir pedido, salvar e restaurar backup em arquivo JSON, limpar pedidos antigos já retirados e uso offline.

## Tecnologias

- Java 17
- Maven
- SQLite (driver `sqlite-jdbc`)
- Java Swing (interface gráfica)
- JUnit 5 (testes)
- HTML, CSS e JavaScript puros, com service worker (PWA)

## Estrutura do projeto

```
.
├── docs/                    # PWA (index.html, manifest, service worker, ícones)
├── src/
│   ├── application/         # Program (console) e ProgramGUI (janela)
│   ├── database/            # Conexao: conexão e criação das tabelas
│   ├── entities/            # Cliente, Pedido, TipoPedido
│   ├── repositories/        # PedidoRepository e implementação JDBC
│   ├── services/            # PedidoService e PedidoServiceIMPL
│   ├── util/                # Entrada: leitura e validação do que o usuário digita
│   └── view/                # TelaPedidos (Swing)
├── test/                    # Testes automáticos (JUnit 5)
└── pom.xml
```

O código-fonte fica direto em `src` (e não em `src/main/java`), e os testes ficam em `test`. Essa configuração está no `pom.xml`.

## Como executar o aplicativo Java

### Requisitos

- JDK 17 ou superior
- Maven (o IntelliJ IDEA já inclui)

### Passos

1. Clone o repositório:

   ```bash
   git clone https://github.com/KauaPaludo/Controle-de-Reservas-Frangos-Paludo.git
   ```

2. Abra a pasta no IntelliJ IDEA e aguarde o Maven baixar as dependências.
3. Execute uma das classes:
   - `application.ProgramGUI`: abre a janela com formulário, lista e totais.
   - `application.Program`: versão de console, com menu de texto.

O banco `pedidos.db` é criado automaticamente na pasta de onde o programa é iniciado. Ele não vai para o Git (está no `.gitignore`).

### Usar outro arquivo de banco

Por padrão o banco é `pedidos.db`. Para usar outro (por exemplo, para testar sem mexer nos pedidos reais), informe a propriedade `pedidos.db` nas opções da JVM:

```
-Dpedidos.db=teste.db
```

## Testes

Os testes cobrem a validação das entradas, a formatação das quantidades e o serviço de pedidos (usando um banco temporário, que não afeta o `pedidos.db`).

No IntelliJ: aba **Maven** > **Lifecycle** > **test**. Pelo terminal, com o Maven instalado:

```bash
mvn test
```

## PWA para tablet

A pasta `docs/` contém o aplicativo web. Para usá-lo:

1. Publique a pasta `docs/` (por exemplo, com o **GitHub Pages**, escolhendo a branch `main` e a pasta `/docs`).
2. Abra o endereço no navegador do tablet.
3. Use a opção de adicionar à tela inicial / instalar aplicativo.

Depois de aberto uma vez com internet, o app também funciona offline. Os pedidos ficam guardados **no navegador do tablet**, então use **Salvar backup** de vez em quando para guardar uma cópia em arquivo.

Para testar no computador sem misturar com os dados do tablet, rode um servidor local dentro da pasta `docs/` (por exemplo, `python -m http.server 8000`) e abra `http://localhost:8000`.

## Autor

Desenvolvido por [KauaPaludo](https://github.com/KauaPaludo).
