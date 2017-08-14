package goldenbrother.gbmobile.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.theartofdev.edmodo.cropper.CropImageView;


import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.helper.BitmapHelper;

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
        Intent intent = new Intent();
        intent.putExtra("path", BitmapHelper.bitmap2PNGFile(this, civ.getCroppedImage()).getAbsolutePath());
        setResult(RESULT_OK, intent);
        finish();
    }
}
