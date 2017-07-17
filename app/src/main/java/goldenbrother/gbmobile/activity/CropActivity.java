package goldenbrother.gbmobile.activity;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.theartofdev.edmodo.cropper.CropImageView;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.helper.BitmapHelper;

public class CropActivity extends CommonActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);

        Uri uri = Uri.parse(getIntent().getExtras().getString("uri"));
        final CropImageView civ = (CropImageView) findViewById(R.id.civ);
        civ.setImageUriAsync(uri);
        civ.setAspectRatio(1,1);
        civ.setOnCropImageCompleteListener(new CropImageView.OnCropImageCompleteListener() {
            @Override
            public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {
                showImage(result.getBitmap());
            }
        });
        findViewById(R.id.bbb).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                civ.getCroppedImageAsync();
            }
        });
    }

    private void showImage(final Bitmap bmp) {
        final ImageView iv = new ImageView(this);
        iv.setImageBitmap(bmp);
        alertWithView(iv, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }, null);
    }
}
