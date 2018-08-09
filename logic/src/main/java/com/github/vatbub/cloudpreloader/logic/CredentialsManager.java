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


import com.github.vatbub.common.core.Prefs;
import org.jetbrains.annotations.Nullable;

public class CredentialsManager {
    private static CredentialsManager instance;
    private Prefs prefs;

    // hide the constructor
    private CredentialsManager() {

    }

    public static CredentialsManager getInstance() {
        if (instance == null)
            instance = new CredentialsManager();
        return instance;
    }

    private Prefs getPrefs() {
        if (prefs == null)
            prefs = new Prefs(CredentialsManager.class.getName());
        return prefs;
    }

    public void saveCredentials(Service.KnownImplementations implementation, Credentials credentials) {
        getPrefs().setPreference(implementation.toString(), credentials.serialize());
    }

    public boolean hasCredentials(Service.KnownImplementations implementation) {
        return getPrefs().getPreference(implementation.toString(), null) != null;
    }

    @Nullable
    public Credentials getCredentials(Service.KnownImplementations implementation) {
        String prefValue = getPrefs().getPreference(implementation.toString(), null);
        if (prefValue == null)
            return null;

        try {
            Credentials credentials = Service.getInstance(implementation).getDefaultCredentialsClass().newInstance();
            credentials.deserialize(prefValue);
            return credentials;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
