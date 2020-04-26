package com.erpdevelopment.vbvm.fragments.studies.study;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.AppCompatTextView;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.fragments.shared.AbstractFragment;

import org.versebyverseministry.models.Study;
import org.versebyverseministry.models.Study_Table;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StudyInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StudyInfoFragment extends AbstractFragment {
    private static final String ARG_STUDY_ID = "ARG_STUDY_ID";

    private Study study;

    private AppCompatTextView webView;
    private ImageView imageView;

    public StudyInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment StudyInfoFragment.
     */
    public static StudyInfoFragment newInstance(String studyID) {
        StudyInfoFragment fragment = new StudyInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_STUDY_ID, studyID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String studyID = getArguments().getString(ARG_STUDY_ID);
            study = SQLite.select().from(Study.class).where(Study_Table.id.eq(studyID)).querySingle();
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_study_info, container, false);
        webView = view.findViewById(R.id.webView);
        imageView = view.findViewById(R.id.imageView);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);

        int imageWidth = displayMetrics.widthPixels;
        String studyImageURL = study.imageForWidth(imageWidth);
        if (studyImageURL != null) {
            Glide.with(getContext()).load(studyImageURL).into(imageView);
        } else {
            Glide.with(getContext()).load(study.thumbnailSource).into(imageView);
        }

        StringBuilder sb = new StringBuilder();

        sb.append("<body>\n");
        sb.append(study.description);
        sb.append("</body>\n");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            webView.setText(Html.fromHtml(sb.toString(), Html.FROM_HTML_MODE_LEGACY));
        } else {
            webView.setText(Html.fromHtml(sb.toString()));
        }


        return view;
    }

}
