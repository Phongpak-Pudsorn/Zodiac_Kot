package com.smileapp.zodiac.view.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.gson.Gson
import com.smileapp.zodiac.MyApplication
import com.smileapp.zodiac.R
import com.smileapp.zodiac.adapter.SpinnerAdapter
import com.smileapp.zodiac.commonclass.BannerShow
import com.smileapp.zodiac.commonclass.Font
import com.smileapp.zodiac.databinding.FragmentProfileBinding
import com.smileapp.zodiac.imagecrop.CropImage
import com.smileapp.zodiac.model.ZodiacInfo
import com.smileapp.zodiac.provider.InternalStorageContentProvider
import com.smileapp.zodiac.utils.Utils
import com.starvision.bannersdk.NoticeAds
import org.json.JSONException
import java.io.*
import java.util.concurrent.Executors

class ProfileFragment:Fragment() {
    var pressAble = true
    val TEMP_PHOTO_FILE_NAME = "temp_photo.jpg"
    val USER_PHOTO_FILE_NAME = "user_photo.jpg"
    var saveBitmap : Bitmap? = null
    var bannerShow:BannerShow?=null
    private var mFileTemp: File? = null
    private var mFileSave: File? = null
    var zodiacMain:ArrayList<ZodiacInfo.ZodiacData.MainData>?=null
    val executor = Executors.newSingleThreadExecutor()
    val handler = Handler(Looper.getMainLooper())
    var items = ArrayList<String>()
    var checkUserDataNull = false
    val binding: FragmentProfileBinding by lazy { FragmentProfileBinding.inflate(layoutInflater) }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        CallData()
        setNoticeAds()
        Font().styleText_RSU_BOLD(requireActivity(),binding.TvTitle,32)
        Utils.setTextGradient_Blue(binding.TvZodiac)
        Utils.setTextGradient_Blue(binding.TvName)
        Utils.setTextGradient_Blue(binding.TvGender)
        val path = requireActivity().getExternalFilesDir(null)
        mFileTemp = File(path,TEMP_PHOTO_FILE_NAME)
        if (mFileTemp!!.exists()) {
//            Log.e("mFileTemp exist", mFileTemp!!.path.toString())
            val myBitmap = BitmapFactory.decodeFile(mFileTemp!!.path)
            binding.mImguser.setImageBitmap(myBitmap)
        }
        binding.mImguser.setOnClickListener {
            openAddPhoto()
        }
        binding.ImgStart.setOnClickListener {
            if (saveBitmap!=null){
                mFileSave = File(path,USER_PHOTO_FILE_NAME)
                val fOut = FileOutputStream(mFileSave)
                saveBitmap!!.compress(Bitmap.CompressFormat.JPEG,100,fOut)
                fOut.flush()
                fOut.close()
            }
            Utils.setNameUser(binding.mEdittext.text.toString())
            val gender = binding.rdGender.checkedRadioButtonId
            if (Utils.getNameUser()==""){
                Toast.makeText(MyApplication.getContext(),"กรุณาใส่ชื่อของคุณ",Toast.LENGTH_SHORT).show()
                checkUserDataNull = true
            }else{
                checkUserDataNull = false
                when (gender) {
                    binding.rdMan.id -> {
            //                    Log.e("man",binding.rdMan.id.toString())
                        val strGender = "Man"
                        Utils.setGENDER(strGender)
                        checkUserDataNull = false
                    }
                    binding.rdWoman.id -> {
            //                    Log.e("woman",binding.rdWoman.id.toString())
                        val strGender = "Woman"
                        Utils.setGENDER(strGender)
                        checkUserDataNull = false
                    }
                    else -> {
                        Toast.makeText(MyApplication.getContext(),"กรุณาระบุเพศของคุณ",Toast.LENGTH_SHORT).show()
                        checkUserDataNull = true
                    }
                }
            }
            if(!checkUserDataNull){
                Utils.setNameAndDateRasi(zodiacMain!![Utils.getRasi()].zodiac_name_thai+" "+zodiacMain!![Utils.getRasi()].zodiac_name_eng+"\n"+zodiacMain!![Utils.getRasi()].zodiac_date)
                Navigation.findNavController(requireView())
                    .navigate(R.id.action_profileFragment_to_mainFragment)
            }
        }
        requireActivity().onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (pressAble) {
                        pressAble = false
                        Navigation.findNavController(requireView()).navigate(R.id.action_profileFragment_to_mainFragment2)
                    }
                }
            })
        binding.SpinDate.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long,
            ) {
                Utils.setRasi(position)
                // mySpinner.setSelection(mAppPreference.getRari());
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }
    fun CallData(){
        executor.execute {
            val zodiacStr = Utils.loadFromAssets(requireActivity())
            items.clear()
            try {
                val zodiacData = Gson().fromJson(zodiacStr, ZodiacInfo::class.java)
                zodiacMain = zodiacData.Data_Zodiac.zodiac_main

            }catch (e:JSONException){
                e.printStackTrace()
            }


            handler.post{
                if (zodiacMain!!.size >0) {
                    for (i in zodiacMain!!.indices) {
                        items.add(zodiacMain!![i].zodiac_name_thai + " " + zodiacMain!![i].zodiac_name_eng + " " + zodiacMain!![i].zodiac_date)
                    }
//                    Log.e("CallData",items.toString())
                }
                binding.SpinDate.adapter = SpinnerAdapter(requireActivity(),R.layout.row,items)
            }
        }

    }
    private fun openAddPhoto() {
//        if (mFileTemp != null) {
//            mFileTemp.delete();
//        }
        val addPhoto = arrayOf(
            resources.getString(R.string.select_img_camera),
            resources.getString(R.string.select_img_gallery),
            resources.getString(R.string.text_close)
        )
        val dialog = AlertDialog.Builder(requireActivity())
        dialog.setTitle(resources.getString(R.string.app_name))
        dialog.setItems(addPhoto) { dialog, id ->
            if (id == 0) {
                takePicture()
            } else if (id == 1) {
                openGallery()
            } else if (id == 2) {
                dialog.dismiss()
            }
        }
        //        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//            @Override
//            public void onCancel(DialogInterface dialog) {
//                onCreate(null);
//            }
//        });
        dialog.show()
    }
    private fun takePicture(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try{
            var mImageCaptureUri: Uri?
            val state = Environment.getExternalStorageState()
            mImageCaptureUri = if (Environment.MEDIA_MOUNTED == state){
//                Log.e("takePicture","if")
//                Log.e("mFileTemp takePicture", mFileTemp!!.path.toString())
                FileProvider.getUriForFile(requireActivity(),"com.smileapp.zodiac.provider", mFileTemp!!)
            }else{
//                Log.e("takePicture","else")
                InternalStorageContentProvider().CONTENT_URI
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT,mImageCaptureUri)

            startTakeResult.launch(intent)
        }catch (e: ActivityNotFoundException){
//            Log.e("cannot take picture",e.message.toString())
        }
    }
    private fun openGallery(){
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startGalleryResult.launch(photoPickerIntent)

    }
    private fun startCropImage() {
//        Log.e("mFileTemp startCropImage", mFileTemp!!.path.toString())
        val intent = Intent(requireActivity(), CropImage::class.java)
        intent.putExtra(CropImage().IMAGE_PATH, mFileTemp!!.path)
        intent.putExtra(CropImage().SCALE, true)
        intent.putExtra(CropImage().ASPECT_X, 3)
        intent.putExtra(CropImage().ASPECT_Y, 3)
        startCropResult.launch(intent)
    }
    private val startCropResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result: androidx.activity.result.ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK){
            val path = result.data?.getStringExtra(CropImage().IMAGE_PATH)
//            Log.e("image bitmap",CropImage().IMAGE_PATH)
            if (path == null) {
//                Log.e("path","null")
            }
            val myBitmap = BitmapFactory.decodeFile(mFileTemp!!.path)
            saveBitmap = myBitmap
            binding.mImguser.setImageBitmap(myBitmap)
        }

    }
    private val startGalleryResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result: androidx.activity.result.ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK){
            try {
                val inputStream: InputStream = context!!.contentResolver.openInputStream(result.data!!.data!!)!!
                val outputStream = FileOutputStream(mFileTemp)
                copyStream(inputStream, outputStream)
                inputStream.close()
                outputStream.close()
                startCropImage()
            }catch (e:Exception){
//                Log.e("REQUEST_CODE_GALLERY","Error while creating temp file",e)
            }

        }

    }
    private val startTakeResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result: androidx.activity.result.ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK){
//            Log.e("mFileTemp startTakeResult", mFileTemp!!.path.toString())
            startCropImage()
        }

    }
    @Throws(IOException::class)
    fun copyStream(input: InputStream, output: OutputStream) {
        val buffer = ByteArray(1024)
        var bytesRead: Int
        while (input.read(buffer).also { bytesRead = it } != -1) {
            output.write(buffer, 0, bytesRead)
        }
    }

    fun setNoticeAds(){
        binding.noticeAds.setNoticeAdsListener(object : NoticeAds.NoticeAdsListener{
            override fun onSuccess(strJson: String?) {
//                Log.e("noticeAds", "noticeAds onSuccessListener: strJson "+strJson)
                if (!Utils.getNoticeAds()) {
                    binding.noticeAds.visibility = View.VISIBLE
                }else{
                    binding.noticeAds.visibility = View.GONE
                }
            }

            override fun onBannerClick(strJson: String?) {
//                Log.e("noticeAds", "noticeAds onBannerClick: strJson "+strJson)
            }

            override fun onFailed(strErrorMessage: String?) {
//                Log.e("noticeAds", "noticeAds onFailedListener: strErrorMessage "+strErrorMessage)
                binding.noticeAds.visibility = View.GONE
            }

            override fun onClose() {
//                Log.e("noticeAds", "noticeAds onClose")
                Utils.setNoticeAds(true)
                binding.noticeAds.visibility = View.GONE
            }
        })
        binding.noticeAds.loadAds(Utils.UUID)
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