package br.com.alura.screenmatch.service;

import java.net.URLEncoder;

import br.com.alura.screenmatch.model.RespostaTraduzida;

public class TraducaoService {
	public static String traduzir(String texto) {
		try {
			String endereco = "https://api.mymemory.translated.net/get";
			String linguagem = "en|pt";

			String textoCodificado = URLEncoder.encode(texto, "UTF-8");
			String linguagemCodificada = URLEncoder.encode(linguagem, "UTF-8");

			String url = endereco + "?q=" + textoCodificado + "&langpair=" + linguagemCodificada;
			ConsumoApi consumo = new ConsumoApi();
			ConverteDados conversor = new ConverteDados();

			var json = consumo.obterDados(url);
			var respostaTraduzida = conversor.obterDados(json, RespostaTraduzida.class);

			return respostaTraduzida.resposta().traducao();
		} catch (Exception e) {
			e.printStackTrace();
			return texto;
		}
	}
}
