package goldenbrother.gbmobile.activity;

import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import goldenbrother.gbmobile.R;

public class QRReaderActivity extends CommonActivity implements QRCodeReaderView.OnQRCodeReadListener {
    // ui
    private QRCodeReaderView readerView;
    // data
    private boolean scanning = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrreader);
        // ui reference
        readerView = (QRCodeReaderView) findViewById(R.id.qr_view);
        // listener
        readerView.setOnQRCodeReadListener(this);
    }

    @Override
    public void onQRCodeRead(final String text, PointF[] points) {
        if (readerView != null && scanning && text != null) {
            scanning = false;
            Intent intent = new Intent();
            intent.putExtra("text", text);
            setResult(RESULT_OK, intent);
            finishActivity();
        }
    }

    @Override
    public void cameraNotFound() {
        //Toast.makeText(QRReaderActivity.this, "cameraNotFound", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void QRCodeNotFoundOnCamImage() {
        //Toast.makeText(QRReaderActivity.this, "QRCodeNotFoundOnCamImage", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (readerView != null)
            readerView.getCameraManager().startPreview();
    }

    @Override
    protected void onPause() {
        if (readerView != null)
            readerView.getCameraManager().stopPreview();
        super.onPause();
    }

    private void finishActivity() {
        this.finish();
    }
}