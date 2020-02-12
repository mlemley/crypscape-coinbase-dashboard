package app.lemley.crypscape.persistance.entities

import androidx.room.*
import app.lemley.crypscape.persistance.converter.GranularityConverter
import org.threeten.bp.Instant

@Entity(
    tableName = "candle",
    indices = [
        Index(name = "candle__platform_id", value = ["platformId"]),
        Index(name = "candle__product_id", value = ["product_id"]),
        Index(name = "candle__groups", value = ["product_id", "platformId", "granularity", "time"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = Platform::class,
            parentColumns = ["id"],
            childColumns = ["platformId"],
            onDelete = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = Product::class,
            parentColumns = ["id"],
            childColumns = ["product_id"],
            onDelete = ForeignKey.NO_ACTION
        )
    ]
)
@TypeConverters(GranularityConverter::class)
data class Candle(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "platformId")
    val platform_id: Long,

    @ColumnInfo(name = "product_id")
    val product_id: Long,

    @ColumnInfo(name = "granularity")
    val granularity: Granularity,

    @ColumnInfo(name = "time")
    val time: Instant,

    @ColumnInfo(name = "open")
    val open: Double,

    @ColumnInfo(name = "close")
    val close: Double,

    @ColumnInfo(name = "high")
    val high: Double,

    @ColumnInfo(name = "low")
    val low: Double,

    @ColumnInfo(name = "volume")
    val volume: Double
)


