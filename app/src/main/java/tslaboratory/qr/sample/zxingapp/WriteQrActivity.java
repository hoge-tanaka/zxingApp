package tslaboratory.qr.sample.zxingapp;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.HashMap;

public class WriteQrActivity extends AppCompatActivity {

    private final int REQUEST_CODE_CAMERA = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_qr);

        // バーコードやQRのオプションを設定
        HashMap hints = new HashMap();
        hints.put(EncodeHintType.MARGIN, 0);

        // "hogehoge"というバーコード生成する
        String code = "hogehoge";
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        try {
            Bitmap bitmap = barcodeEncoder.encodeBitmap(code, BarcodeFormat.CODE_128, pxFromDp(300), pxFromDp(100), hints);
            ((ImageView)findViewById(R.id.image_barcode)).setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
            return;
        }

        // QRコード生成
        try {
            // 誤り訂正レベルを設定(QRが多少欠けていても読み取ることができる)
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            Bitmap bitmap = barcodeEncoder.encodeBitmap(code, BarcodeFormat.QR_CODE, pxFromDp(100), pxFromDp(100), hints);
            ((ImageView)findViewById(R.id.image_qrCode)).setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
            return;
        }

        // QR読み取り画面へ遷移する
        // カメラのパーミッションチェックを行う
        findViewById(R.id.button_go_readQrActivity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            // カメラの使用を許可されたらQR読み取り画面へ遷移する
            case REQUEST_CODE_CAMERA:
                Intent intent = new Intent(this, ReadQrActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    // dpからpixelに変更する
    private int pxFromDp(float dp) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
