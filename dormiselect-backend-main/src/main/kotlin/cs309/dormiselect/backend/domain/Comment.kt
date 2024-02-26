package cs309.dormiselect.backend.domain

import cs309.dormiselect.backend.domain.account.Account
import cs309.dormiselect.backend.domain.account.Student
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import java.sql.Timestamp
import kotlin.reflect.jvm.internal.impl.descriptors.deserialization.PlatformDependentDeclarationFilter.All

@Entity
class Comment(
    @ManyToOne(optional = false) val dormitory: Dormitory,
    @ManyToOne(optional = false) val author: Account,
    var content: String,
) {
    @Id
    @GeneratedValue
    val id: Int? = null
    var likeNum: Int = 0

    fun like() {
        likeNum++
    }

    fun dislike(){
        likeNum--
    }
//    @OneToMany
//    val replies: MutableList<Reply> = mutableListOf()
    val postTime: Timestamp = Timestamp(System.currentTimeMillis())

}