package com.erpdevelopment.vbvm.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.versebyverseministry.models.Mergable

@Entity(tableName = "study_table")
data class RStudy(
        @PrimaryKey
        @Expose
        @SerializedName("ID")
        var id: String,

        @Expose
        @SerializedName("bibleIndex")
        var bibleIndex: Int,

        @SerializedName("title")
        @Expose
        var title: String,

        @SerializedName("thumbnailSource")
        @Expose
        var thumbnailSource: String,

        @SerializedName("podcastLink")
        @Expose
        var podcastLink: String,

        @SerializedName("lessonCount")
        @Expose
        var lessonCount: Int,

        @SerializedName("description")
        @Expose
        var description: String,

        @SerializedName("category")
        @Expose
        var category: Int,

        @SerializedName("url")
        @Expose
        var url: String,

        @SerializedName("image160")
        @Expose
        var image160: String,

        @SerializedName("image300")
        @Expose
        var image300: String,

        @SerializedName("image600")
        @Expose
        var image600: String,

        @SerializedName("image900")
        @Expose
        var image900: String,

        @SerializedName("image1100")
        @Expose
        var image1100: String,

        @SerializedName("image1400")
        @Expose
        var image1400: String
): Mergable<RStudy> {
        override fun identifier(): String {
                return id
        }

        override fun mergeAPIAttributes(apiVersion: RStudy) {
                require(id == apiVersion.id) { "The apiVersion.id must match the current id" }

                // Only merge data that is exposed to GSON
                bibleIndex = apiVersion.bibleIndex
                thumbnailSource = apiVersion.thumbnailSource
                title = apiVersion.title
                podcastLink = apiVersion.podcastLink
                lessonCount = apiVersion.lessonCount
                description = apiVersion.description
                this.category = apiVersion.category
                url = apiVersion.url
                image160 = apiVersion.image160
                image300 = apiVersion.image300
                image600 = apiVersion.image600
                image900 = apiVersion.image900
                image1100 = apiVersion.image1100
                image1400 = apiVersion.image1400
        }
}
