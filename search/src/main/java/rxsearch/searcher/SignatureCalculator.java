package rxsearch.searcher;

@FunctionalInterface
public interface SignatureCalculator {

    void calculateAndAddSignature(SearcherRequest request, SearcherRequest.Builder requestBuilder);

}
