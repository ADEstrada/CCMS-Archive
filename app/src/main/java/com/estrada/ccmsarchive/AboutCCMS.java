package com.estrada.ccmsarchive;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import androidx.cardview.widget.CardView;

public class AboutCCMS extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_ccms);

        TextView headerTitle = findViewById(R.id.header_title);
        ImageView btnBack = findViewById(R.id.btn_back);

        if (headerTitle != null) {
            headerTitle.setText(R.string.menu_about);
        }

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                finish();
            });
        }

        TextView tvDevContent = findViewById(R.id.tvContentDeveloper);
        if (tvDevContent != null) {
            tvDevContent.setText(Html.fromHtml(getString(R.string.developer_desc), Html.FROM_HTML_MODE_LEGACY));
            // Make links clickable
            tvDevContent.setMovementMethod(LinkMovementMethod.getInstance());
        }

        TextView tvAppVerContent = findViewById(R.id.tvContentAppVer);
        if (tvAppVerContent != null) {
            try {
                PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                String version = pInfo.versionName;
                tvAppVerContent.setText("Version " + version + " (Official Build)");
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                tvAppVerContent.setText(R.string.app_ver_desc);
            }
        }

        setupExpandableCard(
                findViewById(R.id.cardMission),
                findViewById(R.id.tvContentMission),
                findViewById(R.id.ivArrowMission)
        );

        setupExpandableCard(
                findViewById(R.id.cardDeveloper),
                tvDevContent,
                findViewById(R.id.ivArrowDeveloper)
        );

        setupExpandableCard(
                findViewById(R.id.cardAcknowledgement),
                findViewById(R.id.tvContentAcknowledgement),
                findViewById(R.id.ivArrowAcknowledgement)
        );

        setupExpandableCard(
                findViewById(R.id.cardAppVersion),
                findViewById(R.id.tvContentAppVer),
                findViewById(R.id.ivArrowAppVer)
        );
    }

    private void setupExpandableCard(CardView card, final TextView content, final ImageView arrow) {
        if (card == null || content == null || arrow == null) return;

        card.setOnClickListener(v -> {
            TransitionManager.beginDelayedTransition((ViewGroup) card.getParent(), new AutoTransition());

            if (content.getVisibility() == View.GONE) {
                content.setVisibility(View.VISIBLE);
                arrow.animate().rotation(180).setDuration(200).start();
            } else {
                content.setVisibility(View.GONE);
                arrow.animate().rotation(0).setDuration(200).start();
            }
        });
    }











}