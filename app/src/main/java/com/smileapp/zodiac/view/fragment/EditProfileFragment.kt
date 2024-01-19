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
import com.smileapp.zodiac.databinding.FragmentEditprofileBinding
import com.smileapp.zodiac.imagecrop.CropImage
import com.smileapp.zodiac.model.ZodiacInfo
import com.smileapp.zodiac.provider.InternalStorageContentProvider
import com.smileapp.zodiac.utils.Utils

import org.json.JSONException
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import kotlin.collections.ArrayList

class EditProfileFragment: Fragment() {
    var photoFile : File?=null
    var imgUri : Uri?=null
    lateinit var currentPhotoPath: String
    val TEMP_PHOTO_FILE_NAME = "Android/data/com.smileapp.zodiac/temp_photo.jpg"
    var myBitmap: Bitmap? = null
    private var mFileTemp: File? = null
    var bannerShow:BannerShow?=null
    var zodiacMain:ArrayList<ZodiacInfo.ZodiacData.MainData>?=null
    val executor = Executors.newSingleThreadExecutor()
    val handler = Handler(Looper.getMainLooper())
    var items = ArrayList<String>()
    var checkUserDataNull = false
    val binding:FragmentEditprofileBinding by lazy { FragmentEditprofileBinding.inflate(layoutInflater) }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Font().styleText_RSU_BOLD(requireActivity(),binding.textView,32)
        Utils.setTextGradient_Blue(binding.TvZodiac)
        Utils.setTextGradient_Blue(binding.TvName)
        Utils.setTextGradient_Blue(binding.TvGender)
        val state = Environment.getExternalStorageState()
        mFileTemp = if (Environment.MEDIA_MOUNTED == state) {
            File(Environment.getExternalStorageDirectory(), TEMP_PHOTO_FILE_NAME)
        } else {
            File(requireActivity().filesDir, TEMP_PHOTO_FILE_NAME)
        }
        if (mFileTemp!!.exists()) {
            Log.e("mFileTemp exist", mFileTemp!!.path.toString())
            myBitmap = BitmapFactory.decodeFile(mFileTemp!!.path)
            binding.mImguser.setImageBitmap(myBitmap!!)
        }
        Log.e("mFileTemp not exist", mFileTemp!!.path.toString())
        Log.e("Environment",Environment.getExternalStorageDirectory().path)
        Log.e("filesDir",requireActivity().filesDir.path)
        CallData()
        if (Utils.getGENDER()=="Man"){
            binding.rdMan.isChecked = true
        }else if (Utils.getGENDER()=="Woman"){
            binding.rdWoman.isChecked = true
        }
        binding.mImguser.setOnClickListener {
            openAddPhoto()
        }
        binding.mEdittext.setText(Utils.getNameUser())
        if (myBitmap != null) {
            myBitmap!!.recycle()
        }
        binding.btnSave.setOnClickListener {
            Utils.setNameUser(binding.mEdittext.text.toString())
            val gender = binding.rdGender.checkedRadioButtonId
            if (Utils.getNameUser()==""){
                Toast.makeText(MyApplication.getContext(),"กรุณาใส่ชื่อของคุณ", Toast.LENGTH_SHORT).show()
                checkUserDataNull = true
            }else{
                checkUserDataNull = false
                if (gender==binding.rdMan.id){
                    Log.e("man",binding.rdMan.id.toString())
                    val strGender = "Man"
                    Utils.setGENDER(strGender)
                    checkUserDataNull = false
                }else if (gender==binding.rdWoman.id){
                    Log.e("woman",binding.rdWoman.id.toString())
                    val strGender = "Woman"
                    Utils.setGENDER(strGender)
                    checkUserDataNull = false
                }else{
                    Toast.makeText(MyApplication.getContext(),"กรุณาระบุเพศของคุณ", Toast.LENGTH_SHORT).show()
                    checkUserDataNull = true
                }
            }
            if(!checkUserDataNull){
                Utils.setNameAndDateRasi(zodiacMain!![Utils.getRasi()].zodiac_name_thai+" "+zodiacMain!![Utils.getRasi()].zodiac_name_eng+"\n"+zodiacMain!![Utils.getRasi()].zodiac_date)
                Navigation.findNavController(requireView())
                    .navigateUp()
            }
        }
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
            var mImageCaptureUri: Uri?=null
            val state = Environment.getExternalStorageState()
            if (Environment.MEDIA_MOUNTED == state){
                Log.e("takePicture","if")
                Log.e("mFileTemp takePicture", mFileTemp!!.path.toString())
                mImageCaptureUri = FileProvider.getUriForFile(requireActivity(),"com.smileapp.zodiac.provider", mFileTemp!!)
            }else{
                Log.e("takePicture","else")
                mImageCaptureUri = InternalStorageContentProvider().CONTENT_URI
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT,mImageCaptureUri)

