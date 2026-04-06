package mx.edu.utez.jyps.utils

import android.os.Build
import android.util.Base64
import androidx.annotation.RequiresApi
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import mx.edu.utez.jyps.BuildConfig
import java.security.KeyFactory
import java.security.interfaces.RSAPrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import java.time.Instant
import java.util.Date
import java.util.UUID

/**
 * Temporary utility (Mock) to sign JWTs for the Google Wallet API from the Android client.
 * CRITICAL ALERT: The private RSA key should never reside on the mobile side in Production.
 * This object exists strictly for interactive UI debugging with credentials in local.properties.
 */
object WalletTokenMockGenerator {

    private const val ISSUER_ID = "3388000000023110105"
    private const val CLASS_ID = "pase_salida_empleado"

    /**
     * Recreates the asymmetric RSAPrivateKey object from the string key provided by Google Cloud,
     * stripping headers and line breaks.
     */
    private fun getPrivateKey(): RSAPrivateKey {
        val keyString = BuildConfig.WALLET_KEY
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replace("\\n", "")
            .replace("\n", "")
            .replace(" ", "")

        val encodedStream = Base64.decode(keyString, Base64.DEFAULT)
        val keySpec = PKCS8EncodedKeySpec(encodedStream)
        val kf = KeyFactory.getInstance("RSA")
        return kf.generatePrivate(keySpec) as RSAPrivateKey
    }

    /**
     * Builds and signs a JWT containing the exact credentials of the Exit Pass.
     *
     * @param passCode The pass identifier (e.g., "PASE015").
     * @param motive Description or reason for the pass.
     * @param employeeName Mock user name for the header.
     * @return Valid encoded JWT string.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun generateSignedWalletToken(passCode: String, motive: String, employeeName: String): String {
        val privateKey = getPrivateKey()
        val algorithm = Algorithm.RSA256(null, privateKey)
        val objectId = "$ISSUER_ID.${UUID.randomUUID()}"
        val absoluteClassId = "$ISSUER_ID.$CLASS_ID"

        // Strictly mapped payload for Google Wallet API GenericObjects
        val claims = mapOf(
            "genericObjects" to listOf(
                mapOf(
                    "id" to objectId,
                    "classId" to absoluteClassId,
                    "hexBackgroundColor" to "#162E5C", // Primary JYPS color
                    "logo" to mapOf(
                        "sourceUri" to mapOf("uri" to "https://www.utez.edu.mx/wp-content/uploads/2026/01/Logotipo-UTEZ-500x234.png")
                    ),
                    "cardTitle" to mapOf(
                        "defaultValue" to mapOf("language" to "es-MX", "value" to "Pase de Salida")
                    ),
                    "header" to mapOf(
                        "defaultValue" to mapOf("language" to "es-MX", "value" to employeeName)
                    ),
                    "barcode" to mapOf(
                        "type" to "qrCode", 
                        "value" to passCode,
                        "alternateText" to passCode
                    ),
                    "textModulesData" to listOf(
                        mapOf(
                            "id" to "motive_module",
                            "header" to "Motivo Autorizado",
                            "body" to motive
                        )
                    )
                )
            )
        )

        val now = Instant.now()
        return JWT.create()
            .withHeader(mapOf("typ" to "JWT", "alg" to "RS256"))
            .withClaim("iss", BuildConfig.WALLET_EMAIL)
            .withClaim("aud", "google")
            .withClaim("typ", "savetowallet")
            .withClaim("iat", Date.from(now)) // Issued At
            .withClaim("nbf", Date.from(now)) // Not Before
            .withClaim("exp", Date.from(now.plusSeconds(3600))) // Expiry in 1h (Required)
            .withClaim("origins", listOf("android-app://mx.edu.utez.jyps")) // Prevention for package mismatch
            .withClaim("payload", claims)
            .sign(algorithm)
    }
}
