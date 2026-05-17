package com.estrada.ccmsarchive;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.github.appintro.AppIntro;
import com.github.appintro.AppIntroPageTransformerType;

public class IntroActivity extends AppIntro {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(CustomIntroSlide.newInstance(
                "Welcome to CCMS Archive",
                "The official repository for CCMS projects at CNSC.",
                R.drawable.archive_logo
        ));

        addSlide(CustomIntroSlide.newInstance(
                "Easy Submission",
                "Upload project details and notify professors with a single click.",
                R.drawable.upload
        ));

        addSlide(CustomIntroSlide.newInstance(
                "Real-time Tracking",
                "Monitor the approval status of your submissions instantly.",
                R.drawable.approve
        ));

        addSlide(new StepAnimationFragment());

        setIndicatorColor(Color.WHITE, Color.parseColor("#80FFFFFF"));
        showStatusBar(true);

        setTransformer(new AppIntroPageTransformerType.Parallax());
    }

    @Override
    protected void onSkipPressed(@Nullable Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        goToLogin();
    }

    @Override
    protected void onDonePressed(@Nullable Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        goToLogin();
    }

    private void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}