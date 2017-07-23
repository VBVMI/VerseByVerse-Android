package org.versebyverseministry.vbvmi.fragments.studies.study;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.versebyverseministry.vbvmi.R;
import org.versebyverseministry.vbvmi.fragments.shared.AbstractFragment;
import org.versebyverseministry.vbvmi.model.Study;
import org.versebyverseministry.vbvmi.model.Study_Table;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StudyInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StudyInfoFragment extends AbstractFragment {
    private static final String ARG_STUDY_ID = "ARG_STUDY_ID";

    private Study study;

    @BindView(R.id.webView)
    WebView webView;

    @BindView(R.id.imageView)
    ImageView imageView;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_study_info, container, false);
        unbinder = ButterKnife.bind(this, view);


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
        sb.append("<head>\n");
        sb.append("<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js\"></script>\n");
        sb.append("<script type=\"text/javascript\">\n");
        sb.append("function codeAddress() {\n");
        sb.append("$(\"body a\").removeAttr(\"href\");\n");
        sb.append("}\n");
        sb.append("window.onload = codeAddress;\n");
        sb.append("</script>\n");
        sb.append("</head>\n");
        sb.append("<body>\n");
        sb.append(study.description);
        sb.append("</body>\n");

        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadData(sb.toString(), "text/html", "UTF-8");

        return view;
    }

}
