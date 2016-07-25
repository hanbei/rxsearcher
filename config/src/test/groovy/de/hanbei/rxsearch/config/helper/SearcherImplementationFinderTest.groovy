package de.hanbei.rxsearch.config.helper

import org.junit.Before
import org.junit.Test

class SearcherImplementationFinderTest {

    SearcherImplementationFinder finder

    @Before
    public void setup() {
        finder = new SearcherImplementationFinder()
    }

    @Test
    public void findImplementations() {
        finder.findSearcherImplementations();
    }

}
