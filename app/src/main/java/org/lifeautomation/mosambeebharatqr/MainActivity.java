package org.lifeautomation.mosambeebharatqr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.mosambee.lib.MosCallback;
import com.mosambee.lib.ResultData;
import com.mosambee.lib.TransactionResult;

import org.lifeautomation.mosambeebharatqr.databinding.ActivityMainBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TransactionResult, CompoundButton.OnCheckedChangeListener, View.OnFocusChangeListener {

    ActivityMainBinding viewBinding;
    boolean is_static = false;
    public static final String TAG = "lu7";
    CountDownTimer countDownTimer;
    String username,password ,amount,refId,date;
    String[] mPermission = {Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
            };

    private static final int REQUEST_CODE_PERMISSION = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = viewBinding.getRoot();
        setContentView(view);
        viewBinding.genQr.setOnClickListener(this);
        viewBinding.checkQr.setOnClickListener(this);
        viewBinding.checkQr2.setOnClickListener(this);
        viewBinding.switchStatic.setOnCheckedChangeListener(this);
        viewBinding.logScrollView.setOnFocusChangeListener(this);
        viewBinding.clear.setOnClickListener(this);
        checkPermission();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.gen_qr:
                fetchData();
                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(amount)) {
                    Toast.makeText(getApplicationContext(), "All fields required !!!", Toast.LENGTH_SHORT).show();
                    return;
                }
                generateBQR(username, password, amount);
                break;
            case R.id.clear:
                viewBinding.resultLog.setText("");
                break;
            case R.id.check_qr:
               fetchData();
                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(amount)||TextUtils.isEmpty(refId)) {
                    Toast.makeText(getApplicationContext(), "All fields required !!!", Toast.LENGTH_SHORT).show();
                    return;
                }

                checkBQR(username,password,amount,date,refId);
                break;
            case R.id.check_qr_2:
                fetchData();
                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(amount)||TextUtils.isEmpty(refId)) {
                    Toast.makeText(getApplicationContext(), "All fields required !!!", Toast.LENGTH_SHORT).show();
                    return;
                }
                startPaymentCheck(username,password,amount,date,refId);
                break;

        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        is_static = b;
    }

    @Override
    public void onResult(ResultData result) {
        String mResult = "\n" + Calendar.getInstance().getTime().toString() + "\n RESULT : " + result.getResult() + "\n" + "RESULT TRANSACTION ID : " + result.getTransactionId() + "\n" + "RESULT TRANSACTION DATA : " + result.getTransactionData() + "\n" + "RESULT TRANSACTION REASON : " + result.getReason() + "\n" + "RESULT TRANSACTION REASON CODE : " + result.getReasonCode() + "\n" + "RESULT TRANSACTION REASON AMOUNT : " + result.getAmount() + "\n";
        viewBinding.resultLog.append(mResult);
        Log.d(TAG, "onResult: ");

        if (result == null) {
            Log.d(TAG, "Barathqr data null");
        } else {
            MosambeeTransactionDataModel mosambeeTransactionDataModel = GsonSingleton.getInstance().fromJson(result.getTransactionData(), MosambeeTransactionDataModel.class);

            if (mosambeeTransactionDataModel.getTransType().equals("BHARAT_QR")) {
                String qr_data=mosambeeTransactionDataModel.getMessage();
                byte[] decodedString = Base64.decode(qr_data.getBytes(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                viewBinding.qrImage.setImageBitmap(decodedByte);

                viewBinding.refId.setText(result.getTransactionId());
            }

        }

    }

    @Override
    public void onCommand(String s) {
        Log.d(TAG, "onCommand: "+s);
        viewBinding.resultLog.append(s+"\n");
    }

    public void generateBQR(String username, String password, String amount) {
        MosCallback mosCallback;
        mosCallback = new MosCallback(this);
        mosCallback.initialise(username, password, this);
        mosCallback.generateBharatQR(Double.parseDouble(amount), "", "", is_static);

    }


    public void checkBQR(String username, String password, String amount,String date ,String referenceId) {
        MosCallback mosCallback;
        mosCallback = new MosCallback(this);
        mosCallback.initialise(username, password, this);
        mosCallback.checkBharatQRStatus(Double.valueOf(amount),date,referenceId,referenceId);
    }


    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if (hasFocus) {
            viewBinding.logScrollView.fullScroll(View.FOCUS_DOWN);
        }
    }



    private void startPaymentCheck(String username, String password, String amount,String date ,String referenceId) {
        countDownTimer = new CountDownTimer(120000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long time = millisUntilFinished / 1000;
                viewBinding.timing.setText(time + " s ");

                if (time % 5 == 0) {
                    checkBQR(username, password, amount, date, referenceId);
                }
            }

            @Override
            public void onFinish() {

            }
        };

        countDownTimer.start();
    }

    public void fetchData(){
        username = viewBinding.username.getText().toString();
        password = viewBinding.password.getText().toString();
        amount = viewBinding.amount.getText().toString();
        refId=viewBinding.refId.getText().toString();

        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
        date= dateFormat.format(Calendar.getInstance().getTime());
    }

    void checkPermission() {

        try {
            if (android.os.Build.VERSION.SDK_INT >= 23) {
                if (ActivityCompat.checkSelfPermission(this, mPermission[0])
                        != PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(this, mPermission[1])
                                != PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(this, mPermission[2])
                                != PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(this, mPermission[3])
                                != PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(this, mPermission[4])
                                != PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(this, mPermission[5])
                                != PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(this, mPermission[6])
                                != PackageManager.PERMISSION_GRANTED||ActivityCompat.checkSelfPermission(this, mPermission[7])
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, mPermission, REQUEST_CODE_PERMISSION);
                    // If any permission aboe not allowed by user, this condition will execute every tim, else your else part will work
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
