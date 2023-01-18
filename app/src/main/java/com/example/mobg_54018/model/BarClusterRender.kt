package com.example.mobg_54018.model

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer

/* This class is a custom renderer for the ClusterManager class. It is used to render the clusters on
the map. */
class BarClusterRender (context: Context, map: GoogleMap, clusterManager: ClusterManager<BarItem>)
    : DefaultClusterRenderer<BarItem>(context, map, clusterManager) {

    override fun shouldRenderAsCluster(cluster: Cluster<BarItem>): Boolean {
        //start clustering if at least 2 items overlap
        return cluster.size > 1
    }
}