package org.versebyverseministry.vbvmi.api;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.runtime.FlowContentObserver;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ProcessModelTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

import org.versebyverseministry.vbvmi.database.AppDatabase;
import org.versebyverseministry.vbvmi.model.Article;
import org.versebyverseministry.vbvmi.model.Article_Table;
import org.versebyverseministry.vbvmi.model.Article_Topic;
import org.versebyverseministry.vbvmi.model.Category;
import org.versebyverseministry.vbvmi.model.Lesson;
import org.versebyverseministry.vbvmi.model.Lesson_Table;
import org.versebyverseministry.vbvmi.model.Lesson_Topic;
import org.versebyverseministry.vbvmi.model.Lesson_Topic_Table;
import org.versebyverseministry.vbvmi.model.Study;
import org.versebyverseministry.vbvmi.model.Study_Topic;
import org.versebyverseministry.vbvmi.model.Study_Topic_Table;
import org.versebyverseministry.vbvmi.model.Topic;
import org.versebyverseministry.vbvmi.model.pojo.Studies;

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

    public static DatabaseManager getInstance() {
        return ourInstance;
    }

    private DatabaseManager() {

    }

    public static FlowContentObserver observer = new FlowContentObserver();

    public void saveStudies(List<Study> studies) {
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

                for(Topic t: instance.topics) {
                    t.save();

                    Study_Topic studyTopic = new Study_Topic();
                    studyTopic.setStudy(instance);
                    studyTopic.setTopic(t);
                    studyTopic.save();
                }
            }

            @Override
            public void didDelete(Study instance) {
                SQLite.delete().from(Study_Topic.class).where(Study_Topic_Table.study_id.eq(instance.id)).execute();
            }
        });
    }

    public void saveLessons(List<Lesson> lessons, String studyId) {
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

                for(Topic t: instance.topics) {
                    t.save();

                    Lesson_Topic lessonTopic = new Lesson_Topic();
                    lessonTopic.setLesson(instance);
                    lessonTopic.setTopic(t);
                    lessonTopic.save();
                }
            }

            @Override
            public void didDelete(Lesson instance) {
                SQLite.delete().from(Lesson_Topic.class).where(Lesson_Topic_Table.lesson_id.eq(instance.id)).execute();
            }
        });
    }

    public void saveArticles(List<Article> articles, boolean cleanOutMissingArticles) {
        List<Article> persistedLessons = SQLite.select().from(Article.class).queryList();

        mergeAPIData(persistedLessons, articles, cleanOutMissingArticles, new MergeOperation<Article>() {
            @Override
            public void didPersist(Article instance) {
                // find all the relevant topic relationships and update them
                List<Lesson_Topic> lessonTopics = SQLite.select().from(Lesson_Topic.class).where(Lesson_Topic_Table.lesson_id.eq(instance.id)).queryList();

                for(Lesson_Topic lesson_topic : lessonTopics) {
                    lesson_topic.delete();
                }

                for(Topic t: instance.topics) {
                    t.save();

                    Article_Topic lessonTopic = new Article_Topic();
                    lessonTopic.setArticle(instance);
                    lessonTopic.setTopic(t);
                    lessonTopic.save();
                }
            }

            @Override
            public void didDelete(Article instance) {
                SQLite.delete().from(Lesson_Topic.class).where(Lesson_Topic_Table.lesson_id.eq(instance.id)).execute();
            }
        });
    }

    public void saveCategories(List<Category> categories) {
        List<Category> persistedCategories = SQLite.select().from(Category.class).queryList();
        mergeAPIData(persistedCategories, categories);
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

        for (T apiEntry : apiEntries) {
            unconsumedIds.remove(apiEntry.identifier());

            T persistedVersion = mergedMap.get(apiEntry.identifier());
            if (persistedVersion != null) {
                persistedVersion.mergeAPIAttributes(apiEntry);
            } else {
                mergedMap.put(apiEntry.identifier(), apiEntry);
            }
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
