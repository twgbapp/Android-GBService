package goldenbrother.gbmobile.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.helper.BitmapHelper;
import goldenbrother.gbmobile.helper.FileHelper;

public class CropActivity extends CommonActivity implements View.OnClickListener, CropImageView.OnCropImageCompleteListener {

    // ui
    private CropImageView civ;
    // extra
    private Uri uri;
    private int ratioX, ratioY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);

        // ui reference
        civ = (CropImageView) findViewById(R.id.civ);
        findViewById(R.id.tv_crop_confirm).setOnClickListener(this);
        civ.setOnCropImageCompleteListener(this);

        // extra
        uri = Uri.parse(getIntent().getExtras().getString("uri"));
        ratioX = getIntent().getExtras().getInt("ratioX");
        ratioY = getIntent().getExtras().getInt("ratioY");

        // init
        civ.setImageUriAsync(uri);
        if (ratioX != 0 && ratioY != 0)
            civ.setAspectRatio(ratioX, ratioY);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_crop_confirm:
                civ.getCroppedImageAsync();
                break;
        }
    }

    @Override
    public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {
        File file = BitmapHelper.bitmap2JPGFile(this, result.getBitmap());
        Intent intent = new Intent();
        intent.putExtra("file", file);
        setResult(RESULT_OK, intent);
        finish();
    }
}
