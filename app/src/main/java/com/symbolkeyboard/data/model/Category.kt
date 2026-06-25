package com.symbolkeyboard.data.model

enum class Category(
    val displayName: String,
    val key: String
) {
    CROSSES_AND_RELIGIOUS("Crosses & Religious", "religious"),
    ANIMALS("Animals", "animals"),
    ANCIENT_SCRIPTS("Ancient Scripts", "ancient"),
    ALCHEMICAL("Alchemical", "alchemical"),
    CHESS("Chess", "chess"),
    MUSICAL("Musical", "musical"),
    ASTROLOGICAL("Astrological", "astrological"),
    ARROWS("Arrows", "arrows"),
    GEOMETRIC("Geometric", "geometric"),
    DINGBATS("Dingbats", "dingbats"),
    MISCELLANEOUS("Miscellaneous", "misc");

    companion object {
        fun fromKey(key: String): Category? = entries.firstOrNull { it.key == key }

        fun fromDisplayName(name: String): Category? = entries.firstOrNull { it.displayName == name }
    }
}
