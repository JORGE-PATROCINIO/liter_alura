package br.com.LiterAlura.LiterAlura.service;

public interface IConverterDados {
    <T> T obterDados(String json, Class<T> classe);
}
