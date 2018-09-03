package com.erpdevelopment.vbvm.api;

import android.util.Log;

import com.erpdevelopment.vbvm.StringHelpers;
import com.erpdevelopment.vbvm.database.AppDatabase;
import com.erpdevelopment.vbvm.model.Answer;
import com.erpdevelopment.vbvm.model.Answer_Topic;
import com.erpdevelopment.vbvm.model.Answer_Topic_Table;
import com.erpdevelopment.vbvm.model.Article_Topic;
import com.erpdevelopment.vbvm.model.Channel;
import com.erpdevelopment.vbvm.model.GroupStudy;
import com.erpdevelopment.vbvm.model.Study;
import com.erpdevelopment.vbvm.model.Study_Topic;
import com.erpdevelopment.vbvm.model.Video;
import com.erpdevelopment.vbvm.model.Video_Table;
import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.runtime.FlowContentObserver;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ProcessModelTransaction;

import com.erpdevelopment.vbvm.model.Article;
import com.erpdevelopment.vbvm.model.Article_Topic_Table;
import com.erpdevelopment.vbvm.model.Category;
import com.erpdevelopment.vbvm.model.Lesson;
import com.erpdevelopment.vbvm.model.Lesson_Table;
import com.erpdevelopment.vbvm.model.Lesson_Topic;
import com.erpdevelopment.vbvm.model.Lesson_Topic_Table;
import com.erpdevelopment.vbvm.model.Study_Topic_Table;
import com.erpdevelopment.vbvm.model.Topic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by thomascarey on 24/06/17.
 */

public class DatabaseManager {
    private static final DatabaseManager ourInstance = new DatabaseManager();
    private static final String TAG = "DatabaseManager";

    public static DatabaseManager getInstance() {
        return ourInstance;
    }

    private DatabaseManager() {

    }

    public static FlowContentObserver observer = new FlowContentObserver("com.erpdevelopment.vbvm");

    void saveStudies(List<Study> studies) {
        // In order to save these studies we must first fetch all the existing ones and create merge and delete lists
        List<Study> persistedStudies = SQLite.select().from(Study.class).queryList();

        mergeAPIData(persistedStudies, studies, new MergeOperation<Study>() {
            @Override
            public void didPersist(Study instance) {
                // find all the relevant topic relationships and update them
                List<Study_Topic> studyTopics = SQLite.select().from(Study_Topic.class).where(Study_Topic_Table.study_id.eq(instance.id)).queryList();

                for(Study_Topic st : studyTopics) {
                    st.delete();
                }

                if (instance.topics != null) {
                    for(Topic t: instance.topics) {
                        t.id = StringHelpers.toSlug(t.id);
                        t.topic = StringHelpers.changeStringCase(t.topic);
                        t.save();

                        Study_Topic studyTopic = new Study_Topic();
                        studyTopic.setStudy(instance);
                        studyTopic.setTopic(t);
                        studyTopic.save();
                    }
                }
            }

            @Override
            public void didDelete(Study instance) {
                SQLite.delete().from(Study_Topic.class).where(Study_Topic_Table.study_id.eq(instance.id)).execute();
            }
        });
    }

    void saveLessons(List<Lesson> lessons, String studyId) {
        for(Lesson lesson : lessons) {
            lesson.studyId = studyId;
        }

        List<Lesson> persistedLessons = SQLite.select().from(Lesson.class).where(Lesson_Table.studyId.eq(studyId)).queryList();

        mergeAPIData(persistedLessons, lessons, new MergeOperation<Lesson>() {
            @Override
            public void didPersist(Lesson instance) {
                // find all the relevant topic relationships and update them
                List<Lesson_Topic> lessonTopics = SQLite.select().from(Lesson_Topic.class).where(Lesson_Topic_Table.lesson_id.eq(instance.id)).queryList();

                for(Lesson_Topic lesson_topic : lessonTopics) {
                    lesson_topic.delete();
                }

                if (instance.topics != null) {
                    for(Topic t: instance.topics) {
                        t.id = StringHelpers.toSlug(t.id);
                        t.topic = StringHelpers.changeStringCase(t.topic);
                        t.save();

                        Lesson_Topic lessonTopic = new Lesson_Topic();
                        lessonTopic.setLesson(instance);
                        lessonTopic.setTopic(t);
                        lessonTopic.save();
                    }
                }
            }

            @Override
            public void didDelete(Lesson instance) {
                SQLite.delete().from(Lesson_Topic.class).where(Lesson_Topic_Table.lesson_id.eq(instance.id)).execute();
            }
        });
    }

