package app.lemley.crypscape.client.coinbase.model

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer

fun enqueueSuccessfulResponse(mockWebServer: MockWebServer, code: Int, content: String) =
    mockWebServer.enqueue(
        mockSuccessfulResponse(code, content)
    )

fun mockSuccessfulResponse(code: Int, content: String): MockResponse =
    MockResponse().mockSuccess(code, content)

fun MockResponse.mockSuccess(code: Int, content: String): MockResponse =
    this.setResponseCode(code).setBody(content)
