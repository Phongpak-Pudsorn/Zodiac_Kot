package com.starvision.models

data class AdsModel(
    val Status : String,
    val NodejsPortBegin : String,
    val NodejsPortEnd : String,
    val AppCheckInstallBanner : String,
    val Datarowpageconfig : ArrayList<DatarowConfig>,
//    val Datarowbannersmall : ArrayList<DatarowConfig>,
//    val Datarowbannermedium : ArrayList<DatarowConfig>,
//    val Datarowbannerbig : ArrayList<DatarowConfig>,
    val Datarowpublish : ArrayList<Datapublish>
){
    data class DatarowConfig(
        val Appadspackage : String,
        val Appadsos : String,
        val Mobileadvertisingname : String,
        val Apppageno : String,
        val Apppagename : String,
        val Apppageshow : String,
        val Datarowpagebanner : ArrayList<DatarowBanner>
    ){
        data class DatarowBanner(
            val Appbannerid : String,
            val Appbannername : String,
            val Appbannericonurl : String,
            val Multilinkid : String,
            val Multilink : String,
            val Multilinktimeskip : String,
            val Multilinkvdoposter : String,
            val Multilinkurlbrowser : String,
            val Linktype : String,
            val Multilinksendimeipackageosactive : String,
        )
    }
    data class Datapublish (
        val Appadspackage : String,
        val Appbannerid : Int,
        val Appbanneros : String,
        val Publishid : Int,
        val Publishtitle : String,
        val Publishdetail : String,
        val Publishlanguage : String,
        val Publishtype : String,
        val modificationdate : String,
        val creationdate : String
    )
}



//{
//    "Status": "True",
//    "NodejsPortBegin": "8880",
//    "NodejsPortEnd": "8889",
//    "AppCheckInstallBanner": "True",
//    "Datarowpageconfig": [
//    {
//        "Appadspackage": "com.starvision.guitareasy",
//        "Appadsos": "Android",
//        "Mobileadvertisingname": "admob",
//        "Apppageno": "0",
//        "Apppagename": "BannerSmall",
//        "Apppageshow": "True",
//        "Datarowpagebanner": [
//        {
//            "Appbannerid": 360,
//            "Appbannername": "LuckyGame",
//            "Appbannericonurl": "https:\/\/starvision.in.th\/mobileweb\/appsmartdirect\/bannersdk\/serverweb\/img\/bannersdk\/banner\/20230816101227_logo.png",
//            "Multilinkid": 266,
//            "Multilink": "https:\/\/starvision.in.th\/mobileweb\/appsmartdirect\/bannersdk\/serverweb\/img\/bannersdk\/banner\/20230816101227_small.jpg",
//            "Multilinktimeskip": "0",
//            "Multilinkvdoposter": "",
//            "Multilinkurlbrowser": "https:\/\/luckygame.in.th\/?appref=starvision",
//            "Linktype": "image_small",
//            "Multilinksendimeipackageosactive": "TRUE"
//        }
//        ]
//    }
//    ],
//    "Datarowbannersmall": [ ],
//    "Datarowbannermedium": [ ],
//    "Datarowbannerbig": [ ],
//    "Datarowpublish": [
//     {
//        "Appadspackage": "com.starvision.fengshui",
//        "Appbannerid": 269,
//        "Appbanneros": "Android",
//        "Publishid": 9296,
//        "Publishtitle": "ความหมายไม้มงคล 9 อย่าง พิธีลงเสาเอก เสริมความร่มเย็นให้บ้าน",
//        "Publishdetail": "https://www.starvision.in.th/mobileweb/webview_calendar/webview.php?OS=Android&p=artide_webview&id=2237",
//        "Publishlanguage": "TH",
//        "Publishtype": "LinkToURL",
//        "modificationdate": "2023-08-31 13:41:03.890504",
//        "creationdate": "2023-08-31 13:41:03.890504"
//    }
//    ]
//}
