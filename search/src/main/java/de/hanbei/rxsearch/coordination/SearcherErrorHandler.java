package de.hanbei.rxsearch.coordination;

import de.hanbei.rxsearch.model.Offer;
import de.hanbei.rxsearch.searcher.SearcherException;
import io.reactivex.Observable;

@FunctionalInterface
public interface SearcherErrorHandler {

    void searcherError(String searcher, SearcherException t);

}
