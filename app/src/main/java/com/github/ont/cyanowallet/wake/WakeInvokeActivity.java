package com.github.ont.cyanowallet.wake;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.github.ont.cyanowallet.MainActivity;
import com.github.ont.cyanowallet.R;
import com.github.ont.cyanowallet.base.BaseActivity;
import com.github.ont.cyanowallet.scan.activity.ScanWalletInvokeActivity;
import com.github.ont.cyanowallet.scan.activity.ScanWalletLoginActivity;
import com.github.ont.cyanowallet.utils.Constant;
import com.github.ont.cyanowallet.utils.SPWrapper;
import com.github.ont.cyanowallet.utils.ToastUtil;

/**
 * @author zhugang
 */
public class WakeInvokeActivity extends BaseActivity {
    private TextView tvPayer;
    private TextView tvDes;
    private TextView tvConfirm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wake_invoke);
        initData();
    }

    private void initData() {
        if (TextUtils.isEmpty(SPWrapper.getDefaultAddress())) {
            ToastUtil.showToast(this, "You need a wallet");
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        // 获取uri参数
        Intent intent = getIntent();
        String scheme = intent.getScheme();
        Uri uri = intent.getData();
        String str = "";
        if (uri != null) {
            String host = uri.getHost();
            String dataString = intent.getDataString();
            String from = uri.getQueryParameter("from");
            String path = uri.getPath();
            String encodedPath = uri.getEncodedPath();
            String queryString = uri.getQuery();
//            String data = "{\"action\":\"invoke\",\"version\":\"v1.0.0\",\"id\":\"10ba038e-48da-487b-96e8-8d3b99b6d18a\",\"params\":{\"login\":true,\"qrcodeUrl\":\"http://101.132.193.149:4027/qrcode/AUr5QUfeBADq6BMY6Tp5yuMsUNGpsD7nLZ\",\"message\":\"will pay 1 ONT in this transaction\",\"callback\":\"http://101.132.193.149:4027/invoke/callback\",\"invokeConfig\":{\"contractHash\":\"16edbe366d1337eb510c2ff61099424c94aeef02\",\"functions\":[{\"operation\":\"method name\",\"args\":[{\"name\":\"arg0-list\",\"value\":[true,100,\"Long:100000000000\",\"Address:AUr5QUfeBADq6BMY6Tp5yuMsUNGpsD7nLZ\",\"ByteArray:aabb\",\"String:hello\",[true,100],{\"key\":6}]},{\"name\":\"arg1-map\",\"value\":{\"key\":\"String:hello\",\"key1\":\"ByteArray:aabb\",\"key2\":\"Long:100000000000\",\"key3\":true,\"key4\":100,\"key5\":[100],\"key6\":{\"key\":6}}},{\"name\":\"arg2-str\",\"value\":\"String:test\"}]}],\"payer\":\"AUr5QUfeBADq6BMY6Tp5yuMsUNGpsD7nLZ\",\"gasLimit\":20000,\"gasPrice\":500}}}";
//            Intent intent = new Intent("android.intent.action.VIEW");
//            intent.setData(Uri.parse("cyano://com.github.cyano?data="+data));
//            startActivity(intent);
            final String data = Uri.decode(new String(Base64.decode(uri.getQueryParameter("param"), Base64.NO_WRAP)));
            if (TextUtils.isEmpty(data)) {
                Toast.makeText(baseActivity, "params error", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            try {
                JSONObject jsonObject = JSONObject.parseObject(data);
                if (jsonObject.getJSONObject("params") == null) {
                    Toast.makeText(baseActivity, "params error", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                switch (jsonObject.getString("action")) {
                    case "login":
                        Intent intent2 = new Intent(this, ScanWalletLoginActivity.class);
                        intent2.putExtra(Constant.KEY, jsonObject.getJSONObject("params").toJSONString());
                        intent2.putExtra(Constant.ID, jsonObject.getString(Constant.ID));
                        intent2.putExtra(Constant.VERSION, jsonObject.getString(Constant.VERSION));
                        startActivity(intent2);
                        break;
                    case "invoke":
                        Intent intent1 = new Intent(WakeInvokeActivity.this, ScanWalletInvokeActivity.class);
                        intent1.putExtra(Constant.KEY, jsonObject.getJSONObject("params").toJSONString());
                        intent1.putExtra(Constant.ID, jsonObject.getString(Constant.ID));
                        intent1.putExtra(Constant.VERSION, jsonObject.getString(Constant.VERSION));
                        intent1.putExtra(Constant.ADDRESS, SPWrapper.getDefaultAddress());
                        startActivity(intent1);
                        break;
                    default:
                        Toast.makeText(baseActivity, "params error", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(baseActivity, "Json error", Toast.LENGTH_SHORT).show();
            }

            finish();
        } else {
            Toast.makeText(baseActivity, "params error", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

}
