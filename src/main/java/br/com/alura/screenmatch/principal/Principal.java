package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;
import ch.qos.logback.core.property.ResourceExistsPropertyDefiner;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {

	private Scanner leitura = new Scanner(System.in);
	private ConsumoApi consumo = new ConsumoApi();
	private ConverteDados conversor = new ConverteDados();
	private final String ENDERECO = "https://www.omdbapi.com/?t=";
	private final String API_KEY = "&apikey=6585022c";
	private List<Serie> series = new ArrayList<Serie>();
	private SerieRepository repositorio;

	public Principal(SerieRepository repositorio) {
		this.repositorio = repositorio;
	}

	public void exibeMenu() {
		var opcao = -1;
		while (opcao != 0) {

			var menu = """
					1 - Buscar séries
					2 - Buscar episódios
					3 - Listar séries buscadas
					4 - Buscar por ator
					5 - Top 5 Séries
					
					0 - Sair
					""";

			System.out.println(menu);
			opcao = leitura.nextInt();
			leitura.nextLine();

			switch (opcao) {
			case 1:
				System.out.println();
				buscarSerieWeb();
				break;
			case 2:
				System.out.println();
				buscarEpisodioPorSerie();
				break;
			case 3:
				System.out.println();
				listarSeriesBuscadas();
				break;
			case 4:
				System.out.println();
				buscarSériePorAtor();
				break;
			case 5:
				System.out.println();
				listarTop5Series();
				break;
			case 0:
				System.out.println("Saindo...");
				break;
			default:
				System.out.println("Opção inválida");
			}
		}
	}

	private void buscarSerieWeb() {
		DadosSerie dados = getDadosSerie();
		repositorio.save(new Serie(dados));
		System.out.println(dados);
	}

	private DadosSerie getDadosSerie() {
		System.out.println("Digite o nome da série para busca");
		var nomeSerie = leitura.nextLine();
		System.out.println();
		var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
		DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
		return dados;
	}

	private void buscarEpisodioPorSerie() {
		listarSeriesBuscadas();
		System.out.println("Digite uma série pelo nome: ");
		String tituloSerie = leitura.nextLine();

		Optional<Serie> buscarSerie = repositorio.findByTituloContainingIgnoreCase(tituloSerie);

		List<DadosTemporada> temporadas = new ArrayList<>();
		if (buscarSerie.isPresent()) {
			Serie serieBuscada = buscarSerie.get();
			for (int i = 1; i <= serieBuscada.getTotalTemporadas(); i++) {
				var json = consumo
						.obterDados(ENDERECO + serieBuscada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
				DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
				temporadas.add(dadosTemporada);
			}
			temporadas.forEach(System.out::println);

			List<Episodio> episodios = temporadas.stream()
					.flatMap(d -> d.episodios().stream().map(ep -> new Episodio(d.numero(), ep)))
					.collect(Collectors.toList());

			serieBuscada.setEpisodios(episodios);

			repositorio.save(serieBuscada);

		} else {
			System.out.println("Série não encontrada");
		}

	}

	private void listarSeriesBuscadas() {

		series = repositorio.findAll();

		series.stream().sorted(Comparator.comparing(Serie::getGenero)).forEach(System.out::println);

	}

	private void buscarSériePorAtor() {
		System.out.println("Digite o nome do ator: ");
		String nomeAtor = leitura.nextLine();

		System.out.println("Avaliações a partir de que valor?: ");
		Double avaliacao = leitura.nextDouble();
		System.out.println();

		List<Serie> seriesBuscadas = repositorio
				.findAllByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(nomeAtor, avaliacao);

		if (!seriesBuscadas.isEmpty()) {
			System.out.println("Séries em que " + nomeAtor + " trabalhou:");
			seriesBuscadas.forEach(s -> {
				System.out.println(s.getTitulo() + " avaliação: " + s.getAvaliacao());
			});
			System.out.println();
		} else {
			System.out.println("Nenhuma série encontrada!");
		}

	}
	
	private void listarTop5Series() {
		System.out.println();
		
		List<Serie> top5Series = repositorio.findTop5ByOrderByAvaliacaoDesc();
		
		if (!top5Series.isEmpty()) {
			System.out.println("Top 5 séries:");
			top5Series.forEach(s -> {
				System.out.println(s.getTitulo() + ", avaliação: " + s.getAvaliacao());
			});
			System.out.println();
		} else {
			System.out.println("Nenhuma série encontrada");
		}
	}

}
