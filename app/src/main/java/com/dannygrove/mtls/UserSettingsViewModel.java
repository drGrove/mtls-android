package com.dannygrove.mtls;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;

public class UserSettingsViewModel extends AndroidViewModel {
    private MTLSRepository mRepository;

    public UserSettingsViewModel(Application application) {
        super(application);
        mRepository = new MTLSRepository(application);
    }
}
