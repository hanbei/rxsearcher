package rxsearch.testing

config(country: "de") {
    searcher {
        github(name: "dummy1", repo: "hanbei/mockhttpserver")
        github(name: "dummy2", repo: "hanbei/rxsearcher")
    }
}