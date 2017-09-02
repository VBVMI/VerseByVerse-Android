package com.erpdevelopment.vbvm.fragments.topics.answers;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.erpdevelopment.vbvm.application.MainActivity;
import com.erpdevelopment.vbvm.fragments.answer.AnswerKey;
import com.erpdevelopment.vbvm.fragments.topics.AbstractListFragment;
import com.erpdevelopment.vbvm.fragments.topics.QueryTopic;
import com.erpdevelopment.vbvm.model.Answer;
import com.erpdevelopment.vbvm.model.Answer_Table;
import com.erpdevelopment.vbvm.model.Answer_Topic;
import com.erpdevelopment.vbvm.model.Answer_Topic_Table;
import com.erpdevelopment.vbvm.model.Topic;
import com.erpdevelopment.vbvm.model.Topic_Table;
import com.erpdevelopment.vbvm.util.ServiceLocator;
import com.raizlabs.android.dbflow.sql.language.Join;
import com.raizlabs.android.dbflow.sql.language.NameAlias;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.FlowCursor;
import com.zhuinden.simplestack.BackstackDelegate;

import org.algi.sugarloader.SugarLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by thomascarey on 2/09/17.
 */

public class AnswersListFragment extends AbstractListFragment implements AnswerSelectionListener {

    AnswersRecylcerAdapter adapter;

    private SugarLoader<AnswersContainer> mLoader = new SugarLoader<AnswersContainer>("AnswersListFragment")
            .background(() -> {
                List<Answer> articles = SQLite.select().from(Answer.class).orderBy(Answer_Table.postedDate, false).queryList();

                FlowCursor cursor = SQLite.select().from(Answer_Topic.class).as("A")
                        .join(Topic.class, Join.JoinType.INNER).as("T")
                        .on(Answer_Topic_Table.topic_id
                                .withTable(NameAlias.builder("A").build())
                                .eq(Topic_Table.id.withTable(NameAlias.builder("T").build()))).orderBy(Answer_Topic_Table.answer_id, true).query();


                Map<String, List<QueryTopic>> mappedTopics = new HashMap<>();
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        String articleId = cursor.getString(1);
                        String topic = cursor.getString(4);
                        String topicId = cursor.getString(2);
                        QueryTopic qt = new QueryTopic(topic, topicId);
                        List<QueryTopic> topics = mappedTopics.get(articleId);
                        if (topics != null) {
                            topics.add(qt);
                        } else {
                            ArrayList<QueryTopic> list = new ArrayList<QueryTopic>();
                            list.add(qt);
                            mappedTopics.put(articleId, list);
                        }
                    }
                }

                return new AnswersContainer(articles, mappedTopics);
            }).onSuccess(answersContainer -> {
                setAnswers(answersContainer.answers, answersContainer.mappedTopics);
            });

    public AnswersListFragment() {

    }

    public static AnswersListFragment newInstance() {
        AnswersListFragment fragment = new AnswersListFragment();

        return fragment;
    }


    @Override
    public void onResume() {
        super.onResume();
        mLoader.init(this);
    }

    @Override
    public void didSelectAnswer(Answer answer) {
        ((BackstackDelegate) ServiceLocator.getService(getContext(), MainActivity.StackType.TOPICS.name())).getBackstack().goTo(AnswerKey.create(answer.id));
    }

    @Override
    protected String tableName() {
        return "Answer";
    }

    @Override
    protected void configureRecyclerView(RecyclerView recyclerView) {
        adapter = new AnswersRecylcerAdapter(this);
        reloadData();
        if (adapter.getItemCount() > 0) {
            showList();
        } else {
            showLoading();
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void reloadData() {
        mLoader.restart(this);
    }

    private void setAnswers(List<Answer> answers, Map<String, List<QueryTopic>> mappedTopics) {
        if (adapter != null) {
            adapter.setAnswers(answers, mappedTopics);
            if (adapter.getItemCount() > 0) {
                showList();
            } else {
                showLoading();
            }
        }
    }

    private class AnswersContainer {
        public List<Answer> answers;
        public Map<String, List<QueryTopic>> mappedTopics;

        public AnswersContainer(List<Answer> answers, Map<String, List<QueryTopic>> mappedTopics) {
            this.answers = answers;
            this.mappedTopics = mappedTopics;
        }
    }
}
