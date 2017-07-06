package goldenbrother.gbmobile.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.adapter.GalleryGridAdapter;
import goldenbrother.gbmobile.helper.FileHelper;
import goldenbrother.gbmobile.helper.ToastHelper;
import goldenbrother.gbmobile.model.GalleryModel;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

public class GalleryActivity extends CommonActivity implements View.OnClickListener {

    // check permission
    public static final int PERMISSION_TAKE_PICTURE = 10;
    public static final int PERMISSION_READ_EXTERNAL_STORAGE = 20;
    // request
    public static final int REQUEST_TAKE_PICTURE = 11;
    // extra
    private ArrayList<Uri> list_selectedUri;
    // ui
    private ImageView iv_take_picture, iv_done;
    private GridView gv_gallery;
    // data
    private ArrayList<GalleryModel> list_gallery_item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        // ui reference
        iv_take_picture = (ImageView) findViewById(R.id.iv_gallery_take_picture);
        iv_done = (ImageView) findViewById(R.id.iv_course_news_gallery_done);
        gv_gallery = (GridView) findViewById(R.id.gv_course_news_gallery);
        // listener
        iv_take_picture.setOnClickListener(this);
        iv_done.setOnClickListener(this);
        // init GridView
        list_gallery_item = new ArrayList<>();
        GalleryGridAdapter adapter = new GalleryGridAdapter(this, list_gallery_item);
        gv_gallery.setAdapter(adapter);
        // try to load Gallery
        tryToLoadGallery();
        // init Selected
        list_selectedUri = getIntent().getParcelableArrayListExtra("uri");
        ArrayList<Integer> selected = new ArrayList<>();
        int count = 0;
        for (GalleryModel m : list_gallery_item) {
            if (list_selectedUri.contains(m.getUri())) {
                selected.add(count);
            }
            count++;
        }
        adapter.setSelected(selected);
    }


    private void tryToLoadGallery() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_READ_EXTERNAL_STORAGE);
        } else {
            loadGallery();
        }
    }

    private void loadGallery() {
        final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID, MediaStore.Images.Media.DATE_ADDED};
        Cursor imagecursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, MediaStore.Images.Media.DATE_ADDED + " DESC");
        if (imagecursor != null) {
            int image_column_index = imagecursor.getColumnIndex(MediaStore.Images.Media._ID);
            list_gallery_item.clear();
            for (int i = 0; i < imagecursor.getCount(); i++) {
                imagecursor.moveToPosition(i);
                int id = imagecursor.getInt(image_column_index);
                int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA);
                GalleryModel g = new GalleryModel();
                g.setBitmap(MediaStore.Images.Thumbnails.getThumbnail(getApplicationContext().getContentResolver(), id, MediaStore.Images.Thumbnails.MICRO_KIND, null));
                g.setUri(Uri.fromFile(new File(imagecursor.getString(dataColumnIndex))));
                list_gallery_item.add(g);
            }
            imagecursor.close();
            updateAdapter();
        }
    }

    private void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_TAKE_PICTURE);
        }
    }

    private void updateAdapter() {
        GalleryGridAdapter adapter = (GalleryGridAdapter) gv_gallery.getAdapter();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.iv_gallery_take_picture:
                // Check permission for CAMERA
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_TAKE_PICTURE);
                } else {
                    takePicture();
                }
                break;
            case R.id.iv_course_news_gallery_done:
                ArrayList<Uri> data = new ArrayList<>();
                ArrayList<Integer> selected = ((GalleryGridAdapter) gv_gallery.getAdapter()).getSeleted();
                for (Integer position : selected) {
                    data.add(list_gallery_item.get(position).getUri());
                }
                Intent intent = new Intent();
                intent.putParcelableArrayListExtra("uri",data);
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_TAKE_PICTURE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePicture();
                } else {
                    ToastHelper.t(this, "Permission denied(Camera)");
                }
                break;
            case PERMISSION_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadGallery();
                } else {
                    ToastHelper.t(this, "Permission denied(Read Storage)");
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_TAKE_PICTURE:
                    loadGallery();
                    break;
            }
        }
    }
}
