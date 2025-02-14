/*
 *  (C) Copyright 2021 TheOtherP (theotherp@posteo.net)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.nzbhydra.searching;

import org.junit.Test;
import org.nzbhydra.searching.searchrequests.SearchRequest;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class CustomSearchRequestMappingTest {

    private final CustomSearchRequestMapping testee = new CustomSearchRequestMapping();

    @Test
    public void shouldMapQueryForShowAddingSeasonAndEpisode() {
        final SearchRequest searchRequest = new SearchRequest();
        searchRequest.setQuery("my show name s4");
        searchRequest.setSeason(4);
        searchRequest.setEpisode("21");

        CustomSearchRequestMapping.Mapping mapping = new CustomSearchRequestMapping.Mapping("TVSEARCH;QUERY;{title:my show name}{0:.*};{title} s{season:00}e{episode:00}");
        testee.mapSearchRequest(searchRequest, mapping);
        assertThat(searchRequest.getQuery()).isPresent().get().isEqualTo("my show name s04e21");
    }

    @Test
    public void shouldSkipMappingOfMetagroupsIfDataUnavailable() {
        final SearchRequest searchRequest = new SearchRequest();
        searchRequest.setQuery("my show name s4");

        CustomSearchRequestMapping.Mapping mapping = new CustomSearchRequestMapping.Mapping("TVSEARCH;QUERY;{title:my show name}{0:.*};{title} s{season:00}e{episode:00}");
        testee.mapSearchRequest(searchRequest, Collections.singletonList(mapping));
        assertThat(searchRequest.getQuery()).isPresent().get().isEqualTo("my show name s4");
    }

    @Test
    public void shouldMapQueryForShowReplacingTheTitle() {
        final SearchRequest searchRequest = new SearchRequest();
        searchRequest.setQuery("my show name s4");
        searchRequest.setSeason(4);
        searchRequest.setEpisode("21");

        CustomSearchRequestMapping.Mapping mapping = new CustomSearchRequestMapping.Mapping("TVSEARCH;QUERY;{title:my show name}{0:.*};some other title I want{0}");
        testee.mapSearchRequest(searchRequest, mapping);
        assertThat(searchRequest.getQuery()).isPresent().get().isEqualTo("some other title I want s4");
    }

    @Test
    public void shouldMapQueryJustReplacingWithoutGroup() {
        final SearchRequest searchRequest = new SearchRequest();
        searchRequest.setQuery("my show name s4");

        CustomSearchRequestMapping.Mapping mapping = new CustomSearchRequestMapping.Mapping("TVSEARCH;QUERY;my show name.*;some other title I want");
        testee.mapSearchRequest(searchRequest, mapping);
        assertThat(searchRequest.getQuery()).isPresent().get().isEqualTo("some other title I want");
    }

    @Test
    public void shouldMapTitleForShowReplacingTheTitle() {
        final SearchRequest searchRequest = new SearchRequest();
        searchRequest.setTitle("my show name");

        CustomSearchRequestMapping.Mapping mapping = new CustomSearchRequestMapping.Mapping("TVSEARCH;TITLE;{title:my show name};some other title I want");
        testee.mapSearchRequest(searchRequest, mapping);
        assertThat(searchRequest.getTitle()).isPresent().get().isEqualTo("some other title I want");
    }

    @Test
    public void shouldFindMatchingDatasetQuery() {
        final SearchRequest searchRequest = new SearchRequest();

        searchRequest.setQuery("my show name s4");
        final CustomSearchRequestMapping.Mapping mapping = new CustomSearchRequestMapping.Mapping("TVSEARCH;QUERY;{title:my show name}{0:.*};{title} s{season:00}e{episode:00}");
        assertThat(testee.isDatasetMatch(searchRequest, mapping)).isTrue();

        searchRequest.setQuery("my show name whatever");
        assertThat(testee.isDatasetMatch(searchRequest, mapping)).isTrue();

        searchRequest.setQuery("my other show name");
        assertThat(testee.isDatasetMatch(searchRequest, mapping)).isFalse();
    }

    @Test
    public void shouldFindMatchingDatasetTitle() {
        final SearchRequest searchRequest = new SearchRequest();
        searchRequest.setTitle("my wrongly mapped title");
        final CustomSearchRequestMapping.Mapping mapping = new CustomSearchRequestMapping.Mapping("TVSEARCH;TITLE;{title:my wrongly mapped title};my correct title");
        assertThat(testee.isDatasetMatch(searchRequest, mapping)).isTrue();

        searchRequest.setTitle("my correctly mapped title");
        assertThat(testee.isDatasetMatch(searchRequest, mapping)).isFalse();
    }


}
