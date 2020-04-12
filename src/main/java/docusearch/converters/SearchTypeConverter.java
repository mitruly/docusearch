package docusearch.converters;

import docusearch.models.SearchType;
import org.springframework.core.convert.converter.Converter;

public class SearchTypeConverter implements Converter<String, SearchType> {
    @Override
    public SearchType convert(String source) {
        SearchType searchType;
        try {
            searchType = SearchType.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            searchType = SearchType.INVALID;
        }
        return  searchType;
    }
}