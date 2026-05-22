package com.estrada.ccmsarchive;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable; // 
import androidx.fragment.app.Fragment;

public class StepAnimationFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_step_animation, container, false);

        // SEQUENCE OF VIEWS TO ANIMATR
        View[] sequence = {
                view.findViewById(R.id.img_step1), view.findViewById(R.id.txt_step1), view.findViewById(R.id.line1),
                view.findViewById(R.id.img_step2), view.findViewById(R.id.txt_step2), view.findViewById(R.id.line2),
                view.findViewById(R.id.img_step3), view.findViewById(R.id.txt_step3), view.findViewById(R.id.line3),
                view.findViewById(R.id.img_step4), view.findViewById(R.id.txt_step4), view.findViewById(R.id.line4),
                view.findViewById(R.id.img_step5), view.findViewById(R.id.txt_step5), view.findViewById(R.id.line5)
        };

        Handler handler = new Handler(Looper.getMainLooper());
        long delay = 500;

        for (int i = 0; i < sequence.length; i++) {
            final View targetView = sequence[i];
            if (targetView == null) continue; 

            handler.postDelayed(() -> {
                targetView.setVisibility(View.VISIBLE);
                targetView.setAlpha(0f);
                targetView.animate()
                        .alpha(1f)
                        .setDuration(400)
                        .start();
            }, delay);

            delay += 500; 
        }

        return view;
    }
}
