package test_app.staging

def bla = fred(name: "fred", baseUrl: "hanbei/mockhttpserver")

config(country: "de") {
    searcher {
        zoom(name: "zoom", baseUrl: "http://example.com/zoom")
        webhose(name: "webhose", key: "71asda23")
        github(name: "github", repo: "hanbei/mockhttpserver")
        fred(name: "fred", baseUrl: "hanbei/mockhttpserver")
        duckduckgo(name: "ddgo")
    }
    filter {
        price()
    }
}
