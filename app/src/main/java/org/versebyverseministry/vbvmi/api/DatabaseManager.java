package org.versebyverseministry.vbvmi.api;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ProcessModelTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

import org.versebyverseministry.vbvmi.database.AppDatabase;
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

    public void saveStudies(List<Study> studies) {

        // In order to save these studies we must first fetch all the existing ones and create merge and delete lists
        DatabaseDefinition database = FlowManager.getDatabase(AppDatabase.class);

        List<Study> persistedStudies = SQLite.select().from(Study.class).queryList();

        MergePair<Study> mergePair = mergeAPIData(persistedStudies, studies);

        database.beginTransactionAsync(new ProcessModelTransaction.Builder<>(
                        new ProcessModelTransaction.ProcessModel<Study>() {
                            @Override
                            public void processModel(Study study, DatabaseWrapper wrapper) {
                                study.save();

                                // find all the relevant topic relationships and update them
                                List<Study_Topic> studyTopics = SQLite.select().from(Study_Topic.class).where(Study_Topic_Table.study_id.eq(study.id)).queryList();

                                for(Study_Topic st : studyTopics) {
                                    st.delete();
                                }

                                for(Topic t: study.topics) {
                                    t.save();

                                    Study_Topic studyTopic = new Study_Topic();
                                    studyTopic.setStudy(study);
                                    studyTopic.setTopic(t);
                                    studyTopic.save();
                                }
                            }
                        }
                ).addAll(mergePair.entriesToSave).build()
        ).build().execute();

        if (mergePair.entriesToDelete.size() > 0) {
            ProcessModelTransaction<Study> deleteProcessModelTransaction = new ProcessModelTransaction.Builder<>(
                    new ProcessModelTransaction.ProcessModel<Study>() {

                        @Override
                        public void processModel(Study study, DatabaseWrapper wrapper) {
                            study.delete();
                            // find all topic relationships to this study and delete them too
                            SQLite.delete().from(Study_Topic.class).where(Study_Topic_Table.study_id.eq(study.id)).execute();
                        }
                    }
            ).addAll(mergePair.entriesToDelete).build();
            database.beginTransactionAsync(deleteProcessModelTransaction).build().execute();
        }
    }

    private static class MergePair<T> {
        public final Collection<T> entriesToSave;
        public final Collection<T> entriesToDelete;

        public MergePair(Collection<T> entriesToSave, Collection<T> entriesToDelete) {
            this.entriesToDelete = entriesToDelete;
            this.entriesToSave = entriesToSave;
        }
    }

    private static <T extends Mergable<T>> MergePair<T> mergeAPIData(List<T> persistedEntries, List<T> apiEntries) {

        Map<String, T> mergedMap = new HashMap<>();

        Set<String> unconsumedIds = new HashSet<>();

        for (T entry : persistedEntries) {
            mergedMap.put(entry.identifier(), entry);
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

        return new MergePair<T>(saveList, entriesToDelete);
    }
}
