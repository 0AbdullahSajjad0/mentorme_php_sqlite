package com.abdullahsajjad.i212477

import java.io.Serializable

data class Reviews(
    var reviewId: String? = null,
    var mentorId: String? = null,
    var mentorname: String? = null,
    var userId: String? = null,
    var feedback: String? = null,
    var rating: Int? = null

) : Serializable

