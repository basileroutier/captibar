package com.example.mobg_54018.model

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

/* The BarItem class implements the ClusterItem interface, which is used by the ClusterManager class to
manage clusters of items on the map */
class BarItem : ClusterItem {
    private val position: LatLng
    private val title: String
    private val snippet: String

    constructor(position: LatLng, title: String, snippet: String) {
        this.position = position
        this.title = title
        this.snippet = snippet
    }

    override fun getPosition(): LatLng {
        return position
    }

    override fun getTitle(): String {
        return title
    }

    override fun getSnippet(): String {
        return snippet
    }

}