    void saveArticles(List<Article> articles, boolean cleanOutMissingArticles) {
        List<Article> persistedLessons = SQLite.select().from(Article.class).queryList();

        mergeAPIData(persistedLessons, articles, cleanOutMissingArticles, new MergeOperation<Article>() {
            @Override
            public void didPersist(Article instance) {
                // find all the relevant topic relationships and update them
                List<Article_Topic> topics = SQLite.select().from(Article_Topic.class).where(Article_Topic_Table.article_id.eq(instance.id)).queryList();

                for(Article_Topic topic : topics) {
                    topic.delete();
                }

                if (instance.topics != null) {
                    for(Topic t: instance.topics) {
                        t.id = StringHelpers.toSlug(t.id);
                        t.topic = StringHelpers.changeStringCase(t.topic);
                        t.save();

                        Article_Topic topic = new Article_Topic();
                        topic.setArticle(instance);
                        topic.setTopic(t);
                        topic.save();
                    }
                }
            }

            @Override
            public void didDelete(Article instance) {
                SQLite.delete().from(Article_Topic.class).where(Article_Topic_Table.article_id.eq(instance.id)).execute();
            }
        });
    }

    void saveAnswers(List<Answer> articles, boolean cleanOutMissingAnswers) {
        List<Answer> persistedLessons = SQLite.select().from(Answer.class).queryList();

        mergeAPIData(persistedLessons, articles, cleanOutMissingAnswers, new MergeOperation<Answer>() {
            @Override
            public void didPersist(Answer instance) {
                // find all the relevant topic relationships and update them
                List<Answer_Topic> topics = SQLite.select().from(Answer_Topic.class).where(Answer_Topic_Table.answer_id.eq(instance.id)).queryList();

                for(Answer_Topic topic : topics) {
                    topic.delete();
                }

                if (instance.topics != null) {
                    for(Topic t: instance.topics) {
                        t.id = StringHelpers.toSlug(t.id);
                        t.topic = StringHelpers.changeStringCase(t.topic);
                        t.save();

                        Answer_Topic topic = new Answer_Topic();
                        topic.setAnswer(instance);
                        topic.setTopic(t);
                        topic.save();
                    }
                }
            }

            @Override
            public void didDelete(Answer instance) {
                SQLite.delete().from(Answer_Topic.class).where(Answer_Topic_Table.answer_id.eq(instance.id)).execute();
            }
        });
    }

    void saveCategories(List<Category> categories) {
        List<Category> persistedCategories = SQLite.select().from(Category.class).queryList();
        mergeAPIData(persistedCategories, categories);
    }

    void saveChannels(List<Channel> channels) {
        for(Channel c : channels) {
            saveChannelVideos(c.videos, c.id);
            c.videoCount = c.videos.size();
        }

        List<Channel> persistedChannels = SQLite.select().from(Channel.class).queryList();
        mergeAPIData(persistedChannels, channels);
    }

    void saveChannelVideos(List<Video> videos, String channelId) {
        for (Video v : videos) {
            v.channelId = channelId;
        }

        List<Video> persistedVideos = SQLite.select().from(Video.class).where(Video_Table.channelId.eq(channelId)).queryList();
        mergeAPIData(persistedVideos, videos);
    }

