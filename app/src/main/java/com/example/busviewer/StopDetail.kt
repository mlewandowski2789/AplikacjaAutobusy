package com.example.busviewer

data class StopDetail(val stopId: String, val lines: List<Line>, val headsigns: Set<String>)