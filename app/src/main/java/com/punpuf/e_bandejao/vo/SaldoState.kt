package com.punpuf.e_bandejao.vo

data class SaldoState(
    val opType: OP_TYPE,
    val id: Int,
    val extraAmount: Double = 0.0,
    val extraId: String = "",
) {
    @Suppress("ClassName")
    enum class OP_TYPE {
        GET,
        CREATE,
        DELETE,
        NO_USER,
    }
}