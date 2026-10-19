package cl.duoc.mpazpanol.util

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/** Hash con sal para los usuarios ficticios de esta demostración local. */
object Passwords {
    private const val ITERACIONES = 120_000

    fun hash(password: String): String {
        val sal = ByteArray(16).also { SecureRandom().nextBytes(it) }
        val derivado = derivar(password, sal, ITERACIONES)
        return "$ITERACIONES:${Base64.encodeToString(sal, Base64.NO_WRAP)}:${Base64.encodeToString(derivado, Base64.NO_WRAP)}"
    }

    fun coincide(password: String, almacenado: String): Boolean {
        val partes = almacenado.split(":")
        if (partes.size != 3) return false
        return try {
            val iteraciones = partes[0].toInt()
            if (iteraciones !in 10_000..500_000) return false
            val sal = Base64.decode(partes[1], Base64.NO_WRAP)
            val esperado = Base64.decode(partes[2], Base64.NO_WRAP)
            MessageDigest.isEqual(esperado, derivar(password, sal, iteraciones))
        } catch (_: Exception) {
            false
        }
    }

    private fun derivar(password: String, sal: ByteArray, iteraciones: Int): ByteArray {
        val spec = PBEKeySpec(password.toCharArray(), sal, iteraciones, 256)
        return try {
            SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1").generateSecret(spec).encoded
        } finally {
            spec.clearPassword()
        }
    }
}
