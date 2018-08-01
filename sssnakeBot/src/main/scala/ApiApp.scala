

object ApiApp extends App {
    val apiCaller = new ApiCaller("src/main/config/coinbase_credentials.yml", "https://api.pro.coinbase.com/")

    val uri = apiCaller.buildUri("https", "api.pro.coinbase.com", "accounts")
    println(uri.toString)
    val headers = apiCaller.sign(uri)

    println(apiCaller.getRestContent(uri, headers))
}
