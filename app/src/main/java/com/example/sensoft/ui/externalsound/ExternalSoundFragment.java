package com.example.sensoft.ui.externalsound;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sensoft.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

public class ExternalSoundFragment extends Fragment {

    private ExternalSoundViewModel externalSoundViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        externalSoundViewModel =
                ViewModelProviders.of(this).get(ExternalSoundViewModel.class);
        View root = inflater.inflate(R.layout.fragment_external_sound, container, false);
        final TextView textView = root.findViewById(R.id.text_notifications);
        externalSoundViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}
