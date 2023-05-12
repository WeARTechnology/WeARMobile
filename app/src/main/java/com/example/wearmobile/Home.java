package com.example.wearmobile;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

public class Home extends AppCompatActivity {
    BottomNavigationView bnv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentHolder, new HomeFragment()).commit();


        FloatingActionButton btn = findViewById(R.id.floatingActionButton2);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent tryOnIntent = new Intent(getApplicationContext(), TryOn.class);
                startActivity(tryOnIntent);

            }
        });

        bnv = findViewById(R.id.bottomNavigationView);
        cleanSelected(this);

        bnv.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.carrinho:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentHolder, new CarrinhoFragment()).commit();
                        break;
                    case R.id.produtos:

                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentHolder, new CatalogoFragment()).commit();

                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Clique aqui para pesquisar");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {


                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);

    }

    public void cleanSelected(Activity tela){
        bnv = tela.findViewById(R.id.bottomNavigationView);
        bnv.setSelectedItemId(R.id.camera);
    }
}
