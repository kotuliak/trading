object ApiApp extends App {
    val apiCaller = new ApiCaller

    val uri = apiCaller.buildUri("https", "api.pro.coinbase.com", "accounts")
    println(uri.toString)
    val headers = apiCaller.sign(uri)

    println(apiCaller.getRestContent(uri, headers))
}
