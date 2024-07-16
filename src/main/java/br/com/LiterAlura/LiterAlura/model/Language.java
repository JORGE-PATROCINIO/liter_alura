package br.com.LiterAlura.LiterAlura.model;

public enum Language {
    ES("es"),
    EN("en"),
    FR("fr"),
    PT("pt");

    private String idioma;

    Language(String idioma) {
        this.idioma = idioma;
    }

    public static Language fromString(String text){
        for (Language language : Language.values()){
            if(language.idioma.equalsIgnoreCase(text)){
                return language;
            }
        }
        throw new IllegalArgumentException("Nenhum idioma encontrado: " + text);
    }

    public String getIdioma(){
        return this.idioma;
    }
}
