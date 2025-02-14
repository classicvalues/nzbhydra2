/*
 *  (C) Copyright 2020 TheOtherP (theotherp@posteo.net)
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

package org.nzbhydra.downloading.torrents;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.nzbhydra.config.BaseConfig;
import org.nzbhydra.config.ConfigProvider;
import org.nzbhydra.downloading.DownloadResult;

import java.io.File;
import java.net.URI;

import static org.mockito.Mockito.when;

public class TorrentFileHandlerTest {

    @Mock
    private ConfigProvider configProviderMock;

    @InjectMocks
    private TorrentFileHandler testee;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        final BaseConfig baseConfig = new BaseConfig();
        when(configProviderMock.getBaseConfig()).thenReturn(baseConfig);
        baseConfig.getDownloading().setSaveTorrentsTo("c:\\torrents");

    }

    @Test
    public void shouldCalculateTorrentFilePath() {
        final File targetFile = testee.getTargetFile(DownloadResult.createSuccessfulDownloadResult("Some title", "".getBytes(), null), null);
        Assertions.assertThat(targetFile).isEqualTo(new File("c:\\torrents\\Some title.torrent"));
    }

    @Test
    public void shouldShortenFileName() {
        final File targetFile = testee.getTargetFile(DownloadResult.createSuccessfulDownloadResult("Some title that is so long that the resulting path exceeds 220 characters which is roughly the max length of a path on windows and perhaps even some linux systems, not sure about that. I think 255 is the limit but let's just be sure, no filename should be that long anyway.", "".getBytes(), null), null);
        Assertions.assertThat(targetFile).isEqualTo(new File("c:\\torrents\\Some title that is so long that the resulting path exceeds 220 characters which is roughly the max length of a path on windows and perhaps even some linux systems, not sure about that. I think 255 is .torrent"));
        Assertions.assertThat(targetFile.getAbsolutePath()).hasSize(220);
    }

    @Test
    public void shouldCalculateMagnetFilePath() throws Exception {
        final File targetFile = testee.getTargetFile(DownloadResult.createSuccessfulDownloadResult("Some title", "".getBytes(), null), new URI("http://127.0.0.1"));
        Assertions.assertThat(targetFile).isEqualTo(new File("c:\\torrents\\Some title.magnet"));
    }
}