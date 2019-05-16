package com.dannygrove.mtls;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.bluetooth.BluetoothAdapter;

import com.mukesh.countrypicker.Country;
import com.mukesh.countrypicker.CountryPicker;
import com.mukesh.countrypicker.listeners.OnCountryPickerListener;

public class ProfileFragment extends Fragment {
    private CountryPicker countryPicker = null;
    private Activity activity = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = getActivity();
        OnCountryPickerListener countryPickerListener = new OnCountryPickerListener() {
            @Override
            public void onSelectCountry(Country country) {
                TextInputEditText countryTextField = activity.findViewById(R.id.country);
                countryTextField.setText(country.getCode());
            }
        };
        TextInputEditText countryTextField = activity.findViewById(R.id.country);
        CountryPicker.Builder countryBuilder = new CountryPicker.Builder().with(activity).listener(countryPickerListener);
        countryPicker = countryBuilder.build();
        countryTextField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countryPicker.showBottomSheet((android.support.v7.app.AppCompatActivity) activity);
            }
        });
        BluetoothAdapter myDevice = BluetoothAdapter.getDefaultAdapter();
        String deviceName = myDevice.getName();
        TextInputEditText deviceNameTextField = activity.findViewById(R.id.device_name);
        deviceNameTextField.setText(deviceName);
    }
}
