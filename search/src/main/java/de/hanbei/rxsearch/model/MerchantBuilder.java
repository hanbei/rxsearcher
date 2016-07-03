package de.hanbei.rxsearch.model;

public class MerchantBuilder {

    public interface NameStep {
        OtherStep name(String name);
    }

    public interface OtherStep extends BuildStep {
        OtherStep id(String id);

        OtherStep image(String id);

        OtherStep category(String id);
    }

    public interface BuildStep {
        Merchant build();
    }

    static class Steps implements BuildStep, NameStep, OtherStep {

        private String name;
        private String id;
        private String image;
        private String category;

        @Override
        public Merchant build() {
            return new Merchant(name, image, id, category);
        }

        @Override
        public OtherStep name(String name) {
            this.name = name;
            return this;
        }

        @Override
        public OtherStep id(String id) {
            this.id = id;
            return this;
        }

        @Override
        public OtherStep image(String image) {
            this.image = image;
            return this;
        }

        @Override
        public OtherStep category(String category) {
            this.category = category;
            return this;
        }
    }

    private MerchantBuilder() {

    }
}
