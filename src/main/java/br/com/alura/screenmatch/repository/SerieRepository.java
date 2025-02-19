package br.com.alura.screenmatch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.alura.screenmatch.model.Serie;

@Repository
public interface SerieRepository extends JpaRepository<Serie, String> {

}
