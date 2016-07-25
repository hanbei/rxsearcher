package de.hanbei.rxsearch.config.helper

import com.google.common.reflect.ClassPath
import de.hanbei.rxsearch.searcher.Searcher

class SearcherImplementationFinder {

    List<Class<? extends Searcher>> findSearcherImplementations() {
        ClassPath classPath = ClassPath.from(getClass().getClassLoader());
        for (ClassPath.ClassInfo classInfo : classPath.getTopLevelClasses()) {
            try {
                if (classInfo.load().isAssignableFrom(Searcher.class)) {
                    println classInfo;
                }
            } catch (ClassNotFoundException e) {
                println "Exception ${e.getMessage()}"
            }
        }
        return Collections.emptyList();
    }

}
