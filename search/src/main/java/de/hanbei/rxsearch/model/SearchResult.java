package de.hanbei.rxsearch.model;


public class SearchResult {

    private String url;
    private String title;
    private String icon;

    protected SearchResult() {
        this(null, null, null);
    }

    public SearchResult(String url, String title) {
        this(url, title, "");
    }

    public SearchResult(String url, String title, String icon) {
        this.url = url;
        this.title = title;
        this.icon = icon;
    }

    public String getUrl() {
        return url;
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

        if (getUrl() != null ? !getUrl().equals(that.getUrl()) : that.getUrl() != null) return false;
        if (getTitle() != null ? !getTitle().equals(that.getTitle()) : that.getTitle() != null) return false;
        return getIcon() != null ? getIcon().equals(that.getIcon()) : that.getIcon() == null;

    }

    @Override
    public int hashCode() {
        int result = getUrl() != null ? getUrl().hashCode() : 0;
        result = 31 * result + (getTitle() != null ? getTitle().hashCode() : 0);
        result = 31 * result + (getIcon() != null ? getIcon().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SearchResult{" +
                "url='" + url + '\'' +
                ", title='" + title + '\'' +
                ", icon='" + icon + '\'' +
                '}';
    }
}
