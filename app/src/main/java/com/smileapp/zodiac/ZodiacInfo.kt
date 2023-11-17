package com.smileapp.zodiac

data class ZodiacInfo(val Data_Zodiac:ZodiacData) {
    data class ZodiacData(val zodiac_main:ArrayList<MainData>,
                          val zodiac_general:GeneralData,
                          val zodiac_love:LoveData){
        data class MainData(val zodiac_id:String,
                            val zodiac_name_thai:String,
                            val zodiac_name_eng:String,
                            val zodiac_date:String,
                            val zodiac_image:String)
        data class GeneralData(val zodiac_1:ArrayList<GeneralDetails>,
                               val zodiac_2:ArrayList<GeneralDetails>,
                               val zodiac_3:ArrayList<GeneralDetails>,
                               val zodiac_4:ArrayList<GeneralDetails>,
                               val zodiac_5:ArrayList<GeneralDetails>,
                               val zodiac_6:ArrayList<GeneralDetails>,
                               val zodiac_7:ArrayList<GeneralDetails>,
                               val zodiac_8:ArrayList<GeneralDetails>,
                               val zodiac_9:ArrayList<GeneralDetails>,
                               val zodiac_10:ArrayList<GeneralDetails>,
                               val zodiac_11:ArrayList<GeneralDetails>,
                               val zodiac_12:ArrayList<GeneralDetails>){
            data class GeneralDetails(val zodiac_menu:String,
                                      val zodiac_description:String)
        }
        data class LoveData(val zodiac_1:ArrayList<LoveDetails>,
                            val zodiac_2:ArrayList<LoveDetails>,
                            val zodiac_3:ArrayList<LoveDetails>,
                            val zodiac_4:ArrayList<LoveDetails>,
                            val zodiac_5:ArrayList<LoveDetails>,
                            val zodiac_6:ArrayList<LoveDetails>,
                            val zodiac_7:ArrayList<LoveDetails>,
                            val zodiac_8:ArrayList<LoveDetails>,
                            val zodiac_9:ArrayList<LoveDetails>,
                            val zodiac_10:ArrayList<LoveDetails>,
                            val zodiac_11:ArrayList<LoveDetails>,
                            val zodiac_12:ArrayList<LoveDetails>){
            data class LoveDetails(val zodiac_menu:String,
                                  val zodiac_description:String)
        }
    }
}