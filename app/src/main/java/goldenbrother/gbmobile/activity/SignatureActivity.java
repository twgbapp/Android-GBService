package goldenbrother.gbmobile.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.github.gcacace.signaturepad.views.SignaturePad;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.helper.BitmapHelper;

public class SignatureActivity extends CommonActivity implements View.OnClickListener {

    // ui
    private SignaturePad pad;
    // data

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);

        pad = (SignaturePad) findViewById(R.id.pad_signature);
        findViewById(R.id.bt_signature_clear).setOnClickListener(this);
        findViewById(R.id.bt_signature_save).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_signature_clear:
                pad.clear();
                break;
            case R.id.bt_signature_save:
                Bitmap bitmap = pad.getSignatureBitmap();
                bitmap = BitmapHelper.resize(bitmap, BitmapHelper.MAX_WIDTH, BitmapHelper.MAX_HEIGHT);
                Intent intent = new Intent();
                intent.putExtra("bitmap", BitmapHelper.bitmap2Byte(bitmap));
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }
}
