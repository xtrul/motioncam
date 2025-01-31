package com.motioncam.ui;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.motioncam.R;
import com.motioncam.databinding.SettingsFragmentBinding;
import com.motioncam.model.SettingsViewModel;

import java.util.Locale;

public class SettingsFragment extends Fragment {
    private SettingsViewModel mViewModel;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    private void setupObservers(SettingsFragmentBinding dataBinding) {
        mViewModel.memoryUseMb.observe(getViewLifecycleOwner(), (value) -> {
            value = SettingsViewModel.MINIMUM_MEMORY_USE_MB + value;
            dataBinding.memoryUseText.setText(String.format(Locale.US, "%d MB", value));
            mViewModel.save(getContext());
        });

        mViewModel.rawVideoMemoryUseMb.observe(getViewLifecycleOwner(), (value) -> {
            value = SettingsViewModel.MINIMUM_MEMORY_USE_MB + value;
            dataBinding.rawVideoMemoryUseText.setText(String.format(Locale.US, "%d MB", value));
            mViewModel.save(getContext());
        });

        mViewModel.jpegQuality.observe(getViewLifecycleOwner(), (value) -> {
            dataBinding.jpegQualityText.setText(String.format(Locale.US, "%d%%", value));
            mViewModel.save(getContext());
        });

        mViewModel.cameraPreviewQuality.observe(getViewLifecycleOwner(), (value) -> {
            switch(value) {
                case 0:
                    dataBinding.cameraQualityPreviewText.setText(getString(R.string.low));
                    break;

                case 1:
                    dataBinding.cameraQualityPreviewText.setText(getString(R.string.medium));
                    break;

                case 2:
                    dataBinding.cameraQualityPreviewText.setText(getString(R.string.high));
                    break;
            }

            mViewModel.save(getContext());
        });

        mViewModel.dualExposureControls.observe(getViewLifecycleOwner(), (value) -> {
            dataBinding.cameraQualitySeekBar.setEnabled(value);

            mViewModel.save(getContext());
        });

        mViewModel.autoNightMode.observe(getViewLifecycleOwner(), (value) -> mViewModel.save(getContext()));
        mViewModel.raw10.observe(getViewLifecycleOwner(), (value) -> mViewModel.save(getContext()));
        mViewModel.raw16.observe(getViewLifecycleOwner(), (value) -> mViewModel.save(getContext()));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.settings_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        // Bind to model
        SettingsFragmentBinding dataBinding = SettingsFragmentBinding.bind(getView().findViewById(R.id.settingsLayout));

        dataBinding.setViewModel(mViewModel);
        dataBinding.setLifecycleOwner(this);

        // Update maximum memory use
        ActivityManager activityManager = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();

        activityManager.getMemoryInfo(memInfo);

        long totalMemory = memInfo.totalMem / (1024 * 1024) - (SettingsViewModel.MINIMUM_MEMORY_USE_MB*2);
        int maxMemory = Math.min( (int) totalMemory, SettingsViewModel.MAXIMUM_MEMORY_USE_MB);

        dataBinding.memoryUseSeekBar.setMax(maxMemory - SettingsViewModel.MINIMUM_MEMORY_USE_MB);
        dataBinding.rawVideoMemoryUseSeekBar.setMax(maxMemory - SettingsViewModel.MINIMUM_MEMORY_USE_MB);

        // Set up observers
        setupObservers(dataBinding);

        mViewModel.load(getContext());
    }

    @Override
    public void onPause() {
        super.onPause();

//        if(getContext() != null)
//            mViewModel.save(getContext());
    }
}
