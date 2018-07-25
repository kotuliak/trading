import java.net.URI
import java.util.Base64

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import org.apache.http.client.methods.RequestBuilder
import org.apache.http.client.utils.URIBuilder
import org.apache.http.impl.client.HttpClients

class ApiCaller {

    val KEY = "1e85e9d76f1ce7bbfe9f598b583f551b"
    val SECRET = "l1MY2cRklE3XuHIOXTAiifOMJM7chvGoa2EO3laB03zMTSd/vHluuRhIVZTFZNiMSJTrKc0PJ2qQr0TAsbpcLg=="
    val PASS = "LauncherDragon123!"

    val url = "https://api.pro.coinbase.com/"

    def buildUri(scheme:String, host: String, endpoint:String, params: Map[String, String] = Map()): URI = {
        var uri = new URIBuilder()
            .setScheme(scheme)
            .setHost(host)
            .setPath(endpoint)
        params.map(m => uri.addParameter(m._1,m._2))

        println(uri.getPath)
        uri.build()
    }

    def getRestContent(uri: URI, headers: Map[String, String] = Map()): String = {
        val httpClient = HttpClients.createDefault
        val request = RequestBuilder.get()
            .setUri(uri)

        headers.map(h => request.setHeader(h._1, h._2))

        val httpResponse = httpClient.execute(request.build)
        val entity = httpResponse.getEntity
        var content = ""
        if (entity != null) {
            val inputStream = entity.getContent
            content = io.Source.fromInputStream(inputStream).getLines.mkString
            inputStream.close
        }
        httpClient.getConnectionManager.shutdown
        content
    }

    def sign(uri: URI): Map[String, String] = {
        val timestamp = "%.2f".format(System.currentTimeMillis() / 1000.0)

        val message = timestamp + "GET" + uri.getPath
        println(message)

        val sha256_HMAC = Mac.getInstance("HmacSHA256")
        val secretKey = new SecretKeySpec(Base64.getDecoder.decode(SECRET), "HmacSHA256")
        sha256_HMAC.init(secretKey)

        val hash = Base64.getEncoder.encodeToString(sha256_HMAC.doFinal(message.getBytes()))

        println(hash)

        var headers = Map(
            "CB-ACCESS-SIGN" -> hash,
            "CB-ACCESS-TIMESTAMP" -> timestamp.toString,
            "CB-ACCESS-KEY" -> KEY,
            "CB-ACCESS-PASSPHRASE" -> PASS,
            "Content-Type" -> "application/json"
        )
        headers
    }

}
