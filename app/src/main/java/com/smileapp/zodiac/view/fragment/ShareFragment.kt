package com.smileapp.zodiac.view.fragment

import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.smileapp.zodiac.R
import com.smileapp.zodiac.commonclass.BannerShow
import com.smileapp.zodiac.commonclass.Font
import com.smileapp.zodiac.databinding.FragmentShareBinding
import com.smileapp.zodiac.utils.Utils
import java.io.File
import java.io.OutputStream

class ShareFragment:Fragment() {
    val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            shareImageView()
//            Log.e("requestPermissionLauncher","if")
            // FCM SDK (and your app) can post notifications.
        } else {
//            Log.e("requestPermissionLauncher","else")
            // TODO: Inform user that that your app will not show notifications.
        }
    }
    var bannerShow: BannerShow?=null
    val binding:FragmentShareBinding by lazy { FragmentShareBinding.inflate(layoutInflater) }
    var check = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bannerShow = BannerShow(requireActivity(), Utils.UUID)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.imgBack.setOnClickListener {
            Navigation.findNavController(requireView()).navigateUp()
        }
        Font().styleText_RSU_BOLD(requireActivity(),binding.TvTitle,32)
        val img = requireActivity().resources.getIdentifier(Utils.getPredictRasi(),"mipmap",requireActivity().packageName)
        binding.imgDodiac.setImageResource(img)
        binding.TvNameRasi.text = Utils.getPredictName()
        binding.TvNameButton.text = Utils.getPredictMenu()
        binding.TvNameButton.setTextColor(Color.WHITE)
        Utils.setTextGradient_Menu_Black_Blue(binding.TvNameRasi)
        Font().styleText_RSU_BOLD(requireActivity(),binding.TvNameRasi,32)
        Font().styleText_RSU_BOLD(requireActivity(),binding.TvNameButton,32)
        binding.mWvDescription.setBackgroundColor(Color.TRANSPARENT)
        binding.mWvDescription.loadDataWithBaseURL("file:///android_res/drawable/",Utils.getHtmlData_While(requireActivity(),Utils.getPredictDesc()+ "<div  align=\"right\"><font  size=\"5\" color=\"red\">" + getString(
            R.string.text_share_nameapp_android) + "</font><div >", 28).toString(), null
            , "utf - 8"
            , null)
        binding.btnShare.setOnClickListener {
            if (!check){
                check = true
                askSharePermission()
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
    private fun askSharePermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.READ_MEDIA_IMAGES) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                shareImageView()
                // FCM SDK (and your app) can post notifications.
//            Log.e("checkSelfPermission","True")
            } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.READ_MEDIA_IMAGES)) {
                requestPermissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
//            Log.e("checkSelfPermission","else if")
            } else {
                // Directly ask for the permission
//            Log.e("checkSelfPermission","else")
                requestPermissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
            }
        }else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
            shareImageView()
        }else{
            if (ContextCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                shareImageView()
                // FCM SDK (and your app) can post notifications.
//            Log.e("checkSelfPermission","True")
            } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                requestPermissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
//            Log.e("checkSelfPermission","else if")
            } else {
                // Directly ask for the permission
//            Log.e("checkSelfPermission","else")
                requestPermissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }

        }
        check = false
    }
    private fun insertImage():Uri{
        binding.LiCropShare.visibility = View.VISIBLE
        val bitmap = loadBitmapFromView(binding.LiCropShare)
        val fos: OutputStream
        val resolver = requireActivity().contentResolver
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, System.currentTimeMillis().toString() + ".png")
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){
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
    private fun loadBitmapFromView(v:View):Bitmap{
        val image = Bitmap.createBitmap(v.width,v.height,Bitmap.Config.ARGB_8888)
        val c = Canvas(image)
        v.layout(v.left,v.top,v.right,v.bottom)
        v.draw(c)
        return image
    }


    override fun onResume() {
        if (!Utils.showBanner) {
            Utils.showBanner = true
            bannerShow!!.getShowBannerSmall(10)
        }
        super.onResume()
    }
}