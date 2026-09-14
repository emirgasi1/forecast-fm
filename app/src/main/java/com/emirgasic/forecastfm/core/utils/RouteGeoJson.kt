package com.emirgasic.forecastfm.core.utils

object RouteGeoJson {

    fun buildRouteJson(points: List<Pair<Double, Double>>): String {
        val coords = points.joinToString(",") { (lat, lng) ->
            "[$lng,$lat]"
        }
        return """
            {
                "type": "FeatureCollection",
                "features": [
                    {
                        "type": "Feature",
                        "geometry": {
                            "type": "LineString",
                            "coordinates": [$coords]
                        },
                        "properties": {}
                    }
                ]
            }
        """.trimIndent()
    }

    fun buildPointJson(lat: Double, lng: Double): String {
        return """
            {
                "type": "FeatureCollection",
                "features": [
                    {
                        "type": "Feature",
                        "geometry": {
                            "type": "Point",
                            "coordinates": [$lng,$lat]
                        },
                        "properties": {}
                    }
                ]
            }
        """.trimIndent()
    }
}