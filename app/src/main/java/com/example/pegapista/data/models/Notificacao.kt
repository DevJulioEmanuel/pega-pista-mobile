package com.example.pegapista.data.models

data class Notificacao (
    val id: String = "",
    val destinatarioId: String = "",
    val remetenteId: String = "",
    val remetenteNome: String = "",
    val remetenteFotoUrl: String = "",
    val tipo: TipoNotificacao = TipoNotificacao.SEGUIR,
    val postId: String? = null,
    val mensagem: String = "",
    val lida: Boolean = false,
    val data: Long = System.currentTimeMillis()
)

enum class TipoNotificacao {
    SEGUIR, CURTIDA, COMENTARIO
}
