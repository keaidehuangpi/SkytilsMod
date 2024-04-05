/*
 * Skytils - Hypixel Skyblock Quality of Life Mod
 * Copyright (C) 2020-2024 Skytils
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package gg.skytils.skytilsmod.cape

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import gg.skytils.skytilsmod.Skytils
import java.io.File
import java.io.FileInputStream
import javax.imageio.ImageIO

object GuiCapeManager {

    val PRETTY_GSON = GsonBuilder().setPrettyPrinting().create()
    private val jsonFile = File(Skytils.capesDir, "cape.json")

    private val embeddedCapes = mutableListOf<ICape>()

    var nowCape: ICape?
    val capeList = mutableListOf<ICape>()

    init {
        arrayOf(
            "huiwow",
            "2011",
            "2012",
            "2013",
            "2015",
            "CherryBlossom",
            "HDGalary",
            "MinecraftMarketPlace",
            "MojangStudios",
            "NewMojang",
            "OldMojang",
            "Vanilla",
            "Rickroll"
        ).forEach {
            try {
                embeddedCapes.add(loadCapeFromResource(it, "assets/skytils/capes/$it.png"))
            } catch (e: Throwable) {
                System.out.println("Failed to load Capes")
            }
        }
        nowCape = embeddedCapes.random()
        pushEmbeddedCape()
    }

    private fun pushEmbeddedCape() {
        capeList.addAll(embeddedCapes)
    }

    fun load() {
        capeList.clear()

        pushEmbeddedCape()

        // add capes from files
        for (file in Skytils.capesDir.listFiles()!!) {
            if (file.isFile && !file.name.equals(jsonFile.name)) {
                try {
                    val args = file.name.split(".").toTypedArray()
                    val name = java.lang.String.join(".", *args.copyOfRange(0, args.size - 1))
                    capeList.add(
                        if (args.last() == "gif") {
                            loadGifCapeFromFile(name, file)
                        } else {
                            loadCapeFromFile(name, file)
                        }
                    )
                } catch (e: Exception) {
                    println("Occurred an error while loading cape from file: ${file.name}")
                    e.printStackTrace()
                }
            }
        }

        if (!jsonFile.exists()) {
            return
        }


        val json = JsonParser().parse(jsonFile.readText(Charsets.UTF_8)).asJsonObject


        if (json.has("name")) {
            val name = json.get("name").asString
            if (!name.equals("NONE")) {
                val result = capeList.find { it.name == name } ?: embeddedCapes.random()
                nowCape = result
            }
        }
    }

    fun save() {
        val json = JsonObject()

        json.addProperty(
            "name", if (nowCape != null) {
                nowCape!!.name
            } else {
                "NONE"
            }
        )
        jsonFile.writeText(PRETTY_GSON.toJson(json), Charsets.UTF_8)
    }

    private fun loadCapeFromResource(name: String, loc: String) =
        SingleImageCape(name, ImageIO.read(GuiCapeManager::class.java.classLoader.getResourceAsStream(loc)))

    private fun loadCapeFromFile(name: String, file: File) = SingleImageCape(name, ImageIO.read(file))

    private fun loadGifCapeFromResource(name: String, loc: String) =
        GuiCapeManager::class.java.classLoader.getResourceAsStream(loc)?.let { GifCape(name, it) }

    private fun loadGifCapeFromFile(name: String, file: File) = GifCape(name, FileInputStream(file))

}
