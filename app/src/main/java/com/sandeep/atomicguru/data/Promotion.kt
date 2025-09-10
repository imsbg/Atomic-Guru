package com.sandeep.atomicguru.data

data class Promotion(
    val id: Int,
    val imageUrl: String,
    val destinationUrl: String,
    val altText: String
)

data class PromotionResponse(
    val promotions: List<Promotion>
)