package docusearch.converters;

import docusearch.models.SearchTypes;
import org.springframework.core.convert.converter.Converter;

public class SearchTypeConverter implements Converter<String, SearchTypes> {
    @Override
    public SearchTypes convert(String source) {
        SearchTypes searchType;
        try {
            searchType = SearchTypes.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            searchType = SearchTypes.INVALID;
        }
        return  searchType;
    }
}