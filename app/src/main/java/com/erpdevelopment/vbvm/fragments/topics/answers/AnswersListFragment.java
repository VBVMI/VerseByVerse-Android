package com.erpdevelopment.vbvm.fragments.topics.answers;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.erpdevelopment.vbvm.application.MainActivity;
import com.erpdevelopment.vbvm.fragments.answer.AnswerKey;
import com.erpdevelopment.vbvm.fragments.topics.AbstractListFragment;
import com.erpdevelopment.vbvm.fragments.topics.QueryTopic;
import com.erpdevelopment.vbvm.fragments.topics.TopicSelectionListener;
import com.erpdevelopment.vbvm.fragments.topics.TopicsKey;
import com.erpdevelopment.vbvm.model.Answer;
import com.erpdevelopment.vbvm.model.Answer_Table;
import com.erpdevelopment.vbvm.model.Answer_Topic;
import com.erpdevelopment.vbvm.model.Answer_Topic_Table;
import com.erpdevelopment.vbvm.model.Topic;
import com.erpdevelopment.vbvm.model.Topic_Table;
import com.erpdevelopment.vbvm.util.ServiceLocator;
import com.raizlabs.android.dbflow.sql.language.BaseModelQueriable;
import com.raizlabs.android.dbflow.sql.language.BaseTransformable;
import com.raizlabs.android.dbflow.sql.language.From;
import com.raizlabs.android.dbflow.sql.language.Join;
import com.raizlabs.android.dbflow.sql.language.NameAlias;
import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLOperator;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Where;
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

public class AnswersListFragment extends AbstractListFragment implements AnswerSelectionListener, TopicSelectionListener {
    private static final String ARG_TOPIC = "ARG_TOPIC";
    private static final String ARG_SEARCH_TEXT = "ARG_SEARCH_TEXT";

    private String topicId;
    AnswersRecylcerAdapter adapter;
    private String searchText;

    private SugarLoader<AnswersContainer> mLoader = new SugarLoader<AnswersContainer>("AnswersListFragment")
            .background(() -> {
                List<Answer> answers;


                if (topicId == null) {
                    if (searchText != null) {
                        answers = SQLite.select().from(Answer.class).where(Answer_Table.title.like("%" + searchText + "%")).or(Answer_Table.body.like("%" + searchText + "%")).orderBy(Answer_Table.postedDate, false).queryList();
                    } else {
                        answers = SQLite.select().from(Answer.class).orderBy(Answer_Table.postedDate, false).queryList();
                    }
                } else {
                    Where<Answer> query = SQLite.select().from(Answer.class).as("A")
                            .join(Answer_Topic.class, Join.JoinType.INNER).as("T")
                            .on(Answer_Table.id.withTable(NameAlias.builder("A").build())
                                    .eq(Answer_Topic_Table.answer_id.withTable(NameAlias.builder("T").build())))
                            .where(Answer_Topic_Table.topic_id.withTable(NameAlias.builder("T").build())
                                    .eq(topicId));
                    if (searchText != null) {
                        OperatorGroup searchGroup = OperatorGroup.clause().and(Answer_Table.title.like("%" + searchText + "%")).or(Answer_Table.body.like("%" + searchText + "%"));
                        query = query.and(searchGroup);
                    }
                    answers = query.orderBy(Answer_Table.postedDate, false).queryList();
                }

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

                return new AnswersContainer(answers, mappedTopics);
            }).onSuccess(answersContainer -> {
                setAnswers(answersContainer.answers, answersContainer.mappedTopics);
            });

    public AnswersListFragment() {

    }


    @Override
    public void setSearchText(String searchText) {
        this.searchText = searchText;
        mLoader.restart(this);
    }

    public static AnswersListFragment newInstance(String topicId, String searchText) {
        AnswersListFragment fragment = new AnswersListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TOPIC, topicId);
        args.putString(ARG_SEARCH_TEXT, searchText);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            topicId = getArguments().getString(ARG_TOPIC);
        }
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
        adapter = new AnswersRecylcerAdapter(this, this);
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
                if (searchText == null || searchText.isEmpty() && topicId == null) {
                    showLoading();
                } else {
                    showEmpty();
                }
            }
        }
    }

    @Override
    public void didSelectTopic(String topicId) {
        ((BackstackDelegate) ServiceLocator.getService(getContext(), MainActivity.StackType.TOPICS.name())).getBackstack().goTo(TopicsKey.createWithTopic(topicId));
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
