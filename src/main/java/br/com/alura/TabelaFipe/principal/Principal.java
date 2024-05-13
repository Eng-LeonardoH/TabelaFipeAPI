package br.com.alura.TabelaFipe.principal;

import br.com.alura.TabelaFipe.model.Dados;
import br.com.alura.TabelaFipe.model.Modelos;
import br.com.alura.TabelaFipe.model.Veiculo;
import br.com.alura.TabelaFipe.service.ConsumoApi;
import br.com.alura.TabelaFipe.service.ConverteDados;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {
    private final String URL_BASE = "https://parallelum.com.br/fipe/api/v1/";

    Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumoApi = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();


    public void exibeMenu(){
        var menu = """
                digite uma das opções para consulta:
                - moto
                - carro
                - caminhão
                Digite: """;

        System.out.println(menu);
        var opcao = leitura.nextLine();

        String endereco;

        if (opcao.contains("car")) {
            endereco = URL_BASE + "carros/marcas";
        } else if (opcao.contains("mot")) {
            endereco = URL_BASE + "motos/marcas";
        } else {
            endereco = URL_BASE + "caminhoes/marcas";
        }

        var json = consumoApi.obterDados(endereco);
        System.out.println(json);

        var marcas = conversor.obterLista(json, Dados.class);

        marcas.stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);

        System.out.println("\nInforme o código do veículo: ");
        var codMarca = leitura.nextLine();

        endereco = endereco + "/" + codMarca + "/modelos";
        json = consumoApi.obterDados(endereco);
        var modeloLista = conversor.obterDados(json, Modelos.class);
        modeloLista.modelos().stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);
        System.out.println("\nDigite um trecho do nome do carro a ser buscado: ");
        var nomeVeiculo = leitura.nextLine();

        List<Dados> modelosFiltrados = modeloLista.modelos().stream()
                .filter(m -> m.nome().toLowerCase().contains(nomeVeiculo.toLowerCase()))
                .collect(Collectors.toList());
        System.out.println("\nModelos filtrados: ");
        modelosFiltrados.forEach(System.out::println);

        System.out.println("\nDigite o código do modelo: ");
        var codigoModelo = leitura.nextLine();
        endereco = endereco + "/" + codigoModelo + "/anos";

        json = consumoApi.obterDados(endereco);
        List <Dados> anos = conversor.obterLista(json, Dados.class);
        List <Veiculo> veiculos = new ArrayList<>();

            for (int i = 0; i < anos.size(); i++) {
                var enderecoAnos = endereco + "/" + anos.get(i).codigo();
                json = consumoApi.obterDados(enderecoAnos);
                Veiculo veiculo = conversor.obterDados(json, Veiculo.class);
                veiculos.add(veiculo);
            }
        System.out.println("Todos os veiculos filtrados com avaliações por ano: ");
        veiculos.forEach(System.out::println);

    }
}
