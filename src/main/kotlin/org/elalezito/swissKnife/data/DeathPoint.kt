package org.elalezito.swissKnife.data

data class DeathPoint(
	val world_key: String,
	val x: Int,
	val y: Int,
	val z: Int,
	val timestamp: Long = System.currentTimeMillis()
) {
	fun isExpired(timeoutSeconds: Int = 1800): Boolean {
		val expiryMillis = timeoutSeconds * 1000
		return System.currentTimeMillis() - timestamp > expiryMillis
	}
}
