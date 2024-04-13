package gg.skytils.skytilsmod.utils

object TimeUtils {
    fun randomDelay(minDelay: Int, maxDelay: Int): Long {
        return RandomUtils.nextInt(minDelay, maxDelay).toLong()
    }

    fun randomClickDelay(minCPS: Int, maxCPS: Int): Long {
        return (Math.random() * (1000 / minCPS - 1000 / maxCPS + 1) + 1000 / maxCPS).toLong()
    }
}