package br.com.alura.screenmatch.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.alura.screenmatch.model.Serie;

@Repository
public interface SerieRepository extends JpaRepository<Serie, String> {

	Optional<Serie> findByTituloContainingIgnoreCase(String titulo);
	
	List<Serie> findAllByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(String ator, Double avaliacao);
	
	List<Serie> findTop5ByOrderByAvaliacaoDesc();

}
