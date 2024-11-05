package otoni.luan.galeriapublica;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int RESULT_REQUEST_PERMISSION = 2;
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setContentView(R.layout.activity_main);
        final MainViewModel vm = new ViewModelProvider(this).get(MainViewModel.class);
        bottomNavigationView = findViewById(R.id.btNav);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
        @Override
            public  boolean onNavigationItemSelected(@NonNull MenuItem item){
            vm.setNavigationOpSelected(item.getItemId());
            int itemId = item.getItemId();
            if (itemId == R.id.gridViewOp) {
                GridViewFragment gridViewFragment = GridViewFragment.newInstance();
                setFragment(gridViewFragment);
            } else if (itemId == R.id.listViewOp) {
                ListViewFragment listViewFragment = ListViewFragment.newInstance();
                setFragment(listViewFragment);
            }
            return true;
        }
        });
    }

    void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragContainer, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        checkForPermissions(permissions);
    }

    private void checkForPermissions(List<String> permissions) {
        List<String> permissionsNotGranted = new ArrayList<>();

        for (String permission : permissions){
            if(!hasPermission(permission)) {
                //caso o usuario nao tenha ainda confirmado uma permissao ela eh add a uma lista de permissoes nao confirmadas
                permissionsNotGranted.add(permission);
            }
        }
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if (permissionsNotGranted.size()>0){
                //as permissoes nao concedidas sao requisitadas ao usuário
                requestPermissions(permissionsNotGranted.toArray(new String[permissionsNotGranted.size()]),RESULT_REQUEST_PERMISSION);
            }
            else{
                MainViewModel vm = new ViewModelProvider(this).get(MainViewModel.class);
                int navigationOpSelected = vm.getNavigationOpSelected();
                bottomNavigationView.setSelectedItemId(navigationOpSelected);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,@NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        final List<String> permissionsRejected = new ArrayList<>();
        if (requestCode == RESULT_REQUEST_PERMISSION){;
            //Para cada permissao eh verificado se foi concedida ou nao
            for(String permission : permissions){
                if (!hasPermission(permission)){
                    permissionsRejected.add(permission);
                }
            }
        }

        if(permissionsRejected.size() > 0){
            if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
                if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))){
                    //exibe mensagem ao usuario informando  que a permissao eh necessaria
                    new AlertDialog.Builder(MainActivity.this).setMessage("Para usar essa app é preciso conceder essas permissões").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which){
                            //requisita as permissoes novamente
                            requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), RESULT_REQUEST_PERMISSION);
                        }
                    }).create().show();
                }
            }
        }
        else{
            MainViewModel vm = new ViewModelProvider(this).get(MainViewModel.class);
            int navigationOpSelected = vm.getNavigationOpSelected();
            bottomNavigationView.setSelectedItemId(navigationOpSelected);

        }
    }

    private boolean hasPermission(String permission){
        //verifica se  uma determinada permissão já foi concedida ou não
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            return ActivityCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }



}