    void saveGroupStudies(List<GroupStudy> studies) {
        for (GroupStudy g : studies){
            saveGroupStudyVideos(g.videos, g.id);
            g.videoCount = g.videos.size();
        }

        List<GroupStudy> persisted = SQLite.select().from(GroupStudy.class).queryList();
        mergeAPIData(persisted, studies);
    }

    void saveGroupStudyVideos(List<Video> videos, String groupStudyId) {
        for (Video v : videos) {
            v.groupStudyId = groupStudyId;
        }
        List<Video> persistedVideos = SQLite.select().from(Video.class).where(Video_Table.groupStudyId.eq(groupStudyId)).queryList();
        mergeAPIData(persistedVideos, videos);
    }


    private static class MergePair<T> {
        public final Collection<T> entriesSaved;
        public final Collection<T> entriesDeleted;

        public MergePair(Collection<T> entriesSaved, Collection<T> entriesDeleted) {
            this.entriesDeleted = entriesDeleted;
            this.entriesSaved = entriesSaved;
        }
    }

    private static <T extends BaseModel & Mergable<T>> MergePair<T> mergeAPIData(List<T> persistedEntries, List<T> apiEntries) {
        return mergeAPIData(persistedEntries, apiEntries, null);
    }

    private static <T extends BaseModel & Mergable<T>> MergePair<T> mergeAPIData(List<T> persistedEntries, List<T> apiEntries, final MergeOperation<T> operation) {
        return mergeAPIData(persistedEntries, apiEntries, true, operation);
    }

    private static <T extends BaseModel & Mergable<T>> MergePair<T> mergeAPIData(List<T> persistedEntries, List<T> apiEntries, boolean deleteDifference , final MergeOperation<T> operation) {

        Map<String, T> mergedMap = new HashMap<>();

        Set<String> unconsumedIds = new HashSet<>();

        for (T entry : persistedEntries) {
            mergedMap.put(entry.identifier(), entry);
            if (deleteDifference)
                unconsumedIds.add(entry.identifier());
        }

        if (apiEntries != null) {
            for (T apiEntry : apiEntries) {
                unconsumedIds.remove(apiEntry.identifier());

                T persistedVersion = mergedMap.get(apiEntry.identifier());
                if (persistedVersion != null) {
                    persistedVersion.mergeAPIAttributes(apiEntry);
                } else {
                    mergedMap.put(apiEntry.identifier(), apiEntry);
                }
            }
        } else {
            Log.d(TAG, "apiEntries was null");
        }

        Collection<T> entriesToDelete = new ArrayList<>();

        for (String id : unconsumedIds) {
            T entry = mergedMap.get(id);
            if (entry != null) {
                entriesToDelete.add(entry);
                mergedMap.remove(id);
            }
        }

        Collection<T> saveList = mergedMap.values();

        DatabaseDefinition database = FlowManager.getDatabase(AppDatabase.class);

        observer.beginTransaction();

        observer.setNotifyAllUris(false);

        database.executeTransaction(new ProcessModelTransaction.Builder<>(
                new ProcessModelTransaction.ProcessModel<T>() {
                    public void processModel(T instance, DatabaseWrapper wrapper) {
                        instance.save();
                        if (operation != null) {
                            operation.didPersist(instance);
                        }
                    }
                }
        ).addAll(saveList).build());

        if (deleteDifference) {
            database.executeTransaction(new ProcessModelTransaction.Builder<>(
                    new ProcessModelTransaction.ProcessModel<T>() {
                        @Override
                        public void processModel(T instance, DatabaseWrapper wrapper) {
                            instance.delete();
                            if (operation != null) {
                                operation.didDelete(instance);
                            }
                        }
                    }
            ).addAll(entriesToDelete).build());
        }

        observer.endTransactionAndNotify();

        return new MergePair<T>(saveList, entriesToDelete);
    }
}