            startTakeResult.launch(intent)
        }catch (e:ActivityNotFoundException){
            Log.e("cannot take picture",e.message.toString())
        }
    }
    private fun openGallery(){
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startGalleryResult.launch(photoPickerIntent)

    }

    @Throws(IOException::class)
    fun copyStream(input: InputStream, output: OutputStream) {
        val buffer = ByteArray(1024)
        var bytesRead: Int
        while (input.read(buffer).also { bytesRead = it } != -1) {
            output.write(buffer, 0, bytesRead)
        }
    }
    private fun startCropImage() {
        Log.e("mFileTemp startCropImage", mFileTemp!!.path.toString())
        val intent = Intent(requireActivity(), CropImage::class.java)
        intent.putExtra(CropImage().IMAGE_PATH, mFileTemp!!.path)
        intent.putExtra(CropImage().SCALE, true)
        intent.putExtra(CropImage().ASPECT_X, 3)
        intent.putExtra(CropImage().ASPECT_Y, 3)
        startCropResult.launch(intent)
    }
    private val startCropResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result: androidx.activity.result.ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK){
            val path = result.data?.getStringExtra(CropImage().IMAGE_PATH)
            if (path == null) {
                Log.e("path","null")
            }
            val myBitmap = BitmapFactory.decodeFile(mFileTemp!!.path)
            binding.mImguser.setImageBitmap(myBitmap)
        }

    }
    private val startGalleryResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result: androidx.activity.result.ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK){
            try {
                val inputStream: InputStream = context!!.contentResolver.openInputStream(result.data!!.data!!)!!
                val outputStream = FileOutputStream(mFileTemp)
                copyStream(inputStream, outputStream)
                inputStream.close()
                outputStream.close()
                startCropImage()
            }catch (e:Exception){
                Log.e("REQUEST_CODE_GALLERY","Error while creating temp file",e)
            }

        }

    }
    private val startTakeResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result: androidx.activity.result.ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK){
            Log.e("mFileTemp startTakeResult", mFileTemp!!.path.toString())
            startCropImage()
        }

    }
//    private val startEditResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result: androidx.activity.result.ActivityResult ->
//        if (result.resultCode != Activity.RESULT_OK){
////            Log.e("result","result_ok")
//            return@registerForActivityResult
//        }else{
//            val i =
//            when(i){
//                -> REQUEST_CODE_TAKE_PICTURE{
//
//                }
//            }
//        }
//    }
private fun createImageFile(): File {
    // Create an image file name
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val storageDir: File? = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(
        "JPEG_${timeStamp}_", /* prefix */
        ".jpg", /* suffix */
        storageDir/* directory */
    ).apply {
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = absolutePath
    }
}
private fun dispatchTakePictureIntent() {
    val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    // Create the File where the photo should go
    photoFile = createImageFile()
//        Log.e("file","created")

    // Continue only if the File was successfully created
    if(photoFile != null){
        val photoURI: Uri = FileProvider.getUriForFile(
            requireActivity(),
            "com.smileapp.zodiac.provider", // Your package
            photoFile!!
        )
//            Log.e("photoURI","${photoURI}")
        imgUri = photoURI
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri)
        startTakeResult.launch(takePictureIntent)
    }

//        if (this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
//            // Start the image capture intent to take photo
//            startForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
//        }

}

    fun CallData(){
        executor.execute {
            val zodiacStr = Utils.loadFromAssets(requireActivity())
            items.clear()
            try {
                val zodiacData = Gson().fromJson(zodiacStr, ZodiacInfo::class.java)
                zodiacMain = zodiacData.Data_Zodiac.zodiac_main

            }catch (e: JSONException){
                e.printStackTrace()
            }


            handler.post{
                if (zodiacMain!!.size >0) {
                    for (i in zodiacMain!!.indices) {
                        items.add(zodiacMain!![i].zodiac_name_thai + " " + zodiacMain!![i].zodiac_name_eng + " " + zodiacMain!![i].zodiac_date)
                    }
                    Log.e("CallData",items.toString())
                }
                binding.SpinDate.adapter = SpinnerAdapter(requireActivity(),R.layout.row,items)
                binding.SpinDate.setSelection(Utils.getRasi())
            }
        }

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