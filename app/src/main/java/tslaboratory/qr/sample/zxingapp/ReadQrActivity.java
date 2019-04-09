package tslaboratory.qr.sample.zxingapp;

import android.content.DialogInterface;
import android.graphics.Point;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.BarcodeView;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;
import com.journeyapps.barcodescanner.Size;
import com.journeyapps.barcodescanner.ViewfinderView;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ReadQrActivity extends AppCompatActivity {

    private DecoratedBarcodeView barcodeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_qr);

        // 端末の横幅を取得しカメラのフォーカスエリアを設定する
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        SettingFocusCamera((int) (size.x * 0.7));

        // QRコードを継続的に読み取る設定
        barcodeView = findViewById(R.id.zxing_barcode_scanner);
        Collection<BarcodeFormat> formats = Arrays.asList(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_128);
        barcodeView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats));
        barcodeView.decodeContinuous(callback);

        // xmlからレーザーの透明度を変更できないのでコードで指定。
        TransparentQrCodeReaderLaser();
    }

    // QRコード読み取り
    private BarcodeCallback callback = new BarcodeCallback() {

        @Override
        public void barcodeResult(BarcodeResult barcodeResult) {

            // 二重読み込み防止のためカメラを止める
            barcodeView.pause();

            // 読み取り結果表示
            AlertDialog.Builder builder = new AlertDialog.Builder(ReadQrActivity.this);
            builder.setMessage(barcodeResult.getText())
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            barcodeView.resume();
                        }
                    });

            builder.show();
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
            // not use
        }
    };

    // hack xmlからレーザーの透明度を変更できないのでコードで指定
    private void TransparentQrCodeReaderLaser() {

        barcodeView.decodeContinuous(callback);
        ViewfinderView viewFinder = barcodeView.getViewFinder();
        Field scannerAlphaField;
        try {
            scannerAlphaField = viewFinder.getClass().getDeclaredField("SCANNER_ALPHA");
            scannerAlphaField.setAccessible(true);
            scannerAlphaField.set(viewFinder, new int[1]);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    // カメラのフォーカスエリアの大きさを画面サイズから設定
    private void SettingFocusCamera(int cameraWidth) {
        BarcodeView barcodeView = findViewById(R.id.zxing_barcode_surface);
        barcodeView.setFramingRectSize(new Size(cameraWidth, cameraWidth));
    }

    @Override
    public void onResume() {
        super.onResume();

        // カメラを起動する
        barcodeView.resume();
    }

    @Override
    public void onPause() {
        super.onPause();

        // カメラを一時停止する
        barcodeView.pause();
    }


}
