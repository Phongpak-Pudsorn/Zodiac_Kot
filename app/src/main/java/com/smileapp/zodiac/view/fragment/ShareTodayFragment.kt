package com.smileapp.zodiac.view.fragment

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.smileapp.zodiac.R
import com.smileapp.zodiac.commonclass.BannerShow
import com.smileapp.zodiac.commonclass.Font
import com.smileapp.zodiac.databinding.FragmentShareTodayBinding
import com.smileapp.zodiac.utils.Utils
import java.io.File
import java.io.OutputStream

class ShareTodayFragment:Fragment() {
    var bannerShow:BannerShow?=null
    val binding:FragmentShareTodayBinding by lazy { FragmentShareTodayBinding.inflate(layoutInflater) }
    var check = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.TvDay.text = Utils.getSharedDay()
        Font().styleText_RSU_BOLD(requireActivity(),binding.TvTitle,32)
        Font().styleText_RSU_BOLD(requireActivity(),binding.TvDay,22)
        Utils.setStringImageDrawable(requireActivity(), binding.imgDay, Utils.getSharedImage(), 300, 215)
        binding.mWvDescription.setBackgroundColor(Color.TRANSPARENT)
        binding.mWvDescription.loadDataWithBaseURL(null,
            Utils.getHtmlData_While(requireActivity(), Utils.getSharedDesc()+"" +"<div  align=\"right\"><font  size=\"5\" color=\"red\">"+getString(
                R.string.text_share_nameapp_android)+"</font><div >", 26)!!, null, "utf-8", null)
        binding.imgBack.setOnClickListener {
            Navigation.findNavController(requireView()).navigateUp()
        }
        binding.btnShare.setOnClickListener {
            if (!check){
                check=true
                shareImageView()
            }
        }
    }
    private fun shareImageView(){
        val urlImage = insertImage()
        val share = Intent(Intent.ACTION_SEND)
        share.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        share.putExtra(Intent.EXTRA_STREAM,urlImage)
        share.type = "image/jpeg"
        share.putExtra(Intent.EXTRA_TEXT,getString(R.string.text_share_hashtag))
        startActivity(Intent.createChooser(share,"Share Image"))
    }
    private fun insertImage(): Uri {
        binding.LiCropShare.visibility = View.VISIBLE
        val bitmap = loadBitmapFromView(binding.LiCropShare)
        val fos: OutputStream
        val resolver = requireActivity().contentResolver
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, System.currentTimeMillis().toString() + ".png")
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.Q){
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + "Zodiac")
        }
        contentValues.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis())
        contentValues.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
        val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        try {
            fos = resolver.openOutputStream(imageUri!!)!!
            bitmap.compress(Bitmap.CompressFormat.PNG,100,fos)
            fos.close()
            Toast.makeText(requireActivity(), "Save images success...", Toast.LENGTH_LONG).show()
        }catch (e:Exception){
            e.printStackTrace()
            Toast.makeText(requireActivity(), "Save images failed...", Toast.LENGTH_LONG).show()
        }
        return imageUri!!
    }
    private fun loadBitmapFromView(v:View): Bitmap {
        val image = Bitmap.createBitmap(v.width,v.height, Bitmap.Config.ARGB_8888)
        val c = Canvas(image)
        v.layout(v.left,v.top,v.right,v.bottom)
        v.draw(c)
        return image
    }

    override fun onStart() {
        bannerShow = BannerShow(requireActivity(), Utils.UUID)
        super.onStart()
    }

    override fun onResume() {
        bannerShow!!.getShowBannerSmall(10)
        super.onResume()
    }
}