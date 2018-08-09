package com.github.vatbub.cloudpreloader.logic;

/*-
 * #%L
 * cloudpreloader.logic
 * %%
 * Copyright (C) 2016 - 2018 Frederik Kammel
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import java.io.IOException;
import java.net.URL;

public class DropboxService extends Service {
    @Override
    public void sendFile(URL url, Credentials credentials, Runnable onFinished) throws IOException {

    }

    // gog090k20yty565
    // cloudPreloaderDropboxSecret

    @Override
    public Class<? extends Credentials> getDefaultCredentialsClass() {
        return null;
    }
}
