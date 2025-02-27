package br.com.alura.screenmatch.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.alura.screenmatch.model.Categoria;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.model.Serie;

@Repository
public interface SerieRepository extends JpaRepository<Serie, String> {

	Optional<Serie> findByTituloContainingIgnoreCase(String titulo);
	
	List<Serie> findAllByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(String ator, Double avaliacao);
	
	List<Serie> findTop5ByOrderByAvaliacaoDesc();
	
	List<Serie> findAllByGenero(Categoria categoria);
	
	List<Serie> findAllByTotalTemporadasLessThanEqualAndAvaliacaoGreaterThanEqual(Integer numTemp, Double avaliacao);
	
	@Query("select s from Serie s where s.totalTemporadas <= :numTemp AND s.avaliacao >= :avaliacao")
	List<Serie> buscarTotalTemporadasAvaliacao(Integer numTemp, Double avaliacao);
	
	@Query("SELECT e FROM Serie s JOIN s.episodios e WHERE e.titulo ILIKE %:trecho%")
	List<Episodio> buscarEpisodioPorTrecho(String trecho);


}
