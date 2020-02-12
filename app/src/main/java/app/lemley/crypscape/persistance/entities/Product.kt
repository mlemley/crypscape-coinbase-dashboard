package app.lemley.crypscape.persistance.entities

import androidx.room.*

@Entity(
    tableName = "product",
    foreignKeys = [
        ForeignKey(
            entity = Platform::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("platformId"),
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Currency::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("base_currency_id"),
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Currency::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("quote_currency_id"),
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["platformId"], name = "product__platform_id", unique = false),
        Index(value = ["base_currency_id"], name = "product__base_currency_id", unique = false),
        Index(value = ["quote_currency_id"], name = "product__quote_currency_id", unique = false)
    ]
)
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "platformId")
    val platformId: Long,

    @ColumnInfo(name = "base_currency_id")
    val baseCurrency: Long,

    @ColumnInfo(name = "quote_currency_id")
    val quoteCurrency: Long,

    @ColumnInfo(name = "serverId")
    val serverId: String = "",

    @ColumnInfo(name = "base_min_size")
    val baseMinSize: Double = 0.0,

    @ColumnInfo(name = "base_max_size")
    val baseMaxSize: Double = 0.0,

    @ColumnInfo(name = "quote_increment")
    val quoteIncrement: Double = 0.0
)


