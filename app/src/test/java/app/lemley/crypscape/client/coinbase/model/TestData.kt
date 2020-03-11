package app.lemley.crypscape.client.coinbase.model

object TestData {
    const val time: String = """
        {
            "iso": "2015-01-07T23:47:25.201Z",
            "epoch": 1420674445.201
        }
    """
    const val ticker: String = """
        {
          "trade_id": 4729088,
          "price": "333.99",
          "size": "0.193",
          "bid": "333.98",
          "ask": "333.99",
          "volume": "5957.11914015",
          "time": "2015-11-14T20:46:03.511254Z"
        }
    """
    const val products: String = """
        [
            {
                "id": "BTC-USD",
                "base_currency": "BTC",
                "quote_currency": "USD",
                "base_min_size": "0.001",
                "base_max_size": "10000.00",
                "quote_increment": "0.01"
            }
        ]
    """
    const val currencies: String = """
       [{
            "id": "BTC",
            "name": "Bitcoin",
            "min_size": "0.00000001"
       }, {
            "id": "USD",
            "name": "United States Dollar",
            "min_size": "0.01000000"
       }] 
    """

    //[ time, low, high, open, close, volume ]
    const val candles: String = """
       [
            [ 1415398768, 0.32, 4.2, 0.35, 4.2, 12.3 ],
            [ 1415398767, 0.31, 4.1, 0.34, 4.1, 12.2 ]
       ] 
    """

    const val orderBookSnapshot: String = """
    {
        "type": "snapshot",
        "product_id": "BTC-USD",
        "bids": [["10101.10", "0.45054140"]],
        "asks": [["10102.55", "0.57753524"]]
    }
    """

    const val orderBookL2Update: String = """
    {
        "type": "l2update",
        "product_id": "BTC-USD",
        "time": "2019-08-14T20:42:27.265Z",
        "changes": [
            [
                "buy",
                "10101.80000000",
                "0.162567"
            ],
            [
                "sell",
                "10202.80000000",
                "0.262567"
            ]
        ]
    } 
    """
}