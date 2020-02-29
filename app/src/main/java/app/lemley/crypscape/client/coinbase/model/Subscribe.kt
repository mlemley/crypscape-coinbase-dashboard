package app.lemley.crypscape.client.coinbase.model

import com.google.gson.annotations.SerializedName

fun subscriptionFor(
    type: Subscribe.Type,
    products: List<String>,
    channels: List<Subscribe.Channel>
): Subscribe {
    val channelNames = mutableListOf<String>()
    channels.forEach { channelNames.add(it.toString()) }
    return Subscribe(type.toString(), products, channelNames)
}

data class Subscribe(
    val type: String,
    @SerializedName("product_ids")
    val productIds: List<String>,
    val channels: List<String>
) {


    sealed class Type {
        object Subscribe : Type() {
            override fun toString(): String {
                return "subscribe"
            }
        }

        object Unsubscribe : Type() {
            override fun toString(): String {
                return "unsubscribe"
            }
        }
    }

    sealed class Channel {
        object Ticker : Channel() {
            override fun toString(): String {
                return "ticker"
            }
        }

        object HeartBeat : Channel() {
            override fun toString(): String {
                return "heartbeat"
            }
        }

        object Level2 : Channel() {
            override fun toString(): String {
                return "level2"
            }
        }
    }
}
