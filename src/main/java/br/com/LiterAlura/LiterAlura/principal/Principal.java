package br.com.LiterAlura.LiterAlura.principal;

import br.com.LiterAlura.LiterAlura.model.*;
import br.com.LiterAlura.LiterAlura.repository.IAutorRepository;
import br.com.LiterAlura.LiterAlura.service.ConsumoAPI;
import br.com.LiterAlura.LiterAlura.service.ConverterDados;

import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {
    private Scanner input = new Scanner(System.in);
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConverterDados conversor = new ConverterDados();
    private String URL_BASE = "https://gutendex.com/books/";
    private IAutorRepository repository;

    public Principal(IAutorRepository repository){
        this.repository = repository;
    }

    public void mostrarMenu() {
        var opcao = -1;
        var menu = """
                #####################################################
                Selecione uma opção:
                1 - Buscar livros por titulo
                2 - Listar livros cadastrados
                3 - Listar autores cadastrados
                4 - Listar autores vivos por ano específico
                5 - Listar livros por idioma
                6 - Gerar estatísticas
                7 - Top 10 livros
                8 - Buscar autor por nome
                9 - Listar outros autores 
                0 - Sair
                """;
        while (opcao != 0) {
            System.out.println(menu);
            try {
                opcao = Integer.valueOf(input.nextLine());
                switch (opcao) {
                    case 1:
                        buscarLivroPorTitulo();
                        break;
                    case 2:
                        listarLivrosCadastrados();
                        break;
                    case 3:
                        listarAutoresCadastrados();
                        break;
                    case 4:
                        listarAutoresVivos();
                        break;
                    case 5:
                        listarLivrosPorIdioma();
                        break;
                    case 6:
                        gerarEstatisticas();
                        break;
                    case 7:
                        top10Livros();
                        break;
                    case 8:
                        buscarAutorPorNome();
                        break;
                    case 9:
                        listarAutores();
                        break;
                    case 0:
                        System.out.println("Obrigado por usar LiterAlura");
                        System.out.println("Encerrando o sistema...");
                        break;
                    default:
                        System.out.println("Opção inválida");
                        break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Opção inválida: " + e.getMessage());

            }
        }
    }

    public void buscarLivroPorTitulo(){
        System.out.println("Digite o nome do livro que deseja buscar:");
        var nome = input.nextLine();
        var json = consumoAPI.obterDados(URL_BASE + "?search=" + nome.replace(" ","+"));
        var dados = conversor.obterDados(json, Dados.class);
        Optional<DadosLivro> livroBuscado = dados.livros().stream()
                .findFirst();
        if(livroBuscado.isPresent()){
            System.out.println(
                    "\n----- EXEMPLARES -----" +
                            "\nTitulo: " + livroBuscado.get().titulo() +
                            "\nAutor: " + livroBuscado.get().autores().stream()
                            .map(a -> a.nome()).limit(1).collect(Collectors.joining())+
                            "\nIdioma: " + livroBuscado.get().languages().stream().collect(Collectors.joining()) +
                            "\nDownloads: " + livroBuscado.get().download() +
                            "\n**************************************************\n"
            );

            try{
                List<Livro> livroEncontrado = livroBuscado.stream().map(a -> new Livro(a)).collect(Collectors.toList());
                Autor autorAPI = livroBuscado.stream().
                        flatMap(l -> l.autores().stream()
                                .map(a -> new Autor(a)))
                        .collect(Collectors.toList()).stream().findFirst().get();
                Optional<Autor> autorBD = repository.buscarAutorPorNome(livroBuscado.get().autores().stream()
                        .map(a -> a.nome())
                        .collect(Collectors.joining()));
                Optional<Livro> livroOptional = repository.buscarLivroPorNome(nome);
                if (livroOptional.isPresent()) {
                    System.out.println("Livro já se encontra na base de dados!");
                } else {
                    Autor autor;
                    if (autorBD.isPresent()) {
                        autor = autorBD.get();
                        System.out.println("Autor já se encontra na base de dados!");
                    } else {
                        autor = autorAPI;
                        repository.save(autor);
                    }
                    autor.setLivros(livroEncontrado);
                    repository.save(autor);
                }
            } catch(Exception e) {
                System.out.println("Atenção! " + e.getMessage());
            }
        } else {
            System.out.println("Livro não encontrado na base de dados!");
        }
    }

    public void listarLivrosCadastrados(){
        List<Livro> livros = repository.buscarTodosOsLivros();
        livros.forEach(l -> System.out.println(
                "----- EXEMPLARES -----" +
                        "\nTitulo: " + l.getTitulo() +
                        "\nAutor: " + l.getAutor().getNome() +
                        "\nIdioma: " + l.getLanguage().getIdioma() +
                        "\nDownload: " + l.getDownload() +
                        "\n*******************************************\n"
        ));
    }

    public void listarAutoresCadastrados(){
        List<Autor> autores = repository.findAll();
        System.out.println();
        autores.forEach(l-> System.out.println(
                "Autor: " + l.getNome() +
                        "\nData de nascimento: " + l.getNascimento() +
                        "\nData da morte: " + l.getFalecimento() +
                        "\nExemplares: " + l.getLivros().stream()
                        .map(t -> t.getTitulo()).collect(Collectors.toList()) + "\n"
        ));
    }

    public void listarAutoresVivos(){
        System.out.println("Digite o ano  dos autores vivos que deseja listar:");
        try{
            var data = Integer.valueOf(input.nextLine());
            List<Autor> autores = repository.buscarAutoresVivos(data);
            if(!autores.isEmpty()){
                System.out.println();
                autores.forEach(a -> System.out.println(
                        "Autor: " + a.getNome() +
                                "\nData de nascimento: " + a.getNascimento() +
                                "\nData da morte: " + a.getFalecimento() +
                                "\nExemplares: " + a.getLivros().stream()
                                .map(l -> l.getTitulo()).collect(Collectors.toList()) + "\n"
                ));
            } else{
                System.out.println("Não constam autores vivos com o ano buscado na base de dados!");
            }
        } catch(NumberFormatException e){
            System.out.println("Digite ano válido" + e.getMessage());
        }
    }

    public void listarLivrosPorIdioma(){
        var menu = """
                Digite o idioma desejado:
                es - espanhol
                en - inglês
                fr - francês
                pt - português
                """;
        System.out.println(menu);
        var idioma = input.nextLine();
        if(idioma.equalsIgnoreCase("es") || idioma.equalsIgnoreCase("en") ||
                idioma.equalsIgnoreCase("fr") || idioma.equalsIgnoreCase("pt")){
            Language language = Language.fromString(idioma);
            List<Livro> livros = repository.buscarLivrosPorIdioma(language);
            if(livros.isEmpty()){
                System.out.println("Não exite livro com esse idioma!");
            } else{
                System.out.println();
                livros.forEach(l -> System.out.println(
                        "----- EXEMPLARES -----" +
                                "\nTitulo: " + l.getTitulo() +
                                "\nAutor: " + l.getAutor().getNome() +
                                "\nIdioma: " + l.getLanguage().getIdioma() +
                                "\nDownload: " + l.getDownload() +
                                "\n*******************************************\n"
                ));
            }
        } else{
            System.out.println("Idioma inválido");
        }
    }

    public void gerarEstatisticas(){
        var json = consumoAPI.obterDados(URL_BASE);
        var dados = conversor.obterDados(json, Dados.class);
        IntSummaryStatistics est = dados.livros().stream()
                .filter(l -> l.download() > 0)
                .collect(Collectors.summarizingInt(DadosLivro::download));
        Integer media = (int) est.getAverage();
        System.out.println("\n----- ESTATÍSTICAS -----");
        System.out.println("Número de downloads: " + media);
        System.out.println("Número máximo de downloads: " + est.getMax());
        System.out.println("Número mínimo de download: " + est.getMin());
        System.out.println("Número de cadastros usados para calcular as estatísticas: " + est.getCount());
        System.out.println("******************************************\n");
    }

    public void top10Livros(){
        List<Livro> livros = repository.top10Livros();
        System.out.println();
        livros.forEach(l -> System.out.println(
                "----- EXEMPLARES -----" +
                        "\nTitulo: " + l.getTitulo() +
                        "\nAutor: " + l.getAutor().getNome() +
                        "\nIdioma: " + l.getLanguage().getIdioma() +
                        "\nDownload: " + l.getDownload() +
                        "\n*********************************************\n"
        ));
    }

    public void buscarAutorPorNome(){
        System.out.println("Digite o nome do autor que deseja buscar:");
        var nome = input.nextLine();
        Optional<Autor> autor = repository.buscarAutorPorNome(nome);
        if(autor.isPresent()){
            System.out.println(
                    "\nAutor: " + autor.get().getNome() +
                            "\nData de nascimento: " + autor.get().getNascimento() +
                            "\nData de morte: " + autor.get().getFalecimento() +
                            "\nExemplares: " + autor.get().getLivros().stream()
                            .map(l -> l.getTitulo()).collect(Collectors.toList()) + "\n"
            );
        } else {
            System.out.println("Autor não consta na base de dados!");
        }
    }

    public void listarAutores(){
        var menu = """
                Escolha uma opção pelo número:
                1 - Listar autores por ano de nascimento
                2 - Listar autores por ano da morte
                """;
        System.out.println(menu);
        try{
            var opcao = Integer.valueOf(input.nextLine());
            switch (opcao){
                case 1:
                    ListarAutoresPorNascimento();
                    break;
                case 2:
                    ListarAutoresPorFalecimento();
                    break;
                default:
                    System.out.println("Opcão inválida!");
                    break;
            }
        } catch (NumberFormatException e) {
            System.out.println("Opção inválida: " + e.getMessage());
        }
    }

    public void ListarAutoresPorNascimento(){
        System.out.println("Digite o ano de nascimento:");
        try{
            var nascimento = Integer.valueOf(input.nextLine());
            List<Autor> autores = repository.ListarAutoresPorNascimento(nascimento);
            if(autores.isEmpty()){
                System.out.println("Não existem autores com essa data de nascimento " + nascimento);
            } else {
                System.out.println();
                autores.forEach(a -> System.out.println(
                        "Autor: " + a.getNome() +
                                "\nData de nascimento: " + a.getNascimento() +
                                "\nData da morte: " + a.getFalecimento() +
                                "\nExemplares: " + a.getLivros().stream().map(l -> l.getTitulo()).collect(Collectors.toList()) + "\n"
                ));
            }
        } catch (NumberFormatException e){
            System.out.println("Ano inválido: " + e.getMessage());
        }
    }

    public void ListarAutoresPorFalecimento(){
        System.out.println("Digite o ano de falecimento:");
        try{
            var falecimento = Integer.valueOf(input.nextLine());
            List<Autor> autores = repository.ListarAutoresPorFalecimento(falecimento);
            if(autores.isEmpty()){
                System.out.println("Não existem autores com essa data de falecimento " + falecimento);
            } else {
                System.out.println();
                autores.forEach(a -> System.out.println(
                        "Autor: " + a.getNome() +
                                "\nData de nascimento: " + a.getNascimento() +
                                "\nData da morte: " + a.getFalecimento() +
                                "\nExemplares: " + a.getLivros().stream().map(l -> l.getTitulo()).collect(Collectors.toList()) + "\n"
                ));
            }
        } catch (NumberFormatException e) {
            System.out.println("Opção inválida: " + e.getMessage());
        }
    }
}
