package github.zerorooot.fuckbilibilivote;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

import github.zerorooot.fuckbilibilivote.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        MySharedPreferences biliSp;
        try {
            biliSp = new MySharedPreferences(getBaseContext());
        } catch (SecurityException e) {
            Toast.makeText(this, "Please in LSPosed Check this app and reopen it !", Toast.LENGTH_LONG).show();
            binding.saveButton.setEnabled(false);
            return;
        }

        setSpinner();
        setText(biliSp);

        binding.saveButton.setOnClickListener(v -> {
            biliSp.save("bili_class_name", Objects.requireNonNull(binding.biliClassName.getText()).toString());
            biliSp.save("bili_class_method", Objects.requireNonNull(binding.biliClassMethod.getText()).toString());
            biliSp.save("bili_hd_class_name", Objects.requireNonNull(binding.biliHdClassName.getText()).toString());
            biliSp.save("bili_hd_class_method", Objects.requireNonNull(binding.biliHdClassMethod.getText()).toString());
            Toast.makeText(getApplicationContext(), "save success", Toast.LENGTH_SHORT).show();
        });
    }


    @SuppressLint("SetTextI18n")
    private void setSpinner() {
        binding.biliVersionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                String s = parent.getItemAtPosition(position).toString();
                switch (position) {
                    case 1:
                        //6.73.1
                        binding.biliClassName.setText("tv.danmaku.chronos.wrapper.chronosrpc.remote.RemoteServiceHandler");
                        binding.biliClassMethod.setText("m0");
                        break;
                    case 2:
                        //6.15.1
                        binding.biliClassName.setText("tv.danmaku.chronos.wrapper.ChronosService");
                        binding.biliClassMethod.setText("J6");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        binding.hdVersionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                String s = parent.getItemAtPosition(position).toString();
                if (position == 1) {
                    //1.19.0
                    binding.biliHdClassName.setText("tv.danmaku.chronos.wrapper.rpc.remote.RemoteServiceHandler");
                    binding.biliHdClassMethod.setText("g0");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setText(MySharedPreferences biliSp) {
        binding.biliClassName.setText(biliSp.get("bili_class_name"));
        binding.biliClassMethod.setText(biliSp.get("bili_class_method"));
        binding.biliHdClassName.setText(biliSp.get("bili_hd_class_name"));
        binding.biliHdClassMethod.setText(biliSp.get("bili_hd_class_method"));
    }
}