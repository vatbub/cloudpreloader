package com.github.vatbub.cloudpreloader.logic;

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
