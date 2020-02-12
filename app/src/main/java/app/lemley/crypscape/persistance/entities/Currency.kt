package app.lemley.crypscape.persistance.entities

import androidx.room.*

@Entity(
    tableName = "currency",
    foreignKeys = [
        ForeignKey(
            entity = Platform::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("platformId"),
            onDelete = ForeignKey.CASCADE
        )
    ],

    indices = [
        Index(name = "currency__platform_id", value = ["platformId"], unique = false),
        Index(name = "currency__id", value = ["id"], unique = true)
    ]
)
data class Currency(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "serverId")
    val serverId: String = "",

    @ColumnInfo(name = "name")
    val name: String = "",

    @ColumnInfo(name = "base_min_size")
    val baseMinSize: Double = 0.0,

    @ColumnInfo(name = "platformId")
    val platform_id: Long? = null

)
