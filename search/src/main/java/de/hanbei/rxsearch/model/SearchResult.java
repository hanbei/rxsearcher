package de.hanbei.rxsearch.model;


public class SearchResult {

    private String url;
    private String text;
    private String title;
    private String icon;

    protected SearchResult() {
        this(null, null, null);
    }

    public SearchResult(String url, String text, String title) {
        this(url, text, title, null);
    }

    public SearchResult(String url, String text, String title, String icon) {
        this.url = url;
        this.text = text;
        this.title = title;
        this.icon = icon;
    }

    public String getUrl() {
        return url;
    }

    public String getText() {
        return text;
    }

    public String getTitle() {
        return title;
    }

    public String getIcon() {
        return icon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SearchResult that = (SearchResult) o;

        if (!getUrl().equals(that.getUrl())) return false;
        if (!getText().equals(that.getText())) return false;
        if (!getTitle().equals(that.getTitle())) return false;
        return getIcon().equals(that.getIcon());

    }

    @Override
    public int hashCode() {
        int result = getUrl().hashCode();
        result = 31 * result + getText().hashCode();
        result = 31 * result + getTitle().hashCode();
        result = 31 * result + getIcon().hashCode();
        return result;
    }
}
