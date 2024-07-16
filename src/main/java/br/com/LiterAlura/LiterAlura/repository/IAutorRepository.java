package br.com.LiterAlura.LiterAlura.repository;

import br.com.LiterAlura.LiterAlura.model.Autor;
import br.com.LiterAlura.LiterAlura.model.Language;
import br.com.LiterAlura.LiterAlura.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface IAutorRepository extends JpaRepository<Autor, Long> {
    @Query("SELECT a FROM Livro l JOIN l.autor a WHERE a.name LIKE %:name%")
    Optional<Autor> buscarAutorPorNome(String name);

    @Query("SELECT l FROM Livro l JOIN l.autor a WHERE l.titulo LIKE %:name%")
    Optional<Livro> buscarLivroPorNome(String name);


    @Query("SELECT l FROM Autor a JOIN a.livros l")
    List<Livro> buscarTodosOsLivros();

    @Query("SELECT a FROM Autor a WHERE a.falecimento > :data")
    List<Autor> buscarAutoresVivos(Integer data);

    @Query("SELECT l FROM Autor a JOIN a.livros l WHERE l.language = :idioma ")
    List<Livro> buscarLivrosPorIdioma(Language idioma);

    @Query("SELECT l FROM Autor a JOIN a.livros l ORDER BY l.download DESC LIMIT 10")
    List<Livro> top10Livros();

    @Query("SELECT a FROM Autor a WHERE a.nascimento = :data")
    List<Autor> ListarAutoresPorNascimento(Integer data);

    @Query("SELECT a FROM Autor a WHERE a.falecimento = :data")
    List<Autor> ListarAutoresPorFalecimento(Integer data);
}
