package br.com.LiterAlura.LiterAlura.model;

import jakarta.persistence.*;

import java.util.stream.Collectors;

@Entity
@Table(name = "livros")
public class Livro {
    @Id
    private Long id;
    private String titulo;
    @Enumerated(EnumType.STRING)
    private Language language;
    private String copyright;
    private Integer download;
    @ManyToOne
    private Autor autor;

    public Livro() {
    }

    public Livro(DadosLivro livro){
        this.id = livro.id();
        this.titulo = livro.titulo();
        this.language = Language.fromString(livro.languages().stream()
                .limit(1).collect(Collectors.joining()));
        this.copyright = livro.copyright();
        this.download = livro.download();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public Integer getDownload() {
        return download;
    }

    public void setDownload(Integer download) {
        this.download = download;
    }

    public Autor getAutor() {
        return autor;
    }

    public void setAutor(Autor autor) {
        this.autor = autor;
    }

    @Override
    public String toString() {
        return
                "id=" + id +
                        ", titulo='" + titulo + '\'' +
                        ", language=" + language +
                        ", copyright='" + copyright + '\'' +
                        ", download=" + download +
                        ", autor=" + autor;
    }
}
