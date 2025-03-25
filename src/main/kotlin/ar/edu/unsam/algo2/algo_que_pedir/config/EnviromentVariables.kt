package ar.edu.unsam.algo2.algo_que_pedir.config

object EnviromentVariables {
    val DEFAULT_APP_COMISION: Int = System.getenv("DEFAULT_APP_COMISION")?.toIntOrNull() ?: 10
    val MAX_DAYS_TO_CALIFICATE = System.getenv("MAX_DAYS_TO_CALIFICATE")?.toLongOrNull() ?: 7
    val MIN_CONFIABLE_STORE_SCORE = System.getenv("MINCONFIABLESTORESCORE")?.toDoubleOrNull() ?: 4.0
    val MIN_PRICE = System.getenv("MINPRICE")?.toDoubleOrNull() ?: 30000.0
    val MIN_CLIENT_YEARS_OLD = (System.getenv("MINOLDCLIENTYEARS")?.toLongOrNull() ?: 1)
}