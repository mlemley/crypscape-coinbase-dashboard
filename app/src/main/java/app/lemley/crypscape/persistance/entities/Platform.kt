package app.lemley.crypscape.persistance.entities

import androidx.room.*
import com.crypscape.mobile.db.converter.DateConverter
import org.threeten.bp.Instant

@Entity(
    tableName = "platform",
    indices = [Index(value = ["id"], unique = true), Index(value = ["name"], unique = true)]
)
@TypeConverters(DateConverter::class)
data class Platform(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "name")
    val name: String = "",

    @ColumnInfo(name = "startDate")
    val startDate: Instant = Instant.now()
) 
