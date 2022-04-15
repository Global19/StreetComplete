package de.westnordost.streetcomplete.layers.way_lit

import de.westnordost.streetcomplete.data.osm.mapdata.MapDataWithGeometry
import de.westnordost.streetcomplete.data.osm.mapdata.filter
import de.westnordost.streetcomplete.layers.Color
import de.westnordost.streetcomplete.layers.Layer
import de.westnordost.streetcomplete.layers.PolylineStyle
import de.westnordost.streetcomplete.osm.ALL_PATHS
import de.westnordost.streetcomplete.osm.ALL_ROADS

class WayLitLayer : Layer {

    override fun getStyledElements(mapData: MapDataWithGeometry) =
        mapData
            .filter("ways with highway ~ ${(ALL_ROADS + ALL_PATHS).joinToString("|")}")
            .map { it to PolylineStyle(createLitStatus(it).color) }
}

private val LitStatus?.color: String get() = when (this) {
    LitStatus.YES ->           "#ccff00"
    LitStatus.NIGHT_AND_DAY -> "#33ff00"
    LitStatus.AUTOMATIC ->     "#00aaff"
    LitStatus.NO ->            "#555555"
    LitStatus.UNSUPPORTED ->   Color.UNSUPPORTED
    null ->                    Color.UNSPECIFIED
}