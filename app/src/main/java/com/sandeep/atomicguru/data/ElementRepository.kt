package com.sandeep.atomicguru.data

import android.content.Context
import com.google.gson.Gson
import java.io.InputStreamReader

class ElementRepository(private val context: Context) {

    val allElements: List<Element> by lazy {
        loadElementsFromJson()
    }

    private fun loadElementsFromJson(): List<Element> {
        context.assets.open("periodic_table.json").use { inputStream ->
            InputStreamReader(inputStream).use { reader ->
                return Gson().fromJson(reader, Elements::class.java).elements
            }
        }
    }

    fun getElementByNumber(atomicNumber: Int): Element? {
        return allElements.find { it.atomicNumber == atomicNumber }
    }

    private val prefs = context.getSharedPreferences("favorites_prefs", Context.MODE_PRIVATE)

    fun getFavoriteElementIds(): Set<String> {
        return prefs.getStringSet("favorite_ids", emptySet()) ?: emptySet()
    }

    fun toggleFavorite(atomicNumber: Int) {
        val currentFavorites = getFavoriteElementIds().toMutableSet()
        val idString = atomicNumber.toString()
        if (currentFavorites.contains(idString)) {
            currentFavorites.remove(idString)
        } else {
            currentFavorites.add(idString)
        }
        prefs.edit().putStringSet("favorite_ids", currentFavorites).apply()
    }
}