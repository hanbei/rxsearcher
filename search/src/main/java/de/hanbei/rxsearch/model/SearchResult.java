package de.hanbei.rxsearch.model;


public class SearchResult {

    private String url;
    private String title;
    private String icon;
    private String searchSource;

    protected SearchResult() {
        this(null, null, null);
    }

    public SearchResult(String url, String title, String searchSource) {
        this(url, title, searchSource, "");
    }

    public SearchResult(String url, String title, String searchSource, String icon) {
        this.url = url;
        this.title = title;
        this.searchSource = searchSource;
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

    public String getSearchSource() {
        return searchSource;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SearchResult that = (SearchResult) o;

        if (!getUrl().equals(that.getUrl())) return false;
        if (!getTitle().equals(that.getTitle())) return false;
        if (!getIcon().equals(that.getIcon())) return false;
        return getSearchSource().equals(that.getSearchSource());

    }

    @Override
    public int hashCode() {
        int result = getUrl().hashCode();
        result = 31 * result + getTitle().hashCode();
        result = 31 * result + getIcon().hashCode();
        result = 31 * result + getSearchSource().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "SearchResult{" +
                "url='" + url + '\'' +
                ", title='" + title + '\'' +
                ", icon='" + icon + '\'' +
                ", searchSource='" + searchSource + '\'' +
                '}';
    }
}
