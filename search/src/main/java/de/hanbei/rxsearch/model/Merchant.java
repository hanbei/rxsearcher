package de.hanbei.rxsearch.model;

public class Merchant {

    private final String name;
    private final String image;
    private final String id;
    private final String category;

    private Merchant() {
        this("", "", "", "");
    }

    Merchant(String name, String image, String id, String category) {
        this.name = name;
        this.image = image;
        this.id = id;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public static MerchantBuilder.NameStep builder() {
        return new MerchantBuilder.Steps();
    }
}
