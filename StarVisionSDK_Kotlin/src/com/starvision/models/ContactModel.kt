package com.starvision.models

data class ContactModel(
    var machineIMEI: String? = null,
    var appAdsPackage: String? = null,
    var appBannerId: String? = null,
    var appBannerCountry: String? = null,
    var checked: String? = null,
    val DataMachine: DataDetailRow? = null){

    data class DataDetailRow(
        var IMEI_UDID : String? = null,
        var Device_Name : String? = null,
        var System_Version : String? = null,
        var Platform : String? = null,
        var Model : String? = null,
        var Brand : String? = null,
        var OS : String? = null)
}
