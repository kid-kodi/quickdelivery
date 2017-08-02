package com.alaqos.kodi.opium.activity;

/**
 * Created by Dosso on 7/13/2017.
 */

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.alaqos.kodi.opium.Config;
import com.alaqos.kodi.opium.R;
import com.alaqos.kodi.opium.helper.FileUtils;
import com.alaqos.kodi.opium.helper.SessionManager;
import com.alaqos.kodi.opium.model.Order;
import com.alaqos.kodi.opium.network.ApiClient;
import com.alaqos.kodi.opium.network.FileUploadService;
import com.alaqos.kodi.opium.network.OrderApiInterface;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderEditActivity extends AppCompatActivity {

    // LogCat tag
    private static final String TAG = OrderEditActivity.class.getSimpleName();


    // Camera activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private Uri fileUri; // file url to store image/video

    private Button btnCapturePicture, btnRecordVideo;
    private ImageView imgPreview;
    private Bitmap bitmap;
    private Button btnUpload;
    private String userId;
    private TextInputLayout inputLayoutNumero, inputLayoutAmount;
    private EditText inputNumero, inputAmount;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        String orderId = intent.getStringExtra("_id");
        if (orderId != null) {
            Toast.makeText(getApplicationContext(), "found!", Toast.LENGTH_SHORT).show();
            //finish();
        }

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        inputLayoutNumero = (TextInputLayout) findViewById(R.id.input_layout_numero);
        inputLayoutAmount = (TextInputLayout) findViewById(R.id.input_layout_amount);
        inputNumero = (EditText) findViewById(R.id.input_numero);
        inputAmount = (EditText) findViewById(R.id.input_amount);

        inputNumero.addTextChangedListener(new MyTextWatcher(inputNumero));
        inputAmount.addTextChangedListener(new MyTextWatcher(inputAmount));

        // Changing action bar background color
        // These two lines are not needed

        SessionManager sessionManager = new SessionManager(this);
        userId = sessionManager.getUser().getId();

        imgPreview = (ImageView) findViewById(R.id.imgPreview);
        //btnCapturePicture = (Button) findViewById(R.id.btnCapturePicture);

        /**
         * Capture image button click event
         */
        imgPreview.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // capture picture
                captureImage();
            }
        });

        btnUpload = (Button) findViewById(R.id.btnUpload);

        btnUpload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // capture picture
                uploadFile();
            }
        });

        btnUpload.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);

        // Checking camera availability
        if (!isDeviceSupportCamera()) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Your device doesn't support camera",
                    Toast.LENGTH_LONG).show();
            // will close the app if the device does't have camera
            finish();
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;
        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_numero:
                    validateNumero();
                    break;
                case R.id.input_amount:
                    validateAmount();
                    break;
            }
        }
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    // Validating name
    private boolean validatePic() {
        if (fileUri == null) {
            Toast.makeText(this,"Veuillez prendre une photo",Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    // Validating name
    private boolean validateNumero() {
        if (inputNumero.getText().toString().trim().isEmpty()) {
            inputLayoutNumero.setError(getString(R.string.err_msg_number));
            requestFocus(inputNumero);
            return false;
        } else {
            inputLayoutNumero.setErrorEnabled(false);
        }

        return true;
    }

    // Validating email
    private boolean validateAmount() {
        String email = inputAmount.getText().toString().trim();

        if (email.isEmpty()) {
            inputLayoutAmount.setError(getString(R.string.err_msg_amount));
            requestFocus(inputAmount);
            return false;
        } else {
            inputLayoutAmount.setErrorEnabled(false);
        }

        return true;
    }

    private void uploadFile(){

        if (!validatePic()) {
            return;
        }

        if (!validateNumero()) {
            return;
        }

        if (!validateAmount()) {
            return;
        }

        btnUpload.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        final String inputNumeroTxt = inputNumero.getText().toString();
        final int inputAmountTxt = Integer.parseInt(inputAmount.getText().toString()) ;

        FileUploadService apiService =
                ApiClient.getClient().create(FileUploadService.class);

        //File file = new File(fileUri.getPath());


        MultipartBody.Part body = prepareFilePart("photo", fileUri);
        RequestBody name = createPartFromString("hello, this is description speaking");
        Log.v("img##", body.toString());
        Log.v("img##", name.toString());

        Call<ResponseBody> call = apiService.postImage(body, name);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                // Do Something
                try {
                    String  bodyString = new String(response.body().bytes());
                    JSONObject json = new JSONObject(bodyString);
                    Log.v("img##", bodyString);
                    createOrder(json.getString("picture"), inputNumeroTxt, inputAmountTxt);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                Log.v("img##", t.getMessage());
                btnUpload.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public String Today(){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    private void createOrder(String imgUrl, String inputNumeroTxt, int inputAmountTxt){
        OrderApiInterface apiService =
                ApiClient.getClient().create(OrderApiInterface.class);

        Call<Order> call = apiService.create( userId, inputNumeroTxt, inputAmountTxt, 0, inputAmountTxt, Today(),imgUrl, false);
        call.enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                // clear the inbox
                btnUpload.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(OrderEditActivity.this, "Data saved", Toast.LENGTH_LONG).show();
                OrderEditActivity.this.finish();
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                Toast.makeText(OrderEditActivity.this, "Unable to fetch json: " + t.getMessage(), Toast.LENGTH_LONG).show();
                btnUpload.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @NonNull
    private RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(
                okhttp3.MultipartBody.FORM, descriptionString);
    }

    @NonNull
    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
        // https://github.com/iPaulPro/aFileChooser/blob/master/aFileChooser/src/com/ipaulpro/afilechooser/utils/FileUtils.java
        // use the FileUtils to get the actual file by uri
        File file = FileUtils.getFile(OrderEditActivity.this, fileUri);
        Log.v("file##", file.toString());
        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("image/*"),
                        file
                );

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

    private void previewMedia(Uri filePath) {
        // Checking whether captured media is image or video
        imgPreview.setVisibility(View.VISIBLE);
        // bimatp factory
        BitmapFactory.Options options = new BitmapFactory.Options();

        // down sizing image as it throws OutOfMemory Exception for larger
        // images
        options.inSampleSize = 8;

        bitmap = BitmapFactory.decodeFile(filePath.getPath(), options);

        imgPreview.setImageBitmap(bitmap);
    }

    /**
     * Checking device has camera hardware or not
     * */
    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /**
     * Launching camera app to capture image
     */
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    /**
     * Here we store the file url as it will be null after returning from camera
     * app
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }



    /**
     * Receiving activity result method will be called after closing the camera
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                // successfully captured the image
                // launching upload activity
                previewMedia(fileUri);


            } else if (resultCode == RESULT_CANCELED) {

                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();

            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }

        }
    }

    /**
     * ------------ Helper Methods ---------------------- 
     * */

    /**
     * Creating file uri to store image/video
     */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                Config.IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create "
                        + Config.IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        }else {
            return null;
        }

        return mediaFile;
    }
}