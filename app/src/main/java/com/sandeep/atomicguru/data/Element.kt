package com.sandeep.atomicguru.data

import com.google.gson.annotations.SerializedName

// This class represents the top-level structure of your JSON file.
data class Elements(
    val elements: List<Element>
)

// This class represents a single element object in the main array.
data class Element(
    val atomicNumber: Int,
    val symbol: String,
    val name: String,
    val source: String,
    val xpos: Int,
    val ypos: Int,
    // --- THIS IS THE ADDED FIELD ---
    // Making it nullable (String?) allows it to be optional in your JSON.
    val name_oe: String?,
    // --- END OF ADDED FIELD ---
    @SerializedName("details_en") val detailsEn: ElementDetails,
    @SerializedName("details_odia") val detailsOdia: ElementDetails
)

// This class represents the structure of the "details_en" and "details_odia" objects.
data class ElementDetails(
    @SerializedName("general_info") val generalInfo: GeneralInfo,
    @SerializedName("physical_properties") val physicalProperties: PhysicalProperties,
    @SerializedName("chemical_properties") val chemicalProperties: List<String>,
    val occurrence: List<String>,
    val uses: List<String>,
    @SerializedName("detailed_description") val detailedDescription: String
)

// This class represents the "general_info" block.
data class GeneralInfo(
    @SerializedName("element_name") val elementName: String,
    val symbol: String,
    @SerializedName("atomic_number") val atomicNumber: String,
    @SerializedName("atomic_mass") val atomicMass: String,
    val category: String,
    @SerializedName("group_period") val groupPeriod: String,
    val appearance: String
)

// This class represents the "physical_properties" block.
data class PhysicalProperties(
    @SerializedName("melting_point") val meltingPoint: String,
    @SerializedName("boiling_point") val boilingPoint: String,
    val density: String,
    @SerializedName("malleability_ductility") val malleabilityDuctility: String,
    val conductivity: String
)