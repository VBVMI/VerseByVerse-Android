package org.versebyverseministry.vbvmi.fragments.answers;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.versebyverseministry.vbvmi.R;
import org.versebyverseministry.vbvmi.StringHelpers;
import org.versebyverseministry.vbvmi.model.Article;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by thomascarey on 27/08/17.
 */

public class ArticlesRecyclerAdapter extends RecyclerView.Adapter<ArticlesRecyclerAdapter.ViewHolder> {


    private List<Article> articles;

    public void setArticles(List<Article> articles) {
        this.articles = articles;
        this.notifyDataSetChanged();
    }

    public ArticlesRecyclerAdapter(List<Article> articles) {
        this.articles = articles;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_article, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Article article = articles.get(position);

        holder.titleView.setText(StringHelpers.fromHtmlString(article.title));
        Date date = new Date(TimeUnit.MILLISECONDS.convert(article.postedDate, TimeUnit.SECONDS));
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());

        holder.dateView.setText(dateFormat.format(date));
        holder.authorView.setText(StringHelpers.fromHtmlString(article.authorName));
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.title_view)
        TextView titleView;

        @BindView(R.id.author_view)
        TextView authorView;

        @BindView(R.id.date_view)
        TextView dateView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
