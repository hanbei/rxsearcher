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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Merchant merchant = (Merchant) o;

        if (getName() != null ? !getName().equals(merchant.getName()) : merchant.getName() != null) {
            return false;
        }
        if (getImage() != null ? !getImage().equals(merchant.getImage()) : merchant.getImage() != null) {
            return false;
        }
        if (getId() != null ? !getId().equals(merchant.getId()) : merchant.getId() != null) {
            return false;
        }
        return getCategory() != null ? getCategory().equals(merchant.getCategory()) : merchant.getCategory() == null;

    }

    @Override
    public int hashCode() {
        int result = getName() != null ? getName().hashCode() : 0;
        result = 31 * result + (getImage() != null ? getImage().hashCode() : 0);
        result = 31 * result + (getId() != null ? getId().hashCode() : 0);
        result = 31 * result + (getCategory() != null ? getCategory().hashCode() : 0);
        return result;
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
}
