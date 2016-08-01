package de.hanbei.rxsearch.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
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

    public static MerchantBuilder.NameStep builder() {
        return new MerchantBuilder.Steps();
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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{\"Merchant\":{")
                .append("\"name\":\"").append(name).append('"')
                .append(", ")
                .append("\"image\":\"").append(image).append('"')
                .append(", ")
                .append("\"id\":\"").append(id).append('"')
                .append(", ")
                .append("\"category\":\"").append(category).append('"')
                .append("}}");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Merchant merchant = (Merchant) o;
        return Objects.equals(name, merchant.name) &&
                Objects.equals(image, merchant.image) &&
                Objects.equals(id, merchant.id) &&
                Objects.equals(category, merchant.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, image, id, category);
    }
}
