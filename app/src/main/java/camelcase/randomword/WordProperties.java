package camelcase.randomword;

public class WordProperties {
    private String mWord;
    private String mWordDefinition;

    public WordProperties() {
    }

    public WordProperties(String Word, String WordDefinition) {
        this.mWord = Word;
        this.mWordDefinition = WordDefinition;
    }

    public String getWord() {
        return mWord;
    }

    public void setWord(String Word) {
        this.mWord = Word;
    }

    public String getWordDefinition() {
        return mWordDefinition;
    }

    public void setWordDefinition(String wordDefinition) {
        this.mWordDefinition = wordDefinition;
    }
}
