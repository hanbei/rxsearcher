package test_app.staging

config(country: "de") {
    searcher {
        webhose(name: "webhose", key: "71asda23")
        github(name: "github", repo: "hanbei/mockhttpserver")
        duckduckgo(name: "ddgo")
    }
    filter {
        price()
    }
}
