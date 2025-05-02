package fr.ceri.amiboprojetfinal.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class Amiibo : RealmObject {
    @PrimaryKey
    var id: String = ""
    var name: String = ""
    var image: String = ""
    var gameSeries: String = ""
    var type: String = ""
}